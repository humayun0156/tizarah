package modules

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject._

import akka.actor._
import dal.DataAccessLayer

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


class Scheduler @Inject() (val system: ActorSystem,
                           @Named("scheduler-actor") val schedulerActor: ActorRef)
                          (implicit ec: ExecutionContext)
{
  system.scheduler.schedule(
    0.microseconds, 20.minutes, schedulerActor, "update")
  system.scheduler.schedule(
    30.minutes, 30.days, schedulerActor, "clean")
}

@Singleton
class SchedulerActor @Inject() (accessData: DataAccessLayer) extends Actor {
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
      println(new Date() + ", Time is not between 01:30 to 02:00.")
      return
    }
    //Do the thing on database
    println("Executing query=====")
    val i = accessData.updateStockItems()
    println(new Date() + ", updated=>" + i)
  }

  def clean(): Unit ={
    println("cleanup running")
  }
}