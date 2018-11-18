import java.util.HashMap;
import java.util.Map;

public class Print2{
    public void doPrint(HashMap<String, Integer> wordFreqs) {
        int i = 0;
        System.out.println("This is version2 Print");
        for(Map.Entry<String,Integer> entry:wordFreqs.entrySet()){
            System.out.println(entry.getKey()
                    + "  -  " + entry.getValue());
            ++i;
            if(i == 25)
                break;
        }
    }
}
