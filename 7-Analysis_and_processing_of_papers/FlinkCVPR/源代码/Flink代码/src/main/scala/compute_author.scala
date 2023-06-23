import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._


object compute_author {
  def main(args: Array[String]): Unit = {
    val bEnv = ExecutionEnvironment.getExecutionEnvironment
    val filePath="F:\\pyProject\\spider\\CVPR_cat2.csv"
    val csv = bEnv.readCsvFile[PapersLog](filePath,ignoreFirstLine = true)
    csv.print()

    val counts = csv.flatMap(_.authors.split(";"))
      .filter(_.nonEmpty)
      .map((_,1))
      .groupBy(0)
      .sum(1)
      .sortPartition(field = 1, Order.DESCENDING)
      .setParallelism(1)
    counts.writeAsCsv("src/main/scala/CVPR/authors_all_DES.csv").setParallelism(1)

//    maxAuthor.print()

    bEnv.execute("batch wordCount")

  }

  case class
  PapersLog(Index:Int, title:String,authors:String)
}
