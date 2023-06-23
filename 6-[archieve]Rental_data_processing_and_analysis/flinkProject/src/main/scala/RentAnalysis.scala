import org.apache.flink.api.java.aggregation.Aggregations
import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala._
import org.apache.flink.api.scala.DataSet
import org.apache.flink.core.fs.FileSystem.WriteMode

case class RentData(host_id:Int, neighbourhood:String, latitude:Double, longitude:Double, room_type:String, price:Int,
                    minimum_nights:Int, number_of_reviews:Int, reviews_per_month:Double, calculated_host_listings_count:Int, availability_365:Int)
case class RawData(id:String, name:String, host_id:String, host_name:String, neighbourhood_group:String,
                    neighbourhood:String, latitude:String, longitude:String, room_type:String, price:String,
                    minimum_nights:String, number_of_reviews:String, last_review:String, reviews_per_month:String,
                    calculated_host_listings_count:String, availability_365:String)
case class FilterData(host_id:String, neighbourhood:String, latitude:String, longitude:String, room_type:String,
                      price:String, minimum_nights:String, number_of_reviews:String, reviews_per_month:String,
                      calculated_host_listings_count:String, availability_365:String)

object RentAnalysis {
  def main(args: Array[String]):Unit={
    // --------数据读入--------
    val env = ExecutionEnvironment.getExecutionEnvironment
    val filePath = "src/main/resources/listings-preprocess.csv"
    val rawList = env.readCsvFile[RawData](filePath, ignoreFirstLine=true)
    var list_length = rawList.count()
    println(s"the raw dataset size is $list_length ")
//    rawList.print()

    // --------数据清洗--------
    // 去重
    val distinctDS = rawList.distinct()
    list_length = distinctDS.count()
    println(s"the distinct dataset size is $list_length")
    // 字段筛选
    val filterDS:DataSet[FilterData] = distinctDS.map(x => new FilterData(x.host_id, x.neighbourhood, x.latitude, x.longitude,
      x.room_type, x.price, x.minimum_nights, x.number_of_reviews,x.reviews_per_month, x.calculated_host_listings_count, x.availability_365))
    println("++筛选后的数据++")
    filterDS.first(3).print()

    // 数据统一
    val unifiedDS_0:DataSet[RentData] = filterDS.map(x => new RentData(x.host_id.toInt, x.neighbourhood.split(" / ")(0),
      x.latitude.toDouble, x.longitude.toDouble, x.room_type, x.price.toInt, x.minimum_nights.toInt,
      if(x.number_of_reviews.isEmpty()){0}else{x.number_of_reviews.toInt}, if(x.reviews_per_month.isEmpty()){0}else{x.reviews_per_month.toDouble},
      x.calculated_host_listings_count.toInt, x.availability_365.toInt))
    println("++格式转换后的数据++")
    unifiedDS_0.first(3).print()
    // 异常值处理
    val unifiedDS:DataSet[RentData] = unifiedDS_0.filter(x => (x.price>9 && x.price<99999 && x.availability_365>0&& x.availability_365<367&& x.minimum_nights>0&& x.minimum_nights<367))
    println("++剔除异常值后的数据集大小++")
    println(unifiedDS.count())

    // --------数据统计--------
    // 统计全局价格分布
    val total_price:DataSet[(String, Int)] = unifiedDS.map(x => (if(x.price<=150){"0~150"}else if(x.price>150 && x.price<=300){"150~300"}
      else if(x.price>300 && x.price<=450){"300~450"}else if(x.price>450 && x.price<=600){"450~600"}
      else if(x.price>600 && x.price<=1000){"600~1000"}else{"1000+"}, 1)).groupBy(0).aggregate(Aggregations.SUM, 1)
    println("++价格分布++")
    total_price.print()
    // 统计全局行政区域分布
    val total_region:DataSet[(String, Int)] = unifiedDS.map(x => (x.neighbourhood, 1)).groupBy(0).aggregate(Aggregations.SUM, 1).sortPartition(1, Order.DESCENDING)
    println("++行政区域分布++")
    total_region.print()
    // 统计全局经纬度分布
    val total_coord:DataSet[(Double, Double)] = unifiedDS.map(x => (x.latitude, x.longitude))
    println("++经纬度坐标分布++")
    total_coord.first(3).print()
    // 统计全局最短租期分布
    val total_min_nights:DataSet[(Int, Int)] = unifiedDS.map(x => (x.minimum_nights, 1)).groupBy(0).sum(1).sortPartition(1, Order.DESCENDING)
    println("++最短租期分布++")
    total_min_nights.first(7).print()
    // 统计全局可租时间分布
    val total_availability:DataSet[(Int, Int)] = unifiedDS.map(x => (x.availability_365, 1)).groupBy(0).sum(1).sortPartition(1, Order.DESCENDING)
    println("++可租时间分布++")
    total_availability.first(7).print()
    // 按房型统计分布
    val total_type:DataSet[(String, Int)] = unifiedDS.map(x => (x.room_type, 1)).groupBy(0).aggregate(Aggregations.SUM, 1).sortPartition(1, Order.DESCENDING)
    println("++房型分布++")
    total_type.print()

    // 按行政区域，分组统计房价的平均值、最大值、最小值
    val region_price_0:DataSet[(String, Int, Int, Int, Int)] = unifiedDS.map(x => (x.neighbourhood, x.price, x.price, x.price, 1)).groupBy(0)
      .aggregate(Aggregations.MAX, 1).and(Aggregations.MIN, 2).and(Aggregations.SUM, 3).and(Aggregations.SUM, 4)
    val region_price:DataSet[(String, Int, Int, Int)] = region_price_0.map(x => (x._1, x._2, x._3, (x._4.toDouble / x._5).toInt)).sortPartition(3, Order.DESCENDING)
    println("++按行政区域分组统计房价++")
    region_price.print()

    // --------数据输出--------
    total_price.setParallelism(1).writeAsCsv("src/main/resources/total_price.csv", "\n", ",", WriteMode.OVERWRITE)
    total_region.setParallelism(1).writeAsCsv("src/main/resources/total_region.csv", "\n", ",", WriteMode.OVERWRITE)
    total_coord.setParallelism(1).writeAsCsv("src/main/resources/total_coord.csv", "\n", ",", WriteMode.OVERWRITE)
    total_min_nights.setParallelism(1).writeAsCsv("src/main/resources/total_min_nights.csv", "\n", ",", WriteMode.OVERWRITE)
    total_availability.setParallelism(1).writeAsCsv("src/main/resources/total_availability.csv", "\n", ",", WriteMode.OVERWRITE)
    total_type.setParallelism(1).writeAsCsv("src/main/resources/total_type.csv", "\n", ",", WriteMode.OVERWRITE)
    region_price.setParallelism(1).writeAsCsv("src/main/resources/region_price.csv", "\n", ",", WriteMode.OVERWRITE)
    env.execute()

  }
}
