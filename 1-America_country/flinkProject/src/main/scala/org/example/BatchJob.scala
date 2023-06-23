package org.example

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala._

object BatchJob {

  //根据表格内容 定义一个POJOs存取内容
  //字段包括county：郡县名、state：州名、FIPS Code：区域编码、population：人口数量、area：面积、Density：人口密度
  case class countyInfo(county:String,state:String,FIPS:String,population:Int,area:Int,Density:Int)

  def main(args: Array[String]): Unit = {
    val inputPath = "src/main/resources/counties.csv"
    val relativePath = "src/main/resources/"
    getStateInformation(inputPath,relativePath+"state.csv")//计算州信息
    getStateTop10(relativePath)//计算美国各数据top10的州
    getMaxAreaCountyInState("Alaska",inputPath)
  }



  //求一个州里面积最大的郡县
  def getMaxAreaCountyInState(StateName:String,inputPath:String) = {
    // 设置批执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    // 得到输入数据
    val input = env.readCsvFile[countyInfo](inputPath,ignoreFirstLine = true)//从CSV中读取数据，并忽略第一行属性字段

    // 统计该州面积最大的郡
    val county = input.filter( _.state == StateName)//过滤数据，只留下该州的信息
        .map(x => (x.area,x.county,x.state))//保留有用字段
        .maxBy(0)//取出面积最大的元组
        .map(x => x._2)//只保留名称字段

    // 执行并输出结果
    println(StateName+"面积最大的县是")
    county.print()
  }

  //统计州信息
  def getStateInformation(inPath:String,outPath:String)={
    // 设置批执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    // 得到输入数据
   val input = env.readCsvFile[countyInfo](inPath,ignoreFirstLine = true)//从CSV中读取数据，并忽略第一行属性字段

    // 统计每州的数据
    val state = input.map(x => (x.population,x.area,x.state)) //去掉一些无用的字段，比如FIPS Code
        .groupBy(2)                                   //按州来聚合数据
        .reduce((x,y)=>(x._1+y._1,x._2+y._2,x._3))            //计算每州的总人口，和总面积
        .map((x)=>(x._1,x._2,(x._1.asInstanceOf[Float]/x._2.asInstanceOf[Float]).formatted("%.2f"),x._3))//计算每州的人口密度保留两位小数
        .setParallelism(1)

    // 执行并输出结果
    state.writeAsCsv(outPath)
    state.print()
  }

  //计算统计数据top10的州
  def getStateTop10(relativePath: String) = {
    // 设置批执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    // 得到输入数据
    val input:DataSet[(Int,Int,Float,String)] = env.readCsvFile(relativePath+"state.csv")

    //计算每种数据的top10
    //1.计算人口总数top10
    val population = input.map((x)=>(x._1,x._4)) //取出字段
      .sortPartition(0,Order.DESCENDING)  //按人数降序排序
      .setParallelism(1)
      .first(10)  //取排序后前10的州

    //2.计算面积top10
    val area =  input.map((x)=>(x._2,x._4)) //取出字段
      .sortPartition(0,Order.DESCENDING)  //按面积降序排序
      .setParallelism(1)
      .first(10)  //取排序后前10的州

    //3.计算人口密度top10
    val density =  input.map((x)=>(x._3,x._4)) //取出字段
      .sortPartition(0,Order.DESCENDING)  //按人口密度降序排序
      .setParallelism(1)
      .first(10)  //取排序后前10的州

    // 执行并输出结果
    population.writeAsCsv(relativePath+"populationTop10.csv")
    population.print()
    area.writeAsCsv(relativePath+"areaTop10.csv")
    area.print()
    density.writeAsCsv(relativePath+"densityTop10.csv")
    density.print()
  }

}
