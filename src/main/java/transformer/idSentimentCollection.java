package transformer;

import java.util.ArrayList;


public class idSentimentCollection {
    public idSentimentCollection(ArrayList<WordSentiment> result, Integer id) {
        this.result = result;
        this.id = id;
    }

    public Integer id;
    public ArrayList<WordSentiment> result;
}
