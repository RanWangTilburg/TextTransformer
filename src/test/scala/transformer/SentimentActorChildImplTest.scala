package transformer

import org.scalatest._
import java.util.ArrayList
/**
  * Created by user on 31-5-16.
  */
class SentimentActorChildImplTest extends FlatSpec with Matchers {
  val data = new ArrayList[idText]()
  for (i <- 1 to 20){
    data.add(new idText("1","I am disappointed at the movie"))
  }
  val child = new SentimentActorChildImpl(data)

  "SentimentActorChildImpl" should "Do something" in {
    //For debugging purpose
    child.parseSentiment()
    val a = "Stop"
    true should be === true
  }
}
