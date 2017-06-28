/**
  * Created by user on 31-5-16.
  */
package transformer

import java.util

import akka.actor._
import java.util.ArrayList
import scala.collection.JavaConversions._


class SentimentActorChildImpl(val data:ArrayList[idText]) {
  private val parser = new SentimentParser()
  private val arrayList = new ArrayList[SentimentCollection]()

  def parseSentiment(): Unit ={
    val sentimentResult = new SentimentCollection();
    for (line<-data){
      sentimentResult.collecttion.add(parser.ParseSentiment(line.text))
    }
    arrayList.add(sentimentResult)
  }
}
class SentimentActorChild(val data:ArrayList[idText]) extends Actor{
  private val parser = new SentimentParser()
  private val arrayList = new ArrayList[SentimentCollection]()

  def receive ={
    case ParseSentiment =>{
      val sentimentResult = new SentimentCollection();
      for (line<-data){
        sentimentResult.collecttion.add(parser.ParseSentiment(line.text))
       // println(parser.ParseSentiment(line.text))
        context.actorSelection("/user/master").tell(CountParsing,sender())
    //println("done")
      }
      arrayList.add(sentimentResult)

    }
  }
}