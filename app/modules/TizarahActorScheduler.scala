package modules

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import javax.inject._

import akka.actor._
import dal.DataAccessLayer
import models.StockItemHistory
import play.api.Logger
import play.api.libs.json.Json
import utils.Utility._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._


class Scheduler @Inject() (val system: ActorSystem,
                           @Named("scheduler-actor") val schedulerActor: ActorRef)
                          (implicit ec: ExecutionContext)
{
  system.scheduler.schedule(
    0.microseconds, 10.minutes, schedulerActor, "update")
  system.scheduler.schedule(
    30.minutes, 30.days, schedulerActor, "clean")
}

@Singleton
class SchedulerActor @Inject() (accessData: DataAccessLayer) extends Actor {
  val logger = Logger(this.getClass)

  def receive = {
    case "update" => updateDB()
    case "clean" => clean()
  }

  def updateDB(): Unit ={
    val df: DateFormat = new SimpleDateFormat("HH:mm")
    val currentTime = df.format(new Date())

    val before: Date = df.parse("01:30")
    val after: Date = df.parse("02:00")
    val toCheck: Date = df.parse(currentTime)

    val isAvailable = before.getTime < toCheck.getTime && after.getTime > toCheck.getTime

    if (!isAvailable) {
      logger.info(new Date() + ", Time is not between 01:30 to 02:00.")
      return
    }
    //Do the thing on database
    logger.info("=== Executing Stock history")
    val cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.DATE, -1)
    logger.info("Yesterday's date = "+ cal.getTime)
    val ts = new Timestamp(cal.getTime.getTime)

    val historyJson = accessData.getStockItems(None).map { res => Json.toJson(res).toString() }
    //FIXME hardcoded shopId
    val history = StockItemHistory(1, ts, Await.result(historyJson, 5.seconds))
    accessData.insertStockHistory(history).foreach(id => println("HistoryId: " + id))

    val i = accessData.updateStockItems()
    logger.info(new Date() + ", updated=>" + i)
  }

  def clean(): Unit ={
    println("cleanup running")
  }
}