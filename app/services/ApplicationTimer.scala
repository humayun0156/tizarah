package services

import java.time.{Clock, Instant}

import com.google.inject.Inject
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * @author Humayun
  */
class ApplicationTimer @Inject() (clock: Clock, appLifeCycle: ApplicationLifecycle) {

  // This code is called when the application starts.
  private val start: Instant = clock.instant
  Logger.info(s"ApplicationTimer Demo: Starting application at $start")

  appLifeCycle.addStopHook { () =>
    val stop: Instant = clock.instant
    val runningTime: Long = stop.getEpochSecond - start.getEpochSecond
    Logger.info(s"Application Demo: Stopping application at ${clock.instant} after ${runningTime}s")
    Future.successful(())
  }
}
