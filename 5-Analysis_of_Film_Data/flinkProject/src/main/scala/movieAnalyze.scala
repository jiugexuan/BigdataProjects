import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, _}

import scala.collection.mutable.ArrayBuffer

object movieAnalyze {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment //建立执行环境
    env.setParallelism(1)


    /*读取HDFS上的数据集*/
    val inputHDFS: DataSet[movie] = env.readCsvFile[movie]("hdfs://localhost:9000/user/hadoop/douban_2.csv", ignoreFirstLine = true)
   
   /*统计每个年份的电影数*/
    val yearL = inputHDFS.map(x => (x.year, 1) ) //抽取每条电影记录的年份
    val yearNum = yearL.groupBy(0).sum(1) //按年份分组并计数
    yearNum.writeAsCsv("output/yearNum.csv") //将结果写至本地文件

    /*统计每个评分区间的电影数*/
    val ratingNum_L = new ArrayBuffer[(String, Int)]()  //先建立数组用于保存结果
    for(i <- 1 to 9){   //对1-9分区间的电影数目进行循环统计
      val tmp = inputHDFS.filter(_.rating.toString.startsWith(i.toString + '.')) //筛选出[i, i+1)分区间的电影
      ratingNum_L.append((i.toString, tmp.collect().size)) //将本区间段的电影数目保存到数组
    }
    val ratingNum = env.fromCollection(ratingNum_L)  //将数组类型转换成DataSet类型
    ratingNum.writeAsCsv("output/ratingNum.csv")  //将结果写至本地文件

    /*统计每个制片地区的电影数*/
    val filmCountry_L = inputHDFS.map(x => (x.country.split('/')(0), 1)) //将首要制片地区提取出来
    val filmCountryNum = filmCountry_L.groupBy(0).sum(1)  //对制片地区分组计数
    val filmCountryNum_Sort = filmCountryNum.sortPartition(1, Order.DESCENDING)  //对统计结果降序排序
    filmCountryNum_Sort.writeAsCsv("output/filmCountryNum_Sort.csv") //将结果写至本地文件

    /*统计每个制片地区电影的平均评分*/
    val filmCountry_rating_c = inputHDFS.map(x => (x.country.split('/')(0), x.rating)) //提取制片地区和评分
    val filmCountry_rating_sum = filmCountry_rating_c.groupBy(0).sum(1) //统计制片地区的电影总评分
    val filmCountry_ratingAverage = filmCountry_rating_sum.join(filmCountryNum).where(0).equalTo(0){ //将制片地区的总评分表和总电影数表合并
      (left, right) => (left._1, left._2 / right._2)  //用总评分/总电影数计算平均得分
    }
    val filmCountry_ratingAverage_Sort = filmCountry_ratingAverage.sortPartition(1, Order.DESCENDING)  //降序排序
    filmCountry_ratingAverage_Sort.writeAsCsv("output/filmCountry_ratingAverage_Sort.csv") //将结果写至本地文件


    /*统计每种类型的电影数*/
    val filmGenres = inputHDFS.flatMap(x => x.genre.split('/'))   //将每部电影的所属的多个类型展开
    val filmGenres_L = filmGenres.collect().toList  //所属类型由DataSet转List,方便后续操作

    val genres_L = filmGenres.map(x => (x, 1))  
    val genres_num = genres_L.groupBy(0).sum(1)  //对所属类型计数得到结果
    val genres_num_Sort = genres_num.sortPartition(1, Order.DESCENDING)  //降序排序
    genres_num_Sort.writeAsCsv("output/genres_num_Sort.csv")  //将结果写至本地文件


    /*统计每种类型电影的平均评分*/
    val filmGenre_num = inputHDFS.map(x => x.genre.split('/').size)  //记录每部电影的所属类型有多少个
    val filmGenre_num_L =  filmGenre_num.collect().toList  //所属类型数目由DataSet转List,方便后续操作

    val filmRating = inputHDFS.map(x => x.rating)  //记录每部电影的评分
    val filmRating_L = filmRating.collect().toList  //评分由DataSet转List,方便后续操作

    val genresRatings = new ArrayBuffer[(String, Double)]()  //先建立数组用以记录展开后每个类型的评分
    var idx = 0  //指针
    for(i <- 0 until filmRating_L.size){  //对每一部电影，按照所属类型的个数将评分分配若干次至展开的类型数组
      val the_genre_num = filmGenre_num_L(i)  //提取第i部电影的类型数目
      val the_rating = filmRating_L(i)  //提取第i部电影的评分
      for(j <- 0 until the_genre_num){  //循环分配评分给展开的类型数组
        genresRatings.append((filmGenres_L(idx + j), the_rating))
      }
      idx = idx + the_genre_num  //更新指针
    }

    val genresRatings_Dataset = env.fromCollection(genresRatings)  //将得到的数组转DataSet
    val genreRating_sum = genresRatings_Dataset.groupBy(0).sum(1)  //分组计算每种类型电影的总评分

    val genres_ratingAverage = genreRating_sum.join(genres_num).where(0).equalTo(0){  //将每种类型的总评分表和总电影数表合并
      (left, right) => (left._1, left._2 / right._2)  //用总评分/总电影数计算平均评分
    }
    val genres_ratingAverage_Sort = genres_ratingAverage.sortPartition(1, Order.DESCENDING)  //降序排序
    genres_ratingAverage_Sort.writeAsCsv("output/genres_ratingAverage_Sort.csv") //将结果写至本地文件
    
    env.execute("Write Results to local files!") //执行写入操作

  }
}


case class movie(id:Long, name:String, year:Int, rating:Double, ratingsum:Long, genre:String, country:String)  //建立movie类来接收来自文件的记录