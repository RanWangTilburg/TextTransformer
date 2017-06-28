package TextTransformerV2

import java.util

import transformer._
import ParActor._
import FileIO._

class SentimentDummy(features:util.ArrayList[String], no_review:Integer) extends ParActor.Runnable[String]{
  val sentimentParse = new SentimentParserV2(features, no_review)

  override def run(data: String): String = {
    sentimentParse.parse_sentiment(data)
  }
}
object Transformer {
  def parse_sentiment(inpath: String, inpath_feature:String, outpath: String, no_review: Integer): Unit = {
    val features = FileIO.IO.readFile(inpath_feature, "utf-8")
    val text=FileIO.IO.readFile(inpath, "utf-8")
    val parFor = new ParFor[SentimentDummy, String](4, 50, features, no_review)
    val result = parFor.run(text)

    FileIO.IO.writeFile(outpath, result, "utf-8")

  }
}

