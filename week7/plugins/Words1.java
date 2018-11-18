import java.util.*;
import java.io.*;
public class Words1{
    List<String> words;
    public Words1() {
        words = new LinkedList<>();
    }
    public List<String> extractWords(String path) throws Exception{
        System.out.println("This is Version1 extractWords Function");
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
        while(wordScanner.hasNext()){
            String line = wordScanner.nextLine();
            String[] tmp = line.replaceAll("[^a-zA-Z]"," ").split(" ");
            for(String s:tmp){
                if(!s.equals("")&&!stopWords.contains(s.toLowerCase()))
                    words.add(s.toLowerCase());
            }
        }
        return words;
    }
}
