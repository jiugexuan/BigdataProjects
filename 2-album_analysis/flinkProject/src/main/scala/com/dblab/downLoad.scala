/**
 * Project:flinkProject
 * Package:com.dblab
 * ClassName:downLoad
 * Author:rmc
 * Date:2021/5/31 上午11:00
 * */

package com.dblab


import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala._


/**
 *
 * @param id
 * @param artist_id   作者编号
 * @param album_title 专辑名
 * @param genres      类型
 * @param release     发行时间
 * @param num         专辑中唱片数量
 * @param sales       销售量
 * @param rolling_stone_score
 * @param mtv_score
 * @param music_maniac_score
 */
case class Music(id: Int, artist_id: Int, album_title: String, genres: String, release: String,
                 num: Int, sales: Int, rolling_stone_score: Double, mtv_score: Double, music_maniac_score: Double)

object downLoad {
  // 文件位置
  val DATA_PATH = "src/main/data/albums.csv"
  val HDFS_PATH = "hdfs://localhost:9000/user/rmc/albums.csv"

  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 设置并行度
    env.setParallelism(1)
    // 读取数据
    val data: DataSet[Music] = env.readCsvFile[Music](DATA_PATH, ignoreFirstLine = true)

    releaseNumOfYear(data)
    releaseNumAndSalesOfGenre(data)
    salesOfGenreAndYear(data)
    frequencyOfTitle(data)
    salesAndAverageScoreOfAuthor(data)

    env.execute()
  }

  // 按照作家划分，统计不同作家的所有专辑的销量和在各个体系中的得分
  // 因为作家数量太多，取总销量前100的作家和评分的关系
  def salesAndAverageScoreOfAuthor(data: DataSet[Music]): Unit = {

    // 统计每位作家所有专辑的销量
    val artistSaleDS: DataSet[(Int, Int)] = data.map(
      element => (element.artist_id, element.sales)
    ).groupBy(_._1)
      .reduce((x, y) => (x._1, x._2 + y._2))
      .sortPartition(1, Order.DESCENDING)

    val salesAndAverageScoreOfAuthorDS: DataSet[(Int, Double, Double, Double, Long)] = data.map(
      // 取出原始数据中的artist_id，sales，mtv_score，rolling_stone_score，music_maniac_score
      element => (element.artist_id, element.sales.toDouble, element.mtv_score, element.rolling_stone_score, element.music_maniac_score)
    ).join(artistSaleDS)
      .where(0)
      .equalTo(0) // 将每位作家所有专辑销量的DataSet与每张专辑的评分数据进行连接，连接后DataSet为（作家id，销量，评分1，评分2，评分3，该作家所有专辑总销量）
      .map(
        // 计算每位作家每张专辑的得分，公式为：（该专辑销量/该作家所有专辑销量）* 该专辑的三种不同评分
        // 处理后的DataSet（作家id，评分1在总评分1中的大小，评分2在总评分2中的大小，评分3在总评分3中的大小，该作家所有专辑总销量）
        item => (item._1._1, item._1._2 / item._2._2.toDouble * item._1._3, item._1._2 / item._2._2.toDouble * item._1._4, item._1._2 / item._2._2.toDouble * item._1._5, item._2._2.toDouble))
      .groupBy(_._1)
      .reduce((x, y) => (x._1, x._2 + y._2, x._3 + y._3, x._4 + y._4, x._5)) // 将相同作家的的专辑评分相加，处理后的DataSet（作家id，总评分1，总评分2，总评分3，该作家所有专辑的总销量）
      .map(item => (item._1, item._2.formatted("%.1f").toDouble, item._3.formatted("%.1f").toDouble, item._4.formatted("%.1f").toDouble, item._5.toLong)) // 类型转换
      .sortPartition(4, Order.DESCENDING) // 按照销量进行排序
      .first(50)

    salesAndAverageScoreOfAuthorDS.writeAsCsv("src/main/data/result/salesAndScoreOfAuthor.csv", "\n", ",")
  }

  // 统计每年不同专辑类型的销量
  def salesOfGenreAndYear(data: DataSet[Music]): Unit = {
    val salesOfGenreAndYearDS: DataSet[(String, String, Long)] = data.map(
      element => (element.release, element.genres, element.sales.toLong)
    ).groupBy(0, 1)
      .reduce((x, y) => (x._1, x._2, x._3 + y._3))

    salesOfGenreAndYearDS.writeAsCsv("src/main/data/result/salesOfGenreAndYear.csv", "\n", ",")
  }

  // 统计每年不同专辑发布数量
  def releaseNumOfYear(data: DataSet[Music]): Unit = {
    val releaseNumOfYearDS: DataSet[(String, String, Int)] = data.map(
      element => (element.release, element.genres, 1)
    ).groupBy(0, 1)
      .reduce((x, y) => (x._1, x._2, x._3 + y._3))
      .sortPartition(0, Order.DESCENDING)

    releaseNumOfYearDS.writeAsCsv("src/main/data/result/releaseNumOfYear.csv", "\n", ",")
  }

  // 按照专辑类型统计每种专辑的发行数量,以及销售量
  def releaseNumAndSalesOfGenre(data: DataSet[Music]): Unit = {
    val releaseNumAndSaleOfGenre: DataSet[(String, Long, Int)] = data.map(
      element => (element.genres, element.sales.toLong, 1)
    ).groupBy(_._1)
      .reduce((x, y) => (x._1, x._2 + y._2, x._3 + y._3))
      .sortPartition(1, Order.DESCENDING)

    releaseNumAndSaleOfGenre.writeAsCsv("src/main/data/result/releaseNumAndSalesOfGenre.csv", "\n", ",")
  }

  // 对专辑名进行词频统计
  def frequencyOfTitle(data: DataSet[Music]): Unit = {
    val wordsOfTitle: DataSet[(String, Int)] = data.flatMap(
      element => element.album_title.toLowerCase.split(" ")
    ).map(element => (element, 1))
      .groupBy(_._1)
      .reduce((x, y) => (x._1, x._2 + y._2))
      .sortPartition(1, Order.DESCENDING)

    wordsOfTitle.writeAsCsv("src/main/data/result/frequencyOfTitle.csv", "\n", ",")
  }
}
