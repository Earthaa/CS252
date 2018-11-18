import java.util.*;
import java.io.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Words2{

    List<String> words;
    public Words2() {
        words = new LinkedList<>();
    }
    public List<String> extractWords(String path) throws Exception {
        System.out.println("This is Version2 extractWords Function");
        File file = new File(path);
        File stopFile = new File("../stop_words.txt");
        Scanner wordScanner = new Scanner(file);
        Scanner stopScanner = new Scanner(stopFile);
        String[] tmpStopWords = stopScanner.nextLine().split(",");
        Set<String> stopWords = new HashSet<>();
        for(String str:tmpStopWords)
            stopWords.add(str);
        stopWords.add("s");
        stopWords.add("re");
        stopWords.add("re");
        while(wordScanner.hasNext()){
            String line = wordScanner.nextLine();
            Pattern pattern =  Pattern.compile("[a-zA-Z]+");
            Matcher m = pattern.matcher(line);
            while(m.find()){
                String match = m.group();
                if(!stopWords.contains(match.toLowerCase()))
                words.add(match.toLowerCase());
            }
        }
        return words;
    }
}
