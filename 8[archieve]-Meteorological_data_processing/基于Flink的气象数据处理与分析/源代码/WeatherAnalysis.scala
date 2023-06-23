package cn.edu.xmu.dblab

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.api.java.aggregation.Aggregations
import org.apache.flink.api.common.operators.Order
import org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE


case class Record(province:String, city_number:Int, city:String, time:String, temperature:Double, humidity:Double, pressure:Double)
case class Record_temperature(province:String, city_number:Int, city:String, time:String, temperature:Double)
case class Record_humidity(province:String, city_number:Int, city:String, time:String, humidity:Double)
case class Record_pressure(province:String, city_number:Int, city:String, time:String, pressure:Double)

object WeatherAnalysis {
 def main(args: Array[String]): Unit = {
    //建立执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    //读取文件
    val file_path = "/home/fanghan/bigdata/getdata/data.csv"
    val csv_data = env.readCsvFile[Record](file_path, ignoreFirstLine = true)
    //存放未测量的数据集合，未测量的值为9999.0，故大于9998则认为是未测量的值
    var remove_set = csv_data.filter(x=>x.temperature>9998).map(x=>x.city_number).distinct()
    //转化为ArrayList
    var remove_lst = remove_set.collect()
    //将未测量温度的城市去掉，然后用Record_temperature封装起来
    val data_sort_by_temperature = csv_data.filter(x=>(!remove_lst.contains(x.city_number))).map(x=>Record_temperature(x.province, x.city_number, x.city, x.time, x.temperature))
    //按照城市号对城市分组后对每组的城市温度进行求和，再对每个城市的温度除以24得到平均温度，然后进行排序并选取前20个平均温度最高的城市
    val temperature_max20 = data_sort_by_temperature.groupBy("city_number").aggregate(Aggregations.SUM, "temperature")
                                                                        .map(x=>Record_temperature(x.province, x.city_number, x.city, x.time, x.temperature/24))
                                                                        .sortPartition("temperature", Order.DESCENDING)
                                                                        .first(20)
   //得到平均温度最低的20个城市
   val temperature_min20 = data_sort_by_temperature.groupBy("city_number").aggregate(Aggregations.SUM, "temperature")
                                                                        .map(x=>Record_temperature(x.province, x.city_number, x.city, x.time, x.temperature/24))
                                                                        .sortPartition("temperature", Order.ASCENDING)
                                                                        .first(20)
   //写入文件中
   temperature_max20.writeAsCsv("/home/fanghan/bigdata/getdata/temperature_max20.csv", writeMode=OVERWRITE)
   temperature_min20.writeAsCsv("/home/fanghan/bigdata/getdata/temperature_min20.csv", writeMode=OVERWRITE)
   //找到未测量湿度的城市
   remove_set = csv_data.filter(x=>x.humidity>9998).map(x=>x.city_number).distinct()
   //转换为ArrayList
   remove_lst = remove_set.collect()
   //将未测量温度的城市去掉，然后用Record_humidity封装起来
   val data_sort_by_humidity = csv_data.filter(x=>(!remove_lst.contains(x.city_number))).map(x=>Record_humidity(x.province, x.city_number, x.city, x.time, x.humidity))
   //得到平均湿度最低的20个城市
   val humidity_min20 = data_sort_by_humidity.groupBy("city_number").aggregate(Aggregations.SUM, "humidity")
                                                                     .map(x=>Record_humidity(x.province, x.city_number, x.city, x.time, x.humidity/24))
                                                                     .sortPartition("humidity", Order.ASCENDING)
                                                                     .first(20)
   //写入文件
   humidity_min20.writeAsCsv("/home/fanghan/bigdata/getdata/humidity_min20.csv", writeMode=OVERWRITE)
   //找到未测量气压的城市
   remove_set = csv_data.filter(x=>x.pressure>9998).map(x=>x.city_number).distinct()
   //转换为ArrayList
   remove_lst = remove_set.collect()
   //将未测量气压的城市去掉，然后用Record_humidity封装起来
   val data_sort_by_pressure = csv_data.filter(x=>(!remove_lst.contains(x.city_number))).map(x=>Record_pressure(x.province, x.city_number, x.city, x.time, x.pressure))
   //得到平均气压最高的20个城市
   val pressure_max20 = data_sort_by_pressure.groupBy("city_number").aggregate(Aggregations.SUM, "pressure")
                                                                     .map(x=>Record_pressure(x.province, x.city_number, x.city, x.time, x.pressure/24))
                                                                     .sortPartition("pressure", Order.DESCENDING)
                                                                     .first(20)
   //写入文件
   pressure_max20.writeAsCsv("/home/fanghan/bigdata/getdata/pressure_max20.csv", writeMode=OVERWRITE)
   pressure_max20.print()
 }
}
//       rm -r target
//      /home/fanghan/local/maven/bin/mvn package
//      /home/fanghan/local/flink/bin/flink run --class cn.edu.xmu.dblab.WeatherAnalysis ./target/weatheranalysis-1.0.jar
