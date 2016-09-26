package test;

import net.sf.json.JSONArray;
import main.java.zhihu.ParseComments;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by sirius-.- on 2015/9/16.
 */
public class TestZhiHu {
    public void restore(int []a,int seed){
        int n=a.length-1;
        int[] randNum=new int[n];
        Random rand=new Random(seed);
        for(int i=0;i<a.length;i++){
            randNum[i]=rand.nextInt();
        }
        for(int i=n-1;i>=0;i--){
            int t=a[i];
            a[i]=a[randNum[n-i]];
            a[randNum[n-i]]=t;
        }

    }
    public static void main(String [] args)throws Exception{


    }

}
