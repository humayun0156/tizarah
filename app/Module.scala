import java.time.Clock

import com.google.inject.AbstractModule
import services.{ApplicationTimer, AtomicCounter, Counter}
/**
  * @author Humayun
  */
class Module extends AbstractModule{
  override def configure(): Unit = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    bind(classOf[Counter]).to(classOf[AtomicCounter])
  }
}
