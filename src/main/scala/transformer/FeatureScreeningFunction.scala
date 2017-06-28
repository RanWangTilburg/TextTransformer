/**
  * Created by user on 7-6-16.
  */

package transformer
object FeatureScreeningFunction{
  def getFeatureByThreshold(Seq:Seq[(String, Int)], thres:Int) = {
    Seq.filter {case (_,v) => v>thres}
  }
  def getFeaturesByOrder(Seq:Seq[(String, Int)], number:Int) = {
    Seq.sortBy(_._2).reverse.take(number)
  }
  def dropFeature(Seq:Seq[(String, Int)], name:String)={
    Seq.filter {case (k,_) => k != name}
  }
}
