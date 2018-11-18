import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
public class Nineteen {
    public static String Words;
    public static String Freqs;
    public static String Print;
    public static void readClass() throws Exception{
        File configurationFile = new File("./conf.txt");
        Scanner sc = new Scanner(configurationFile);
         Words = sc.nextLine().split(":")[1];
         Freqs = sc.nextLine().split(":")[1];
         Print = sc.nextLine().split(":")[1];
    }
    public static void main(String args[]) throws Exception{
        //Read Configuration file and save each field into jarFile,words,Freqs
        readClass();
        //Dynamically loading class from jar by using reflection
        URL urlWord = new File("./plugins/Words.jar").toURI().toURL();
        URL urlFreqs = new File("./plugins/Freqs.jar").toURI().toURL();
        URL urlPrint = new File("./plugins/Print.jar").toURI().toURL();
        URL[] urls = {new URL(urlWord.toString()),new URL(urlFreqs.toString()),
                        new URL(urlPrint.toString())};
        //System.out.println(Words);
        Class cWords = Class.forName(Words,true,new URLClassLoader(urls));
        Class cFreqs = Class.forName(Freqs,true,new URLClassLoader(urls));
        Class cPrint = Class.forName(Print,true,new URLClassLoader(urls));
        //Initialize class object
        Object oWords = cWords.getConstructor().newInstance();
        Object oFreqs = cFreqs.getConstructor().newInstance();
        Object oPrint = cPrint.getConstructor().newInstance();
        //Use reflection to call the methods 
        Method tfWords = cWords.getMethod("extractWords", String.class);
        Method tfFreqs = cFreqs.getMethod("top25", List.class);
        Method tfPrint = cPrint.getMethod("doPrint", HashMap.class);
        //call methods
        tfPrint.invoke(oPrint, tfFreqs.invoke(oFreqs, tfWords.invoke(oWords, "../pride-and-prejudice.txt")));
    }
}


