package transformer;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.SymbolLocator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpellCorrector {
    private JLanguageTool langTool = null;
    private int counter = 0;

    public SpellCorrector() {
        langTool = new JLanguageTool(new BritishEnglish());
    }

    public String SpellCheck(String text) {
        StringBuilder result = new StringBuilder(text);
        //langTool.activateDefaultPatternRules();
        List<RuleMatch> matches = null;
        try {
            int shift = 0;
            matches = langTool.check(text);
            for (RuleMatch match : matches) {
//                System.out.println("Found mistakes in ");
//                System.out.println(text);
                List suggestedReplacement = match.getSuggestedReplacements();
                if (suggestedReplacement.size() != 0) {
                    if ((match.getToPos() - shift < result.length()) & (match.getFromPos() - shift > 0)) {
                        result.replace(match.getFromPos() - shift, match.getToPos() - shift, suggestedReplacement.get(0).toString());
//                    int a = match.getFromPos();
//                    int b = match.getToPos();
//                        System.out.println(result.toString());
                        shift += match.getToPos() - match.getFromPos() - suggestedReplacement.get(0).toString().length();
                    }
                }
//                } else {
//                    if ((match.getToPos() - shift < result.length()) & (match.getFromPos() - shift > 0)) {
//                        result.replace(match.getFromPos(), match.getToPos(), " ");
//                        shift += match.getFromPos() - match.getToPos()-1;
//                    }
                }
//            System.out.println(result);
        } catch (IOException e) {
            return  " ";
        }

        return result.toString();
    }
    public String correct_line(String text, int id){
        counter++;
        String[] array = text.split("\t");
        array[id] = SpellCheck(array[id]);
        if (counter % 50==0){
            System.out.println("Finished cleaning line "+counter);
        }
        return String.join("\t", array);
    }
    public ArrayList<String> SpellCorrectArrayList(ArrayList<String> texts, int lineNumber) {
        int counter = 0;
        ArrayList<String> result = new ArrayList<>();
        for (String line : texts) {
            String[] Array = line.split("\t");
            String text = Array[lineNumber];
            text = SpellCheck(text);

            Array[lineNumber] = text;
            result.add(String.join("\t", Array));
            counter++;

            if (counter % 100 == 0) {
                System.out.println("Cleaning line " + counter);
            }
        }
        return result;
    }

}