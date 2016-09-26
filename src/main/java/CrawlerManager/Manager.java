package main.java.CrawlerManager;


import java.util.Date;
import java.util.Timer;

/**
 * Created by sirius-.- on 2015/8/2.
 */
public class Manager{
    private Timer timer=null;
    private long period=1000*60*60*12*2000;//��������12Сʱ
    private static CrawlerTask task=null;

    private static Date date=null;
    public Manager(){
        System.out.println("Crawler Started at "+new Date());
        timer=new Timer();
        task=new CrawlerTask();
        date=new Date();
    }

    private void startCrawl(){
        timer.schedule(task,date,period);
    }

    public static void main (String [] args){

        new Manager().startCrawl();
    }
}
