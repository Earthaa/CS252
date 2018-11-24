import java.util.*;
import java.lang.Thread;
import java.io.*;
import static java.util.stream.Collectors.toMap;

public class TwentyEight {
    public static void main(String[] args) throws InterruptedException {
        stopWordManager stop = new stopWordManager();
        Message msg1 = new Message();
        msg1.msg.addLast("init");
        Sender.send(stop,msg1);

        DataStorageManager storage = new DataStorageManager();
        Message msg2 = new Message();
        msg2.msg.addLast("init");
        msg2.msg.addLast("../pride-and-prejudice.txt");
        msg2.msg.addLast(stop);
        Sender.send(storage,msg2);

        DoCounter counter = new DoCounter();
        Message msg3 = new Message();
        msg3.msg.addLast("run");
        msg3.msg.addLast(storage);
        Sender.send(counter,msg3);

        ActiveWFObject[] threadArray = {stop,storage,counter};
        for(int i = 0; i < 3; i++)
            threadArray[i].run();
    }
}
class Sender{
    synchronized public static void send(ActiveWFObject receiver, Message message){
        receiver.queue.add(message);
    }
}
class Message{
    Message(){
        msg = new LinkedList<>();
    }
    LinkedList<Object> msg;
}
abstract class ActiveWFObject extends Thread{
    //String name;
    Queue<Message> queue;
    boolean stop;
    ActiveWFObject(){
        this.queue = new LinkedList<>();
        this.stop = false;
        this.start();
    }
    abstract public void dispatch(Message message) throws FileNotFoundException;
    public void run(){
        while(!this.stop) {
            Message message = queue.poll();
            if (message != null) {
                try {
                    if (message.msg.get(0) == "die")
                    {this.stop = true;
                        break;}
                    this.dispatch(message);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

class DataStorageManager extends ActiveWFObject{
    ActiveWFObject stopWordManager;
    String data;
    public synchronized void init(Message message) throws FileNotFoundException {
        String path_to_file =(String)message.msg.get(0);
        this.stopWordManager = (ActiveWFObject)message.msg.get(1);
        this.data = "";
        File file = new File(path_to_file);
        Scanner sc = new Scanner(file);
        while(sc.hasNext())
           data += sc.nextLine();
        data = data.replaceAll("[^a-zA-Z]"," ").toLowerCase();
    }
    public synchronized void processWords(Message message){
        ActiveWFObject recipient = (ActiveWFObject) message.msg.get(0);
        String[] afterSplit = this.data.split(" ");
        for(int i = 0; i < afterSplit.length; i++){
            Message newMessage = new Message();
            newMessage.msg.addLast("filter");
            newMessage.msg.addLast(afterSplit[i]);
            newMessage.msg.addLast(recipient);
            Sender.send(stopWordManager,newMessage);
        }
        Message newMessage = new Message();
        newMessage.msg.addLast("top25");
        newMessage.msg.addLast(recipient);
        Sender.send(stopWordManager,newMessage);
    }
    @Override
    public synchronized void dispatch(Message message) throws FileNotFoundException {
        if(message.msg.get(0) == "init") {
            message.msg.poll();
            this.init(message);
        }
        else if(message.msg.get(0) == "send_word_freqs"){
            message.msg.poll();
            processWords(message);
        }
        else
            Sender.send(stopWordManager,message);
    }
}
class stopWordManager extends ActiveWFObject{
    HashSet<String> stopWords;
    @Override
    public void dispatch(Message message) throws FileNotFoundException {
        if(message.msg.getFirst() == "init"){
            message.msg.poll();
            this.init();
        }
        else if(message.msg.getFirst() == "filter"){
            message.msg.poll();
            this.filter(message);
        }
        else{
            Sender.send((ActiveWFObject) message.msg.get(1),message);
        }
    }
    void init() throws FileNotFoundException {
        File file  = new File("../stop_words.txt");
        Scanner sc = new Scanner(file);
        stopWords = new HashSet<>();
        String[] tmp = sc.nextLine().split(",");
        for(String each:tmp)
            stopWords.add(each);
        stopWords.add("s");
    }
    void filter(Message message){
        String word = (String)message.msg.poll();
        ActiveWFObject recipient = (ActiveWFObject)message.msg.poll();
        if(!stopWords.contains(word) && word != "")
        {
            Message newMessage = new Message();
            newMessage.msg.addLast("word");
            newMessage.msg.addLast(word);
            Sender.send(recipient,newMessage);
        }
    }
}

class DoCounter extends ActiveWFObject{
    Map<String,Integer>  wordFreqs;
    ActiveWFObject storageManager;
    @Override
    public void dispatch(Message message) throws FileNotFoundException {
        if(message.msg.getFirst() == "word"){
            message.msg.poll();
            incrementCount(message);
        }
        else if(message.msg.getFirst() == "top25"){
            top25();
        }
        else if(message.msg.getFirst() == "run"){
            message.msg.poll();
            doRun(message);
        }
        else
            throw new IllegalArgumentException("Invalide message");
    }
    public void incrementCount(Message message){
        String word = (String)message.msg.getFirst();
        if(wordFreqs.containsKey(word))
            wordFreqs.put(word,wordFreqs.get(word)+1);
        else
            wordFreqs.put(word,1);
    }
    public void top25(){
        HashMap<String, Integer> sortedMap = wordFreqs.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        int i = 0;
        System.out.println("This is version1 Print");
        for(Map.Entry<String,Integer> entry:sortedMap.entrySet()){
            System.out.println(entry.getKey()
                    + "  -  " + entry.getValue());
            ++i;
            if(i == 25)
                break;
        }
        Message die = new Message();
        die.msg.addLast("die");
        Sender.send(this.storageManager,die);
        this.stop = true;
    }
    public void doRun(Message message){
        this.storageManager = (ActiveWFObject) message.msg.getFirst();
        this.wordFreqs = new HashMap<>();
        Message newMessage = new Message();
        newMessage.msg.addLast("send_word_freqs");
        newMessage.msg.addLast(this);
        Sender.send(storageManager,newMessage);
    }
}