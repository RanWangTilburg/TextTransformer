/**
  * Created by user on 31-5-16.
  */

package transformer

import java.util

import FileIO.IO
import akka.actor._
import akka.routing.{ActorRefRoutee, Router, SmallestMailboxRoutingLogic}
import scala.collection.mutable.Map
import scala.collection.JavaConversions._

class SentimentParserCoordinatorRouter(infile: String, nChild: Int) extends Actor {
  private var data = IO.readFileIdText(infile)
  private val actors = (1 to nChild).map(i => context.system.actorOf(Props(new SentimentActorChildRouter()), "child" + i));

  private val routees = actors map ActorRefRoutee
  private val router = Router(SmallestMailboxRoutingLogic(), routees)

  private var no = 0
  private var starts = 50
  private var featureCountResult: Map[String, Int] = Map()
  private var featureToUse: Seq[(String, Int)] = List()
  private var featureCache: Seq[(String, Int)] = List()
  private var featureNames = new util.ArrayList[String]()
  private var outData = new util.ArrayList[String]()
  //println("Start")

  def readNewData(infile: String): Unit = {
    data = IO.readFileIdText(infile)
  }

  def setNumberToStart(newNumber: Integer): Unit = {
    starts = newNumber
  }

  def receive = {
    case ParseSentimentMaster => {
      for (i <- 0 until starts) {
        router.route(data.get(i), sender)
      }
    }
    case CountParsing => {
      no += 1
      println("Finished Parsing Features of Observation " + no.toString)
      if (no + starts - 1 < data.size()) {
        router.route(data.get(no + starts - 1), sender)
        //        println(no+starts-1)
      }

      if (no == data.size()) {
        println("Finished Parsing Features")
        no = 0;
        self ! SummarizeFeature
      }
    }
    case CountFeatureProgress => {
      no += 1
      println("Finished Summarizing Features of Observation " + no.toString)
      if (no == data.size()) {
        println("Finished Summarizing Features")
        no = 0
        actors foreach {
          _ ! GimmeFeatures
        }
      }
    }

    case SummarizeFeature => {
      actors foreach {
        _ ! CountFeature
      }
    }
    case FeatureCount(result) => {
      no += 1
      featureCountResult = featureCountResult ++ result.map { case (k, v) => k -> (v + featureCountResult.getOrElse(k, 0)) }
      if (no == nChild) {
        no = 0
        println("Received all the feature data from Children")
        featureToUse = featureCountResult.toSeq.sortBy(_._2).reverse
        featureCache = featureToUse
        println("The features are ")
        println(featureToUse)
        //        self ! ParseFinalResult
      }

    }
    case RescreenFeature => {
      featureToUse = featureCache
    }
    case PrintFeatures => {
      println(featureToUse)
    }
    case ScreenFeature(filter) => {
      featureToUse = filter(featureToUse)
    }
    case ParseFinalResult => {
      initFinalOutput
      outData.add(getHeader)
    }
    case FinalOutputResult(result) => {
      printProgressFinalResultCollection
      //      println(result)
      outData.add(result)
      if (no == data.size()) println("Finished!!!!!") //;println(outData)
    }
    case WriteFinalOutput(outfile) => {
      IO.writeFile(outfile, outData, "UTF8")
    }

  }

  private def printProgressFinalResultCollection: Unit = {
    no += 1;
    println("Finished Collecting Result of Observation " + no.toString)
  }

  private def getHeader: String = {
    val temp = new StringBuilder("id")
    featureNames foreach { case a => temp.append("," + a + "Pos"); temp.append("," + a + "Neutral"); temp.append("," + a + "Neg") }
    return temp.toString()
  }

  private def initFinalOutput: Unit = {
    no = 0
    featureNames = new util.ArrayList[String]() //Reset the results
    featureToUse foreach { case (k, _) => featureNames.add(k) }
    println("Final Features to Use Are")
    println(featureNames)
    outData = new util.ArrayList[String]() //Reset the output data
    actors foreach {
      _ ! ParseFinalOutput(featureNames)
    }
  }
}