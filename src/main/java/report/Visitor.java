package main.java.report;

import main.java.general.Crawler;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/4/2.
 */
public class Visitor extends TimerTask implements Runnable{

    static java.lang.String name="凤凰网教育新闻爬虫";   //爬虫名称
    static String ip="";      //当前爬取网站
    static String time="";    //当前时间
    static String url="";     //已爬取url
    static int info=0;     //已爬取数量

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    public void run() {
        try {
            time=df.format(new Date());
            InetAddress addr = InetAddress.getLocalHost();
            ip=addr.getHostAddress();//获得本机IP
           // System.out.println("||"+name+"||"+ip+"||"+url+"||"+info+"||"+time+"||");
           // Jsoup.connect("http://58.198.176.141:8080/EduAnalysis/msg?msg=" +"["+name+"]-[" +ip+"]-["+info+"]-["+time+"]").get();
            //report("||"+name+"||"+ip+"||"+url+"||"+info+"||"+time+"||");
        } catch (Exception e) {
            //System.out.println("-------------Crawler发生异常--------------");
        }
    }

    public void seturl(String c){
        this.url=c;
    }

    public void setinfo(int i){
        this.info=i;
    }

    public void report(String message) throws IOException{
        Crawler crawler=new Crawler();
        String content;
        crawler.setUrl("http://58.198.176.141:8080/EduAnalysis/msg?msg="+message);
        content=crawler.getContent();
    }
}