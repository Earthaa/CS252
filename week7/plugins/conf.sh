javac TFFreqs.java TFPrint.java TFWords.java
jar cf myInterface.jar TFFreqs.class TFPrint.class TFWords.class 
javac -classpath "./myInterface.jar" Freqs1.java Print1.java Words1.java
jar cf plugin1.jar Freqs1.class Print1.class Words1.class
javac -classpath "./myInterface.jar" Freqs2.java Print2.java Words2.java
jar cf plugin2.jar Freqs2.class Print2.class Words2.class
