/**
  * Created by user on 31-5-16.
  */
package transformer

import java.util
import java.util.ArrayList
import akka.actor._
import scala.collection.mutable.Map
import scala.collection.JavaConversions._

class SentimentActorChildRouter extends Actor {
  private val data = new ArrayList[idText]()
  private val parser = new SentimentParser()
//  private val arrayList = new ArrayList[SentimentCollection]()
  private val sentimentResult = new ArrayList[idSentimentCollection]()

  private var featureCount:Map[String, Int] = Map()
  private var featureNames:ArrayList[String] = new util.ArrayList[String]()
  def receive ={
    case idText:idText => {
      data.add(idText)
      sentimentResult.add(parser.ParseSentiment(idText))
      context.actorSelection("/user/master").tell(CountParsing,sender())
    }
    case CountFeature => {
      def getString(s: WordSentiment) = s.word

      for (ob <- sentimentResult) {
        val tempMap = ob.result.groupBy(getString).mapValues(_.size)
        //println(tempMap)
        featureCount = featureCount ++ tempMap.map { case (k, v) => k -> (v + featureCount.getOrElse(k, 0)) }
        context.actorSelection("/user/master").tell(CountFeatureProgress, sender())
      }
    }
    case GimmeFeatures => {
        context.actorSelection("/user/master").tell(FeatureCount(featureCount),sender())
    }
    case ParseFinalOutput(features) => {
      //println("I am here")
      featureNames  = features
      for (ob <- sentimentResult){
//        println(getStringFromSentiment(ob))
        context.actorSelection("/user/master").tell(FinalOutputResult(getStringFromSentiment(ob)),sender())
      }

    }
    }

  private def getStringFromSentiment(ob: idSentimentCollection): String = {
    val StringToSendArray = initArrayString
    StringToSendArray(0) = ob.id.toString
    for (result <- ob.result) {
      for (i <- 0 until featureNames.size()) {
        if (featureNames(i)==result.word){
          if  (result.sentiment > 2) {
            StringToSendArray(3 * i + 1) = 1.toString
          } else if (result.sentiment == 2) {
            StringToSendArray(3 * i + 2) = 1.toString
          } else if (result.sentiment < 2) {
            StringToSendArray(3 * i + 3) = 1.toString
          }
        }
      }
    }
    return joinArrayString(StringToSendArray)
  }

  private def getStringFromSentimentv2(ob: idSentimentCollection): String={
    val StringToSendArray = initArrayString
    StringToSendArray(0) = ob.id.toString

    for (result <- ob.result){
      for (i <- 0 until featureNames.size()){
        if (result.sentiment > 2) {
          StringToSendArray(i+1) = 1.toString
        }
        else if (result.sentiment==2){
          StringToSendArray(i+1) = 0.toString
        }
        else if (result.sentiment<2){
          StringToSendArray(i+1) = (-1).toString
        }
      }
    }
    return joinArrayString(StringToSendArray)
  }

  private def initArrayString: Array[String] ={
    val size = 1 + 3*featureNames.size()
    val result = new Array[String](size)
//    result foreach(_=0.toString)
    for (i <- 0 until result.size){
      result(i)=0.toString
    }
    result
  }

  private def initArrayStringV2: Array[String] ={
    val size = 1 + featureNames.size()
    val result = new Array[String](size)
    for (i <- 0 until result.size){
      result(i) = 0.toString
    }
    result
  }
  private def joinArrayString(in : Array[String]): String ={
    val temp = new StringBuilder(in(0))
    for (i <- 1 until in.size){
      temp.append(",")
      temp.append(in(i).toString)
    }
    return temp.toString()
  }


}