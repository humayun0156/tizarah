import java.time.Clock

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import modules.{Scheduler, SchedulerActor}
import services.{ApplicationTimer, AtomicCounter, Counter}
/**
  * @author Humayun
  */
class Module extends AbstractModule with AkkaGuiceSupport{
  override def configure(): Unit = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    bind(classOf[Counter]).to(classOf[AtomicCounter])

    //akka scheduler
    bindActor[SchedulerActor]("scheduler-actor")
    bind(classOf[Scheduler]).asEagerSingleton()
  }
}
