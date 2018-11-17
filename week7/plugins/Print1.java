import java.util.HashMap;
import java.util.Map;

public class Print1 implements TFPrint{
    public void doPrint(HashMap<String, Integer> wordFreqs) {
        int i = 0;
        System.out.println("This is version1 Print");
        for(Map.Entry<String,Integer> entry:wordFreqs.entrySet()){
            System.out.println(entry.getKey()
                    + "  -  " + entry.getValue());
            ++i;
            if(i == 25)
                break;
        }
    }
}
