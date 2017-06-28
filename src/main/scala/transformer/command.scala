/**
  * Created by user on 31-5-16.
  */

package transformer

import java.util.ArrayList
import scala.collection.mutable.Map

sealed trait Command //Send from master to child
case object ParseSentiment extends Command
case class CountFeature(Filter:WordSentiment=>Boolean) extends Command
case object GimmeFeatures extends Command
case class ParseFinalOutput(featureNames:ArrayList[String]) extends Command

sealed trait Counter //used to tell the master the progress
case object CountParsing extends Counter
case object CountFeatureProgress extends Counter
case object CountFinalOutput extends Counter

sealed trait CommandMaster
case object ParseSentimentMaster extends CommandMaster
case object SummarizeFeature extends  CommandMaster
case class ScreenFeature(f:Seq[(String, Int)] => Seq[(String,Int)])
case object ParseFinalResult extends CommandMaster
case object PrintFeatures extends CommandMaster
case class WriteFinalOutput(outfile:String) extends CommandMaster
case object RescreenFeature extends CommandMaster

sealed trait Data
case class IdTextToParse(idtext:idText) extends Data
case class FeatureCount(result:Map[String, Int]) extends Data
case class FinalOutputResult(result:String) extends Data
