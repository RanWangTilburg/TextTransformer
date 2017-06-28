package transformer;

/**
 * Created by user on 31-5-16.
 */
public class WordSentiment {
public String word;
public Integer sentiment;

public WordSentiment(String word, Integer sentiment) {
        this.word = word.toLowerCase();
        this.sentiment = sentiment;
        }
}
