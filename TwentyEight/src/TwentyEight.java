import java.util.*;
import java.lang.Thread;
import java.io.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;

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
            threadArray[i].join();
    }
}
class Sender{
    synchronized public static void send(ActiveWFObject receiver, Message message) throws InterruptedException {
        receiver.queue.put(message);
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
    BlockingQueue<Message> queue;
    boolean stop;
    ActiveWFObject(){
        this.queue = new LinkedBlockingDeque<>();
        this.stop = false;
        this.start();
    }
    abstract public void dispatch(Message message) throws Exception;
    public void run(){
        while(!this.stop) {
            Message message = null;
            try {
                message = queue.take();
                this.dispatch(message);
                if (message.msg.get(0) == "die")
                this.stop = true;
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class DataStorageManager extends ActiveWFObject{
    ActiveWFObject stopWordManager;
    String data;
    public synchronized void init(Message message) throws FileNotFoundException {
        String path_to_file =(String)message.msg.get(1);
        this.stopWordManager = (ActiveWFObject)message.msg.get(2);
        this.data = "";
        File file = new File(path_to_file);
        Scanner sc = new Scanner(file);
        while(sc.hasNext())
           data += sc.nextLine();
        data = data.replaceAll("[^a-zA-Z]"," ").toLowerCase();
    }
    public synchronized void processWords(Message message) throws InterruptedException {
        ActiveWFObject recipient = (ActiveWFObject) message.msg.get(1);
        String[] afterSplit = this.data.split(" ");
        for(int i = 0; i < afterSplit.length; i++){
            if(afterSplit[i] != ""){
            Message newMessage = new Message();
            newMessage.msg.addLast("filter");
            newMessage.msg.addLast(afterSplit[i]);
            newMessage.msg.addLast(recipient);
            Sender.send(stopWordManager,newMessage);
            }
        }
        Message newMessage = new Message();
        newMessage.msg.addLast("top25");
        newMessage.msg.addLast(recipient);
        Sender.send(stopWordManager,newMessage);
    }
    @Override
    public synchronized void dispatch(Message message) throws Exception{
        if(message.msg.get(0) == "init") {
            this.init(message);
        }
        else if(message.msg.get(0) == "send_word_freqs"){
            processWords(message);
        }
        else
            Sender.send(stopWordManager,message);
    }
}
class stopWordManager extends ActiveWFObject{
    HashSet<String> stopWords;
    @Override
    public synchronized void dispatch(Message message) throws Exception {
        if(message.msg.getFirst() == "init"){
            this.init();
        }
        else if(message.msg.getFirst() == "filter"){
            this.filter(message);
        }
        else{
            if(message.msg.get(0) != "die")
            Sender.send((ActiveWFObject) message.msg.get(1),message);
        }
    }
    public synchronized void init() throws FileNotFoundException {
        File file  = new File("../stop_words.txt");
        Scanner sc = new Scanner(file);
        stopWords = new HashSet<>();
        String[] tmp = sc.nextLine().split(",");
        for(String each:tmp)
            stopWords.add(each);
        stopWords.add("s");
    }
    public synchronized void filter(Message message) throws Exception{
        String word = (String)message.msg.get(1);
        ActiveWFObject recipient = (ActiveWFObject)message.msg.get(2);
        if(!stopWords.contains(word))
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
    public synchronized void dispatch(Message message) throws Exception{
        if(message.msg.getFirst() == "word"){
            incrementCount(message);
        }
        else if(message.msg.getFirst() == "top25"){
            top25();
        }
        else if(message.msg.getFirst() == "run"){
            doRun(message);
        }
        else
            throw new IllegalArgumentException("Invalide message");
    }
    public synchronized void incrementCount(Message message){
        String word = (String)message.msg.get(1);
        if(wordFreqs.containsKey(word))
            wordFreqs.put(word,wordFreqs.get(word)+1);
        else
            wordFreqs.put(word,1);
    }
    public synchronized void top25() throws Exception{
        HashMap<String, Integer> sortedMap = wordFreqs.entrySet()
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
        Message die = new Message();
        die.msg.addLast("die");
        Sender.send(this.storageManager,die);
        this.stop = true;
    }
    public synchronized void doRun(Message message) throws Exception{
        this.storageManager = (ActiveWFObject) message.msg.get(1);
        this.wordFreqs = new HashMap<>();
        Message newMessage = new Message();
        newMessage.msg.addLast("send_word_freqs");
        newMessage.msg.addLast(this);
        Sender.send(storageManager,newMessage);
    }
}