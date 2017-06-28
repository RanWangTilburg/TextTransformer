package transformer

import akka.actor._
//import kamon.Kamon
import com.softwaremill.debug.DebugMacros._
object Main extends App {
//    val cleaner:TextCleanerFunctionImplClass  = new TextCleanerFunctionImplClass()
//    val s1 =  "Pleas forgiv me if Im not cheerfull today, but everything has gone rong."
//    val s2 = "My computeer is not so good"
//    val s3 = "myy computeer is not so good, but I like it. I think you are stupid thoughh"
//    val r1 = cleaner.SpellCheck(s1)
//    val r2 = cleaner.SpellCheck(s2)
//    val r3 = cleaner.SpellCheck(s3)
//   Kamon.start()

//  val infile = "/home/user/Desktop/data2short.txt"
//  val length = 50
//  val tres = 0.99
//
//  val system = ActorSystem("System")
//  val master = system.actorOf(Props(new SentimentParserCoordinator(infile, length, tres)), "master")
//  master ! ParseSentimentMaster
//  val b = "Stop"

  val infile = "/home/user/Desktop/TextTransformer/data2short.txt"
  val nChild = 10

  val system = ActorSystem("System")
  val master = system.actorOf(Props(new SentimentParserCoordinatorRouter(infile,nChild)),"master")
  master ! ParseSentimentMaster
}