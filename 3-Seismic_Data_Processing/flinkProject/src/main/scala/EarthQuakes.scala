import org.apache.flink.api.scala.{ExecutionEnvironment, _}

object EarthQuakes {
  def main(args: Array[String]): Unit = {
    //第1步：建立执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    //第2步：指定数据文件路径
    val filePath="src/main/data/earthquake.csv"
    //第3步：读取数据
    val csv = env.readCsvFile[EarthQuakesLog](filePath,ignoreFirstLine = true)
    //第3步：对数据集指定转换操作
    //每条数据映射到(年份, 1)
    val yearMap : DataSet[(String,Int)] = csv.map(x=>x.Date).map(yearSplit(_))
    //对每年地震数量统计
    val yearCount = yearMap.groupBy(0).sum(1)

    //每条数据映射到(月份, 年份, 1)
    val monthMap : DataSet[(String,String,Int)] = csv.map(x=>x.Date).map(monthSplit(_))
    //对分组(月份, 年份)统计数量
    val monthCount = monthMap.groupBy(0,1).sum(2)

    //取出(纬度, 经度, 地震等级)
    val location : DataSet[(Double,Double,Double)] = csv.map(x=>(x.Latitude,x.Longitude,x.Magnitude))

    // 第4步：输出结果
    yearCount.writeAsCsv("src/main/data/result/yearCount")
    monthCount.writeAsCsv("src/main/data/result/monthCount")
    location.writeAsCsv("src/main/data/result/location")
    env.execute()
  }
  case class
  EarthQuakesLog(Date:String, Time:String, Latitude:Double, Longitude:Double,
                 Type:String, Depth:Double, Magnitude:Double)

  def yearSplit(x:String) : (String,Int) = {
    val strs = x.split("/")
    return (strs(2),1)
  }
  def monthSplit(x:String) : (String,String,Int) = {
    val strs = x.split("/")
    return (strs(0),strs(2),1)
  }

}