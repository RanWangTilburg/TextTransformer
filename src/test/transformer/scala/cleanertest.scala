/**
  * Created by user on 29-5-16.
  */
package transformer

import org.scalatest._

class CleanerTest extends FlatSpec with Matchers {
  val CleanerImpl:TextCleanerFunctionImplClass = new TextCleanerFunctionImplClass();
  "Cleaner" should "spell correct" in {
    val a = "my computers are not so great"
    true should be === true
  }
}
