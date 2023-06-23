package org.apache.flink.quickstart

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.table.api.DataTypes
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.descriptors.{Csv, FileSystem, Schema}

import java.util.Calendar

object Homework {
  def main(args: Array[String]) {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)
    val tEnv = StreamTableEnvironment.create(env)
    val schema = new Schema()
      .field("player", DataTypes.STRING())
      .field("Country", DataTypes.STRING())
      .field("Year", DataTypes.INT())
      .field("Game", DataTypes.STRING())
      .field("Gold", DataTypes.INT())
      .field("Silver", DataTypes.INT())
      .field("Bronze", DataTypes.INT())
      .field("Total", DataTypes.INT())
    tEnv.connect(new FileSystem().path("data/olympic-athletes.csv"))
      .withFormat(new Csv().deriveSchema())
      .withSchema(schema)
      .createTemporaryTable("my_table")

    val table = tEnv.from("my_table")
    val athletes = tEnv.toAppendStream[Athlete](table)
    val athletesWithTime= athletes.map(player=>new AthleteWithTime(player.player, player.Country,player.Years,
      player.Game,player.Gold,player.Silver,player.Bronze,player.Total,Calendar.getInstance.getTimeInMillis))
      .assignTimestampsAndWatermarks(new TimeAssigner)

    val peoplePerCountry = athletesWithTime
      .map(player => (player.Country,1))
      .keyBy(0)
      .timeWindow(Time.seconds(5))
      .sum(1)

    val peoplePerGame = athletesWithTime
      .map(player => (player.Game,1))
      .keyBy(0)
      .timeWindow(Time.seconds(5))
      .reduce((a,b)=>(a._1, a._2 + b._2))

    val totalPerCountry = athletesWithTime
      .map(player => (player.Country, player.Total))
      .keyBy(0)
      .timeWindow(Time.seconds(5))
      .aggregate(new MySum)

    peoplePerCountry.writeAsCsv("data/result/people_per_country.csv").setParallelism(1)
    peoplePerGame.writeAsCsv("data/result/people_per_Game.csv").setParallelism(1)
    totalPerCountry.writeAsCsv("data/result/total_per_country.csv").setParallelism(1)
    env.execute()

  }
}

case class Athlete(player:String, Country:String, Years:Int, Game:String, Gold:Int, Silver:Int, Bronze:Int, Total:Int)
case class AthleteWithTime(player:String, Country:String, Years:Int, Game:String, Gold:Int, Silver:Int, Bronze:Int, Total:Int, Time:Long)

class TimeAssigner
  extends BoundedOutOfOrdernessTimestampExtractor[AthleteWithTime](Time.seconds(5)) {

  override def extractTimestamp(r: AthleteWithTime): Long = r.Time

}

class MySum
  extends AggregateFunction[(String, Int), (String, Int), (String, Int)] {
  override def createAccumulator() = {
    ("", 0)
  }
  override def add(in: (String, Int), acc: (String, Int)) = {
    (in._1, in._2 + acc._2)
  }
  override def getResult(acc: (String, Int)) = {
    (acc._1, acc._2)
  }
  override def merge(acc1: (String, Int), acc2: (String, Int)) = {
    null
  }
}