import java.util.*;
import java.io.*;
/*
* This assignment is implemented by using JAVA iterator
* Each class below has implemented iterator implement
* Data is available in stream line by line
* */
public class TwentySeven {
    public static void main(String[] args)throws FileNotFoundException {
        CountAndSort cas = new CountAndSort("../pride-and-prejudice.txt");
        while(cas.hasNext()){
            Map<String,Integer> res = cas.next();
            int i = 0;
            System.out.println("-------------------------------------------------------------");
            for(Map.Entry<String,Integer> entry:res.entrySet()){
                System.out.println(entry.getKey()
                        + "  -  " + entry.getValue());
                ++i;
                if(i == 25)
                    break;
            }
        }
    }
}
//Read and generate a line at each time
class ReadLine implements Iterator<String>{
    File file;
    Scanner sc;//Read file through it
    public ReadLine(String fileName) throws FileNotFoundException{
        file = new File(fileName);
        if(!file.exists())
            throw new FileNotFoundException();
        sc = new Scanner(file);
    }
    public boolean hasNext(){
        return sc.hasNextLine();
    }
    public String next(){
        String res = sc.nextLine();
        return res;
    }
}
//Generate a word
class AllWords implements Iterator<String>{
    ReadLine rl;
    Queue<String> ls;
    public AllWords(String fileName) throws FileNotFoundException{
        rl = new ReadLine(fileName);
        ls = new LinkedList<String>();
    }
    public boolean hasNext(){
        return rl.hasNext();
    }
    public String next(){
        while(rl.hasNext() && ls.size() == 0){
            String tmp = rl.next();
            String[] splitWords = tmp.replaceAll("[^a-zA-Z]"," ").split(" ");
            for(String str:splitWords){
                if(!str.equals(""))
                    ls.add(str);
            }
        }
        return ls.poll().toLowerCase();
    }
}
//Generate a non-stop-word
class NonStopWords implements Iterator<String>{
    AllWords aw;
    Set<String> stopWords;
    public NonStopWords(String fileName) throws FileNotFoundException{
        aw = new AllWords(fileName);
        stopWords = new HashSet<String>();
        File file = new File("../stop_words.txt");
        if(!file.exists())
            throw new FileNotFoundException();
        Scanner sc = new Scanner(file);
        String[] tmp= sc.nextLine().split(",");
        for(String str:tmp)
            stopWords.add(str);
        stopWords.add("re");
        stopWords.add("s");
    }
    public boolean hasNext(){
        return aw.hasNext();
    }
    public String next(){
        String word = aw.next();
        while(stopWords.contains(word) && aw.hasNext()) {
            word = aw.next();
        }
        return word;
    }
}
//Sort and return sorted map
class CountAndSort implements Iterator<Map<String,Integer>>{
    NonStopWords nsw;
    Map<String,Integer> wordCount;
    int i;
    public CountAndSort(String fileName) throws FileNotFoundException
    {
        nsw = new NonStopWords(fileName);
        wordCount = new HashMap<String,Integer>();
        i = 1;
    }
    public boolean hasNext() {
            return nsw.hasNext();
    }
    public Map<String, Integer> next()
    {
        while(nsw.hasNext() && i % 5000 != 0){
            String key = nsw.next();
            if(wordCount.containsKey(key))
                wordCount.put(key,wordCount.get(key) + 1);
            else
                wordCount.put(key,1);
            i++;
        }
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(wordCount.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        i = i + 1;//Don't Forget
        return sortedMap;
    }
}
