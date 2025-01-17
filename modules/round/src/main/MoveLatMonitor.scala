package lila.round

import akka.actor.Scheduler
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration._

private object MoveLatMonitor {

  private case class Latency(totalMicros: Long = 0, count: Int = 0) {
    def record(micros: Int) = copy(totalMicros + micros, count + 1)
    def average             = (totalMicros / count.atLeast(1)).toInt
  }
  private val latency = new AtomicReference(Latency())

  def record(micros: Int): Unit = latency.getAndUpdate(_ record micros).unit

  object wsLatency {
    var latestMillis     = 0
    def set(millis: Int) = latestMillis = millis
  }

  def start(scheduler: Scheduler)(implicit ec: scala.concurrent.ExecutionContext) =
    scheduler.scheduleWithFixedDelay(10 second, 2 second) { () =>
      val full = latency.getAndSet(Latency()).average + wsLatency.latestMillis * 1000
      lila.common.Bus.publish(lila.hub.actorApi.round.Mlat(full), "mlat")
    }
}
