import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;

public class Nineteen {
        public static void main(String args[]) throws Exception{
        //Read Configuration file and save each field into jarFile,words,Freqs
        File configurationFile = new File("./conf.txt");
        Scanner sc = new Scanner(configurationFile);
        String jarFile  = sc.nextLine().split(":")[1];
        String Words = sc.nextLine().split(":")[1];
        String Freqs = sc.nextLine().split(":")[1];
        String Print = sc.nextLine().split(":")[1];
        //Dynamically loading class from jar by using reflection
        URL url = new File(jarFile).toURI().toURL();
        System.out.println(url.toString());
        URL[] urls = {new URL(url.toString())};

        //System.out.println(Words);
        Class cWords = Class.forName(Words,true,new URLClassLoader(urls));
        Class cFreqs = Class.forName(Freqs,true,new URLClassLoader(urls));
        Class cPrint = Class.forName(Print,true,new URLClassLoader(urls));
        //Initialize class object
        Object oWords = cWords.getConstructor().newInstance();
        Object oFreqs = cFreqs.getConstructor().newInstance();
        Object oPrint = cPrint.getConstructor().newInstance();
        //Use Interface to call the methods just as what shows on ppt
        TFWords tfWords = (TFWords) oWords;
        TFFreqs tfFreqs = (TFFreqs) oFreqs;
        TFPrint tfPrint = (TFPrint) oPrint;
        //call methods

        tfPrint.doPrint(tfFreqs.top25(tfWords.extractWords("../pride-and-prejudice.txt")));

    }

}
