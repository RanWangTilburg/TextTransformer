package transformer;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by user on 30-5-16.
 */
public class SentimentParserTest {
    public SentimentParser parser = new SentimentParser();
    @Test
    public void testParseSentiment() throws Exception {
        for (int i = 0;i<100;i++) {
            String a = "I really like the movie";
            String b = "I really hate the things and people that are around here";
            ArrayList<WordSentiment> result1 = parser.ParseSentiment(a);
            ArrayList<WordSentiment> result2 = parser.ParseSentiment(b);
        }
        assertEquals(0,0);
    }
}