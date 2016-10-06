package services

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

/**
  * @author Humayun
  */
trait Counter {
  def nextCount(): Int
}

@Singleton
class AtomicCounter extends Counter {
  private val atomicCounter = new AtomicInteger()

  override def nextCount(): Int = atomicCounter.getAndIncrement()
}
