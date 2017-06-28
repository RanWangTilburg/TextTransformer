package transformer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;

import FileIO.IO;
import edu.stanford.nlp.util.CoreMap;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.languagetool.language.BritishEnglish;

abstract class TextCleanerFunction<E> {
    abstract E ReadFile(String infile); //Read in a file

    abstract void WriteFile(String outfile, E data); //Write

    abstract E CleanWithReplace(E data, ArrayList<Replacement> replacements, int nCol); //Clean the data : Including lemma and delete non ASC II, including replacement

    abstract E CleanWithoutReplace(E data, int nCol); // Clean the data; No replacement given

    void ReadCleanWrite(String infile, String outfile, String replacefile, int nCol) {
        ArrayList<Replacement> replacements = Replacement.getReplacementFromFile(replacefile);
        E data = ReadFile(infile);
        E cleaned = CleanWithReplace(data, replacements, nCol);
        WriteFile(outfile, cleaned);
    }

    void ReadCleanWriteNoReplace(String infile, String outfile, int nCol) {
        E data = ReadFile(infile);
        E cleaned = CleanWithoutReplace(data, nCol);
        WriteFile(outfile, cleaned);
    }
}

interface TextCleanerFunctionImpl {
    String Lemma(String text); //Lemmatize the text

    String DeleteNonASCII(String text); // Delete Non ASC II Strings

    String Replace(String text, ArrayList<Replacement> replacement); //Replace a string with a list of rules and replacements

    String SpellCheck(String text); //Correct Spelling mistakes
}

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////


public class TextCleaner extends TextCleanerFunction<ArrayList<String>> {
    private String encoding = "UTF8";
    private TextCleanerFunctionImplClass worker = new TextCleanerFunctionImplClass();

    @Override
    public ArrayList<String> ReadFile(String infile) {
        ArrayList<String> data = IO.readFile(infile, encoding);
        return data;
    }

    @Override
    public void WriteFile(String outfile, ArrayList<String> data) {
        IO.writeFile(outfile, data, encoding);
    }

    @Override
    ArrayList<String> CleanWithReplace(ArrayList<String> data, ArrayList<Replacement> replacements, int nCol) {
        ArrayList<String> result = new ArrayList<>();
        Integer counter = 0;
        for (String line : data) {
            String[] Array = line.split("\t");
            String text = Array[nCol];
            //This is probably inefficient
            text = worker.Replace(text, replacements);
            text = worker.DeleteNonASCII(text);
            text = worker.SpellCheck(text);
            text = worker.Lemma(text);
            Array[nCol] = text;
            result.add(String.join("\t", Array));
            counter++;
            if (counter % 50 == 0) {
                System.out.println("Finished Cleaning review till " + counter.toString());
            }
        }

        return result;
    }

    @Override
    ArrayList<String> CleanWithoutReplace(ArrayList<String> data, int nCol) {
        ArrayList<String> result = new ArrayList<>();
        for (String line : data) {
            String[] Array = line.split("\t");
            String text = Array[nCol];
            //This is probably inefficient
//            text = worker.Replace(text,replacements);
            text = worker.DeleteNonASCII(text);
            text = worker.SpellCheck(text);
            text = worker.Lemma(text);
            Array[nCol] = text;
            result.add(String.join("\t", Array));
        }
        return result;
    }

}

class TextCleanerFunctionImplClass implements TextCleanerFunctionImpl {
    private StanfordCoreNLP pipeline;
    private JLanguageTool langTool = null;

    public TextCleanerFunctionImplClass() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
        langTool = new JLanguageTool(new BritishEnglish());
    }


    @Override
    public String Lemma(String text) {//I am not sure whether each time initializing Annotation is costly
        StringBuilder temp = new StringBuilder();
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                temp.append(token.get(CoreAnnotations.LemmaAnnotation.class));
                temp.append(" ");
            }
        }
        return temp.toString();
    }

    @Override
    public String DeleteNonASCII(String text) {return text.replaceAll("[^\\x20-\\x7E]", "");
    }

    @Override
    public String Replace(String text, ArrayList<Replacement> replacements) {
        StringBuilder result = new StringBuilder(text);
        for (Replacement replacement : replacements) {
            replaceAll(result, replacement.pattern, replacement.replace);
        }
        return result.toString();
    }

    @Override
    public String SpellCheck(String text) {
        StringBuilder result = new StringBuilder(text);
        //langTool.activateDefaultPatternRules();
        List<RuleMatch> matches = null;
        try {
            int shift = 0;
            matches = langTool.check(text);
            for (RuleMatch match : matches) {
                List suggestedReplacement = match.getSuggestedReplacements();
                if (suggestedReplacement.size() != 0) {
                    if ((match.getToPos() - shift < result.length()) & (match.getFromPos() - shift > 0)) {
                        result.replace(match.getFromPos() - shift, match.getToPos() - shift, suggestedReplacement.get(0).toString());
//                    int a = match.getFromPos();
//                    int b = match.getToPos();
//                        System.out.println(result.toString());
                        shift += match.getToPos() - match.getFromPos() - suggestedReplacement.get(0).toString().length();
                    }
                } else {
                    if ((match.getToPos() - shift < result.length()) & (match.getFromPos() - shift > 0)) {
                        result.replace(match.getFromPos(), match.getToPos(), "");
                        shift += match.getFromPos() - match.getToPos();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private void replaceAll(StringBuilder sb, Pattern pattern, String replacement) {
        Matcher m = pattern.matcher(sb);
        int start = 0;
        while (m.find(start)) {
            sb.replace(m.start(), m.end(), replacement);
            start = m.start() + replacement.length();
        }
    }
}

class Replacement {
    public String replace;
    public Pattern pattern;

    public Replacement(String replaceText, String patternText) {
        this.replace = replaceText;
        this.pattern = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE);
    }

    public static ArrayList<Replacement> getReplacementFromFile(String infile) {
        ArrayList<String> data = IO.readFile(infile, "UTF8");
        ArrayList<Replacement> result = new ArrayList<>();
        for (String line : data) {
            result.add(new Replacement(line.split("\t")[1], line.split("\t")[0]));
        }
        return result;
    }
}