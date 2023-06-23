package Course.work

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala._

case class PokemonInfo(NO:Double,Name:String,Type:String,Total:Int,HP:Int,Attack:Int,Defense:Int,Special_Attack:Int,Special_Defense:Int,Speed:Int)
case class Type(Attack:String,Defense:String,Effectiveness:String,Multiplier:Double)

object Ddata {
  def selectEvolution(name:String): DataSet[(String, String, String)] ={
    val env=ExecutionEnvironment.getExecutionEnvironment
    val InputData3=env.readCsvFile[(String,String,Int,String,String)]("src/main/resources/Pokemon/Evolution.csv",ignoreFirstLine = true)
    val resultData1=InputData3.join(InputData3).where(1).equalTo(0).apply((a,b)=>(a._1,a._2,b._2)).filter(x=>(x._1.equals(name)||x._2.equals(name)||x._3.equals(name)))
    return resultData1
  }

  def main(args: Array[String]): Unit = {
    val env=ExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val inputpath="src/main/resources/Pokemon/Pokemon.csv"
    val InputData1=env.readCsvFile[(Double,String,String,Int,Int,Int,Int,Int,Int,Int)](inputpath,ignoreFirstLine = true)
    val resultData1=InputData1.map(line=>(line._3,1)).groupBy(0).sum(1).sortPartition(1,Order.ASCENDING)
    resultData1.print("Type and Num")

    val resultData2=InputData1.map(line=>(line._2,line._4,line._5,line._6,line._7,line._8,line._9,line._10)).distinct().sortPartition(1,Order.DESCENDING).first(5)
    resultData2.print("TOP5宝可梦")

    val resultData3=InputData1.map(line=>(line._3,line._4,line._5,line._6,line._7,line._8,line._9,line._10,1))
      .groupBy(0).reduce((x,y)=>(x._1,x._2+y._2,x._3+y._3,x._4+y._4,x._5+y._5,x._6+y._6,x._7+y._7,x._8+y._8,x._9+y._9))
      .map(line=>("种族:",line._1,"总值平均",line._2/line._9,"HP平均",line._3/line._9,"Attack均值",line._4/line._9,"Defense均值",line._5/line._9,"特攻均值",line._6/line._9,"特防均值",line._7/line._9,"速度均值",line._8/line._9))
      .sortPartition(1,Order.DESCENDING)
    resultData3.print("各种族值均值")

    val InputData2=env.readCsvFile[(String,String,String,Double)]("src/main/resources/Pokemon/Type.csv",ignoreFirstLine = true)

    val resultData4=InputData2.filter(line=>(line._3.equals("No Effect"))).map(line=>(line._2,line._1,1)).groupBy(0).reduce((x,y)=>(x._1,x._2+y._2,x._3+y._3))
    resultData4.print("免疫情况")
    val resultData4_1=InputData2.map(line=>("source:\""+line._1+"\"","target:\""+line._2+"\"","name:\""+line._3+"\""))
   // resultData4_1.print()


    val name="Bulbasaur"
    val InputData3=env.readCsvFile[(String,String,Int,String,String)]("src/main/resources/Pokemon/Evolution.csv",ignoreFirstLine = true)
    val resultData5=InputData3.join(InputData3).where(1).equalTo(0).apply((a,b)=>(a._1,a._2,b._2)).filter(x=>(x._1.equals(name)||x._2.equals(name)||x._3.equals(name)))
    resultData5.print("进化过程")
    selectEvolution("Bulbasaur").print()

    env.execute()
  }
}