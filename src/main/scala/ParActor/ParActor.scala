package ParActor

import java.util

import akka.actor.{Props, _}
import akka.routing.{ActorRefRoutee, Router, SmallestMailboxRoutingLogic}
import akka.routing.SmallestMailboxPool
import scala.reflect._
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import Util._

case class work[E: ClassTag](working_data: E)

case class Result[E: ClassTag](content: E)

case object Return

abstract class Runnable[T: ClassTag] {
  def run(data: T): T
}

class ParActor[T1 <: Runnable[T2] : ClassTag, T2 <: AnyRef : ClassTag](args: AnyRef*) extends Actor {

  import Util.Type._

  val worker = newInstance[T1](args: _*)

  def receive = {
    case work(working_data: T2) => {
      val result = worker.run(working_data)
      val message = Result[T2](result)
      sender().tell(message, context.parent)
    }
  }
}

class Coordinator[T1 <: Runnable[T2] : ClassTag, T2 <: AnyRef : ClassTag](nChild: Int, nFirst: Int, init_args: AnyRef*) extends Actor {

  private val router = context.system.actorOf(Props(new ParActor[T1, T2](init_args: _*)).withRouter(SmallestMailboxPool(nChild)))

  private var counter = 0
  private var counter_finished = 0
  private val result = new util.ArrayList[T2]()
  private var data = new util.ArrayList[T2]()
  private var size = 0
  private var originalSender = context.parent
  private var progress = new ProgressBar(100)
  def receive = {
    case newdata: util.ArrayList[T2] => {

      result.clear()
      size = newdata.size()
      progress = new ProgressBar(size)
      data = newdata
      originalSender = sender()
      for (i <- 0 until nFirst) {
        router ! work[T2](newdata.get(i))
        counter += 1
      }
    }
    case Result(content: T2) => {
      //      println(result.content)
      counter_finished += 1
      progress.add(1)
      result.add(content)
      if (counter < size) {
        router ! work[T2](data.get(counter))
        counter += 1
      }

      if (counter_finished >= size) {
        progress.finish()
        originalSender ! result
      }
    }
    case Return => {
      if (counter_finished < size) {
        println("Results are not ready for collection")
      }
      else {
        sender ! result
      }
    }
  }

}
class ParFor[T1 <: Runnable[T2] : ClassTag, T2 <: AnyRef : ClassTag](nChild: Int, nItem: Int, init_args: AnyRef*) {
  require(nChild > 0, "Number of worker actor must be larger than 0")
  require(nItem > 0, "Number of items to be pre-allocated must be larger than 0")
  val system = ActorSystem("System")
  val master = system.actorOf(Props(new Coordinator[T1, T2](nChild, nItem, init_args: _*)), "master")
  implicit val timeout = Timeout(10 hour)

  def run(arrayList: util.ArrayList[T2]): util.ArrayList[T2] = {
    val future = master ? arrayList
    val result = Await.result(future, timeout.duration).asInstanceOf[util.ArrayList[T2]]
    result
  }

}

class Foo(val String: String) extends Runnable[String] {
  override def run(data: String): String = {
    "This is it"
  }
}



object testing {
  def run(): Unit = {
    val data = new util.ArrayList[String]()
    for (i <- 1 until 100) data.add("test")

    val parfor = new ParFor[Foo, String](4, 10, "lalala")
    println(parfor.run(data))
  }


}