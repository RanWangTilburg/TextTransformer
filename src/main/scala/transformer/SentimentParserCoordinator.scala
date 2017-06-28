/**
  * Created by user on 31-5-16.
  */
package transformer
import FileIO._
import akka.actor._
import java.util.ArrayList
import scala.collection.JavaConversions._

class SentimentParserCoordinator(val inFile:String, val length:Int, val tres:Double) extends Actor {
  val data = IO.readFileBatchesIdText(inFile, "UTF8", length, tres)
  println("===============Finished Reading File================")
  val size = data.size()
  var lines = 0;
  for (i<-data) lines += i.size()
  println("===============File has " + lines.toString() + " lines===================")
  println("===============Creating " + size.toString()+ " children===================")
  val actors = (1 to size).map(i => context.system.actorOf(Props(new SentimentActorChild(data.get(i-1))), "child" +i))
  println("===============Ready to Parse Features==============")

  var counter = 0;
  def receive = {
    case ParseSentimentMaster =>{
      actors foreach {_ ! ParseSentiment}
    }
    case CountParsing => {
      counter += 1
      //if (counter%50==0) {
        println("Finished Parsing Features of observation " + counter.toString())
      //}

      if (counter == lines){
        println("Finished Parsing Features")
      }
    }
  }
}
