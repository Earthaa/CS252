import java.util.*;
import java.lang.Thread;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
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
//provide static method send
class Sender{
    public static void send(ActiveWFObject receiver, Message message) throws InterruptedException {
        receiver.queue.put(message);
    }
}
//Use to save message
class Message{
    Message(){
        msg = new LinkedList<>();
    }
    LinkedList<Object> msg;
}
abstract class ActiveWFObject extends Thread{
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
            Message message;
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
    ActiveWFObject downStream;//stopWordManager
    File file;
    Scanner sc;
    int count;
    String[] words;
    @Override
    public void dispatch(Message message) throws Exception{
        if(message.msg.get(0) == "init") {
            this.init(message);
        }
        else if(message.msg.get(0) == "get_word"){
            getWord(message);
        }
        else if(message.msg.get(0) == "has_next")
            hasNext();
        else
            Sender.send(downStream,message);
    }
    public void hasNext() throws Exception {
        boolean flag = sc.hasNext();
        Message newMessage = new Message();
        newMessage.msg.addLast("give_has_next");
        newMessage.msg.addLast(flag);
        Sender.send(downStream,newMessage);
    }
    public void init(Message message) throws FileNotFoundException {
        String path_to_file =(String)message.msg.get(1);
        this.downStream = (ActiveWFObject)message.msg.get(2);
        file = new File(path_to_file);
        sc = new Scanner(file);
        count = 0;
    }
    public void getWord(Message message) throws InterruptedException {
        if(count == 0 || count == words.length){
            count = 0;
            String str = sc.nextLine();
            words = str.replaceAll("[^a-zA-Z]"," ").split(" ");
        }
        String word = words[count];
        Message newMessage = new Message();
        newMessage.msg.addLast("give_word");
        newMessage.msg.addLast(word);
        Sender.send(downStream,newMessage);
        count++;
    }
}
class stopWordManager extends ActiveWFObject{
    HashSet<String> stopWords;
    ActiveWFObject downStream;
    ActiveWFObject upStream;
    @Override
    public void dispatch(Message message) throws Exception {
        if(message.msg.get(0) == "init")
            this.init(message);
        else if(message.msg.get(0) == "get_word")
            getWord();
        else if(message.msg.get(0) == "give_word")
            giveWord(message);
        else if(message.msg.get(0) == "has_next")
            hasNext();
        else if(message.msg.get(0) == "give_has_next")
            giveHasNext(message);
        else
            Sender.send(downStream,message);
    }
    public void hasNext() throws Exception{
        Message newMessage = new Message();
        newMessage.msg.addLast("has_next");
        Sender.send(upStream,newMessage);
    }
    public void giveHasNext(Message message) throws Exception{
        Message newMessage = new Message();
        boolean flag = (boolean)message.msg.get(0);
        newMessage.msg.addLast("give_has_next");
        newMessage.msg.addLast(flag);
        Sender.send(downStream,message);
    }
    public void init(Message message) throws FileNotFoundException {
        downStream = (ActiveWFObject) message.msg.get(1);
        upStream = (ActiveWFObject) message.msg.get(2);
        File file  = new File("../stop_words.txt");
        Scanner sc = new Scanner(file);
        stopWords = new HashSet<>();
        String[] tmp = sc.nextLine().split(",");
        for(String each:tmp)
            stopWords.add(each);
        stopWords.add("s");
        stopWords.add("");
    }
    public void getWord() throws Exception{
        Message newMessage = new Message();
        newMessage.msg.addLast("get_word");
        Sender.send(upStream,newMessage);
    }
    public void giveWord(Message message) throws Exception {
        String word = (String) message.msg.get(1);
        Message newMessage = new Message();
        if(!stopWords.contains(word)) {
            newMessage.msg.addLast("give_word");
            newMessage.msg.addLast(word);
            Sender.send(downStream,newMessage);
        }
        else{
            newMessage.msg.addLast("get_word");
            Sender.send(upStream,newMessage);
        }
    }
}

class DoCounter extends ActiveWFObject{
    Map<String,Integer>  wordFreqs;
    ActiveWFObject upStream;
    boolean hasnext;
    @Override
    public void dispatch(Message message) throws Exception{
        if(message.msg.getFirst() == "get_word")
            getWord();
        else if(message.msg.getFirst() == "has_next")
            hasNext();
        else if(message.msg.getFirst() == "give_word")
            giveWord(message);
        else if(message.msg.getFirst() == "run"){
            doRun(message);
        }
        else
            throw new IllegalArgumentException("Invalide message");
    }
    public void hasNext() throws Exception{
        Message newMessage = new Message();
        newMessage.msg.addLast("has_next");
        Sender.send(upStream,newMessage);
    }
    public void getWord() throws Exception{
        Message newMessage = new Message();
        newMessage.msg.addLast("get_word");
        Sender.send(upStream,newMessage);
    }
    public void giveWord(Message message){
        String word = (String)message.msg.get(1);
        if(wordFreqs.containsKey(word))
            wordFreqs.put(word,wordFreqs.get(word)+1);
        else
            wordFreqs.put(word,1);
    }
    public void giveHasNext(Message message){
        this.hasnext = (boolean)message.msg.get(1);
    }

    public void top25() throws Exception{
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
    public void doRun(Message message) throws Exception{
        this.upStream = (ActiveWFObject) message.msg.get(1);
        this.wordFreqs = new HashMap<>();
        Message newMessage = new Message();
        newMessage.msg.addLast("");
        newMessage.msg.addLast(this);
        Sender.send(storageManager,newMessage);
    }
}