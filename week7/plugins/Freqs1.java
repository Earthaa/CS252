import java.util.*;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

public class Freqs1{
    HashMap<String,Integer> wordFreqs;
    public Freqs1(){
        wordFreqs = new HashMap<>();
    }
    public HashMap<String, Integer> top25(List<String> words) throws Exception {
        System.out.println("This is Version1 top25 Function");
        Iterator<String> it = words.iterator();
        while (it.hasNext()) {
            String tmp = it.next();
            if (wordFreqs.containsKey(tmp))
                wordFreqs.put(tmp, wordFreqs.get(tmp) + 1);
            else
                wordFreqs.put(tmp, 1);
        }
        HashMap<String, Integer> sortedMap = wordFreqs .entrySet()
                                                        .stream()
                                                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                                                        .collect(
                                                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                                LinkedHashMap::new));
        wordFreqs = sortedMap;
        return wordFreqs;
    }
}
