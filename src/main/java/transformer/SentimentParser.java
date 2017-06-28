package transformer;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;


import edu.stanford.nlp.ling.CoreAnnotations;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

/**
 * Created by user on 30-5-16.
 */
interface  SentimentParserInterface {
    ArrayList<WordSentiment> ParseSentiment(final String text);
}

////////////////////////////////////////////////////////////////

public class SentimentParser implements SentimentParserInterface {
    private StanfordCoreNLP pipeline;
    private HashSet<String> nounTags;

    public SentimentParser() {
        nounTags = new HashSet<String>();
        nounTags.add("NN");
        nounTags.add("NNS");
        nounTags.add("NNP");
        nounTags.add("NNPS");
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, parse, lemma, sentiment");
        pipeline = new StanfordCoreNLP(props);
        System.out.println("================Ready to Parse================");
    }

    @Override
    public ArrayList<WordSentiment> ParseSentiment(final String text) {
        ArrayList<WordSentiment> result = new ArrayList<>();
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
            Integer sentiment = RNNCoreAnnotations.getPredictedClass(tree);
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String pos = token.get(PartOfSpeechAnnotation.class);
                if (this.nounTags.contains(pos)) {
                    result.add(new WordSentiment(token.get(TextAnnotation.class), sentiment));
                }
            }
        }
        return result;
    }
    public idSentimentCollection ParseSentiment(final idText idText){
        return new idSentimentCollection(ParseSentiment(idText.text),idText.id);
    }
}
