import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import scala.io.Source

object compute_cvpr {
    def main(args: Array[String]): Unit = {
      val bEnv = ExecutionEnvironment.getExecutionEnvironment
      val filePath="F:\\pyProject\\spider\\CVPR_cat2.csv"
      val csv = bEnv.readCsvFile[PapersLog](filePath,ignoreFirstLine = true)
      //println(csv.hashCode)
      val stopEnword = Source.fromFile("src/main/scala/stopwords.txt").getLines()
      val stopWordList = stopEnword.toList

      val counts = csv.flatMap(_.title.split(" "))
        .filter(_.nonEmpty)
        .filter(stopword(_, stopWordList))
        .map((_,1))
        .groupBy(0)
        .sum(1)
        .sortPartition(field = 1, Order.DESCENDING)
        .setParallelism(1)
      counts.writeAsCsv("src/main/scala/CVPR/hotWords19-23_des.csv").setParallelism(1)

      bEnv.execute("batch wordCount")

    }

    def stopword(string: String, stopWordList: List[String]):Boolean = {
      !stopWordList.contains(string.toLowerCase())
    }

    case class
    PapersLog(index:Int,title:String,authors:String)
}
