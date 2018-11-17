import java.util.*;
import java.io.*;
import java.lang.reflect.*;
/*
* This assignment is implemented based on 4.1
* For convention I set the global variables to
save words,data and word frequencies.
* You could scroll to the buttom to see my main function which use
reflection to call the methods and dynamic load the class
* */
public class Seventeen {
    List<String> data;
    List<String> words;
    Map<String,Integer>  wordFreqs;
    public Seventeen(){
        data = new LinkedList<>();
        words = new LinkedList<>();
        wordFreqs = new HashMap<>();
    }
    public void readFile(String pathToFile) throws Exception{
        File file = new File(pathToFile);
        Scanner sc = new Scanner(file);
        while (sc.hasNext())
            data.add(sc.nextLine());
    }
    public void filterAndNormalize(){
        for(String str:data){
            String[] tmp = str.replaceAll("[^a-zA-Z]"," ").split(" ");
            for(String s:tmp){
                if(!s.equals(""))
                    words.add(s.toLowerCase());
            }
        }
    }
    public void removeStopWords() throws Exception{
        File file = new File("./stop_words.txt");
        Scanner sc = new Scanner(file);
        String[] tmp = sc.nextLine().split(",");
        Set<String> stopWords = new HashSet<>();
        for(String str:tmp)
            stopWords.add(str);
        stopWords.add("s");
        stopWords.add("re");
        Iterator<String> it= words.iterator();
        while(it.hasNext()){
            if(stopWords.contains(it.next()))
                it.remove();
        }
    }
    public void getFrequencies(){
        Iterator<String> it = words.iterator();
        while (it.hasNext()){
            String tmp = it.next();
            if(wordFreqs.containsKey(tmp))
                wordFreqs.put(tmp,wordFreqs.get(tmp)+1);
            else
                wordFreqs.put(tmp,1);
        }
    }
    public void Sort(){
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(wordFreqs.entrySet());
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
        wordFreqs = sortedMap;
    }
    public void printResult(){
        int i = 0;
        for(Map.Entry<String,Integer> entry:wordFreqs.entrySet()){
            System.out.println(entry.getKey()
                    + "  -  " + entry.getValue());
            ++i;
            if(i == 25)
                break;
        }
    }
    public static void main(String args[]) throws Exception{
        Class c = Class.forName("Seventeen");
        //Reflection to load the methods
        Method readFile = c.getDeclaredMethod("readFile", String.class);
        Method filterAndNormalize = c.getDeclaredMethod("filterAndNormalize");
        Method removeStopWords = c.getDeclaredMethod("removeStopWords");
        Method getFrequency = c.getDeclaredMethod("getFrequencies");
        Method Sort = c.getDeclaredMethod("Sort");
        Method printResult = c.getDeclaredMethod("printResult");
         //Dynamic load the class
        readFile.invoke(o,"./pride-and-prejudice.txt");
        //Invoke the method by using reflection
        filterAndNormalize.invoke(o);
        removeStopWords.invoke(o);
        getFrequency.invoke(o);
        Sort.invoke(o);
        printResult.invoke(o);
    }

}

