package transformer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;


public class SentimentParserV2 {

    private ArrayList<Pattern> patterns;
    private Integer no_features;
    private StanfordCoreNLP pipeline;
    //    private Integer id_position;
    private Integer review_position;

    public SentimentParserV2(ArrayList<String> features, Integer review_position) {
        assert (!features.isEmpty());
//        assert (id_position >= 0);
        assert (review_position >= 0);
//        assert (id_position != review_position);
        this.no_features = features.size();
//        this.id_position = id_position;
        this.review_position = review_position;
        this.patterns = new ArrayList<>();
        for (String feature : features) {
            patterns.add(Pattern.compile(Pattern.quote(feature), Pattern.CASE_INSENSITIVE));
        }
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, parse, lemma, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public String parse_sentiment(String line) {
        String[] review_array = line.split("\t");
        String text_to_annotate = review_array[review_position];
//        ArrayList<WordSentiment> result = new ArrayList<>(no_features);
        Annotation annotation = new Annotation(text_to_annotate);
        pipeline.annotate(annotation);

        ArrayList<Integer> numbers = getIntegers();

//        String[] sentiments = new String[no_features];
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
            Integer sentiment = RNNCoreAnnotations.getPredictedClass(tree);
//            System.out.println("The resulting sentiment is" + sentiment.toString());
            String sentence_text = sentence.toString();
            System.out.println(sentence_text);
            for (Integer i = 0; i < no_features; i++) {
                Boolean match = patterns.get(i).matcher(sentence_text).find();
//                Boolean match = sentence_text.matches(patterns.get(i));
                set_sentiment(numbers, sentiment, i, match);
            }
//            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
//                String pos = token.get(PartOfSpeechAnnotation.class);
//                if (this.nounTags.contains(pos)) {
//                    result.add(new WordSentiment(token.get(TextAnnotation.class), sentiment));
//                }
//            }
        }

        StringBuilder tmp2 = return_line(line, numbers);

        return tmp2.toString();
    }

    @NotNull
    private ArrayList<Integer> getIntegers() {
        Integer[] numbers_tmp = new Integer[no_features];
        ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(numbers_tmp));
        Collections.fill(numbers, 0);
        return numbers;
    }

    @NotNull
    private StringBuilder return_line(String line, ArrayList<Integer> numbers) {
        StringBuilder tmp2 = new StringBuilder(line);

        for (Integer i = 0; i < no_features; i++) {
            tmp2.append("\t");
            tmp2.append(numbers.get(i));
        }
        return tmp2;
    }

    private void set_sentiment(ArrayList<Integer> numbers, Integer sentiment, Integer i, Boolean match) {
        if (match) {
            System.out.println("Found it");
            if (sentiment > 2) {
                numbers.set(i, 1);
            } else if (sentiment < 2) {
                numbers.set(i, -1);
            }
        }
    }

}
