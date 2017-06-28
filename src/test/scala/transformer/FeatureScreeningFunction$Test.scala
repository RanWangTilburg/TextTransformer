package transformer

import org.scalatest._
/**
  * Created by user on 7-6-16.
  */
class FeatureScreeningFunction$Test extends FlatSpec with Matchers {
  val test1 = Seq(("My", 1000), ("This is not here", 100))
  "testDropFeature" should "Drop the a feature with a specific name " in {
    val dropThisIsNotHere = FeatureScreeningFunction.dropFeature(_:Seq[(String, Int)],"This is not here")
    assert(dropThisIsNotHere(test1)===Seq(("My", 1000)))

  }

  "testGetFeaturesByOrder" should  "Take the first few features" in {
    assert(FeatureScreeningFunction.getFeaturesByOrder(test1, 1)===Seq(("My", 1000)))
  }

  "testGetFeatureByThreshold" should "Drop features below certain threshold" in {
    assert(FeatureScreeningFunction.getFeatureByThreshold(test1, 200)===Seq(("My", 1000)))
  }

}
