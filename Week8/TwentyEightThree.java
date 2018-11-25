import java.util.*;
import java.lang.Thread;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import static java.util.stream.Collectors.toMap;
/*
* As you see, each method have getWord and giveWord method which
read word by word from the source obeying lazy river style
* The total program follows the Actor style and it has the same actors as TwentyEight.java
* getWord is used to ask a word from upstream and giveWord means give such word to the
downstream
* */
public class TwentyEightThree {
    public static void main(String[] args) throws InterruptedException {

        DoCounterLazy counter = new DoCounterLazy();
        stopWordManagerLazy stop = new stopWordManagerLazy();
        DataStorageManagerLazy storage = new DataStorageManagerLazy();

        Message msg1 = new Message();
        msg1.msg.addLast("init");
        msg1.msg.addLast(stop);
        Sender.send(counter,msg1);

        Message msg2 = new Message();
        msg2.msg.addLast("init");
        msg2.msg.addLast(counter);
        msg2.msg.addLast(storage);
        Sender.send(stop,msg2);

        Message msg3 = new Message();
        msg3.msg.addLast("init");
        msg3.msg.addLast(args[0]);
        msg3.msg.addLast(stop);
        Sender.send(storage,msg3);

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

class DataStorageManagerLazy extends ActiveWFObject{
    ActiveWFObject downStream;//stopWordManager
    File file;
    Scanner sc;
    int count ;
    String[] words;
    @Override
    public void dispatch(Message message) throws Exception{
        if(message.msg.get(0) == "init") {
            this.init(message);
        }
        else if(message.msg.get(0) == "get_word"){
            getWord(message);
        }
        else
            Sender.send(downStream,message);
    }
    public void init(Message message) throws FileNotFoundException {
        String path_to_file =(String)message.msg.get(1);
        this.downStream = (ActiveWFObject)message.msg.get(2);
        file = new File(path_to_file);
        sc = new Scanner(file);
        count = 0;
        words = new String[0];
    }

    public void getWord(Message message) throws InterruptedException {
        while(count == words.length) {
            if(!sc.hasNext()){
                Message die = new Message();
                die.msg.addLast("die");//If no next word, send die
                Sender.send(downStream,die);
                this.stop = true;
                return;
            }
            words = sc.nextLine().replaceAll("[^a-zA-Z]"," ").split(" ");
            count = 0;
        }
        String word = words[count].toLowerCase();
        Message newMessage = new Message();
        newMessage.msg.addLast("give_word");//give word to down stream
        newMessage.msg.addLast(word);
        Sender.send(downStream,newMessage);
        count ++;
    }
}

class stopWordManagerLazy extends ActiveWFObject{
    HashSet<String> stopWords;
    ActiveWFObject downStream;
    ActiveWFObject upStream;
    @Override
    public void dispatch(Message message) throws Exception {
        if(message.msg.get(0).equals("init")){
            init(message);}
        else if(message.msg.get(0).equals("get_word")){
            getWord();}
        else if(message.msg.get(0).equals("give_word")){
            giveWord(message);}
        else{
            Sender.send(downStream,message);}
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

class DoCounterLazy extends ActiveWFObject{
    Map<String,Integer>  wordFreqs;
    ActiveWFObject upStream;
    @Override
    public void dispatch(Message message) throws Exception{
        if(message.msg.getFirst() == "get_word"){
            getWord();}
        else if(message.msg.getFirst() == "give_word"){
            giveWord(message);}
        else if(message.msg.getFirst() == "init"){
            init(message);
        }
        else if(message.msg.getFirst() == "die"){
            top25();}
        else
            throw new IllegalArgumentException("Invalide message");
    }

    public void getWord() throws Exception{
        Message newMessage = new Message();
        newMessage.msg.addLast("get_word");
        Sender.send(upStream,newMessage);
    }
    public void giveWord(Message message) throws Exception{
        String word = (String)message.msg.get(1);
        if(wordFreqs.containsKey(word))
            wordFreqs.put(word,wordFreqs.get(word)+1);
        else
            wordFreqs.put(word,1);
        doRun();//put a word and then read another word
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
    }
    public void init(Message message) throws Exception{
        this.upStream = (ActiveWFObject) message.msg.get(1);
        this.wordFreqs = new HashMap<>();
        doRun();
    }
    public void doRun() throws InterruptedException {//Use to read another word
        Message newMessage = new Message();
        newMessage.msg.addLast("get_word");
        Sender.send(this,newMessage);
    }
}