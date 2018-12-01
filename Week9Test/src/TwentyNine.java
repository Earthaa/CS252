import java.util.*;
import java.io.*;
import java.util.concurrent.*;
import java.lang.Thread;

import static java.util.stream.Collectors.toMap;
/*
* In This 29.2, I use processFreqs and a concurrentHashMap to merge the frequencies concurrently
* processFreqs change the wordFreqs HashMap concurrently
* */
public class TwentyNine {
    public static void main(String[] args) throws Exception{
        Space.readStopWords();
        Space.readWords("../pride-and-prejudice.txt");
        Thread[] wordsWorkers = new Thread[5];
        Thread[] freqsWorkers = new Thread[3];
        //Do Count words
        for(int i = 0; i < 5; i++){
            wordsWorkers[i] = new processWords();
            wordsWorkers[i].start();
        }
        for(int i = 0; i < 5; i++)
            wordsWorkers[i].join();

        //Do merging frequencies
        for(int i = 0; i < 3; i++){
            freqsWorkers[i] = new processFreqs();
            freqsWorkers[i].start();
        }
        for(int i = 0; i < 3; i++)
            freqsWorkers[i].join();

        //Do printing, print top25
        HashMap<String, Integer> sortedMap = Space.wordFreqs.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        int i = 0;
        for(Map.Entry<String,Integer> entry:sortedMap.entrySet()){
            System.out.println(entry.getKey()
                    + "  -  " + entry.getValue());
            ++i;
            if(i == 25)
                break;
        }
    }
}
//Provide global variables
class Space{
    public static BlockingQueue<String> wordSpace = new LinkedBlockingDeque<>();
    public static BlockingQueue<Map<String,Integer>> freqSpace = new LinkedBlockingDeque<>();
    public static Set<String> stopWords = new HashSet<>();
    public static ConcurrentHashMap<String,Integer> wordFreqs = new ConcurrentHashMap<>();
    public static void readStopWords() throws Exception{
        File file  = new File("../stop_words.txt");
        Scanner sc = new Scanner(file);
        stopWords = new HashSet<>();
        String[] tmp = sc.nextLine().split(",");
        for(String each:tmp)
            stopWords.add(each);
        stopWords.add("s");
    }
    public static void readWords(String path) throws Exception{
        File file = new File(path);
        Scanner sc = new Scanner(file);
        while (sc.hasNext()){
            String[] words = sc.nextLine().replaceAll("[^a-zA-Z]"," ").toLowerCase().
                    split(" ");
            for(int i = 0; i < words.length; i++){
                if(!words[i].equals(""))
                    wordSpace.put(words[i]);
            }
        }
    }
}

//process words concurrently
class processWords extends Thread{
    @Override
    public void run(){
        HashMap<String,Integer> wordFreqs  = new HashMap<>();//wordFreqs
        while (true){
            String word = null;
            try {
                word = Space.wordSpace.poll(1,TimeUnit.SECONDS);
                if(word == null)
                    break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!Space.stopWords.contains(word)){
                if(!wordFreqs.containsKey(word))
                    wordFreqs.put(word,1);
                else
                    wordFreqs.put(word,wordFreqs.get(word) + 1);
            }
        }
        try {
            Space.freqSpace.put(wordFreqs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
//process frequencies concurrently
class processFreqs extends Thread {
    public void run() {
        while (true) {
            Map<String, Integer> freqs = null;
            try {
                freqs = Space.freqSpace.poll(1, TimeUnit.SECONDS);
                if (freqs == null)
                    break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Map.Entry<String, Integer> each : freqs.entrySet()) {
                String key = each.getKey();
                int val = each.getValue();
                if (!Space.wordFreqs.containsKey(key))
                    Space.wordFreqs.put(key, val);
                else
                    Space.wordFreqs.put(key, Space.wordFreqs.get(key) + val);
            }
        }
    }
}
