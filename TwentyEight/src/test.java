import java.util.*;
public class test {
    public static void main(String[] args){
        String a ="sb";
        LinkedList<Object> o = new LinkedList<>();
        o.add(a);
        Integer b =3;
        o.add(b);
        String test = (String) o.poll();
        System.out.println(test);

    }
}
