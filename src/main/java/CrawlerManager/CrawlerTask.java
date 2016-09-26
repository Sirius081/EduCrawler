package main.java.CrawlerManager;


import main.java.general.EduCrawler;
import java.util.Objects;
import java.util.TimerTask;

/**
 * Created by sirius-.- on 2015/8/2.
 */
public class CrawlerTask extends TimerTask{
    private static main.java.GanSuEdu.Crawler gsCrawler;
    private static main.java.HuBeiEdu.Crawler hbCrawler;
    public CrawlerTask(){
    }
    @Override
    public void run() {


        new eachCrawlerJob(new main.java.GanSuEdu.Crawler()).start();
        new eachCrawlerJob(new main.java.HuBeiEdu.Crawler()).start();
        new eachCrawlerJob(new main.java.JiangSuEdu.Crawler()).start();
        new eachCrawlerJob(new main.java.JiLinEdu.Crawler()).start();
        new eachCrawlerJob(new main.java.ShangHaiMenHu.Crawler()).start();
        new eachCrawlerJob(new main.java.XinHuaEdu.Crawler()).start();
        new eachCrawlerJob(new main.java.YunNanEdu.Crawler()).start();
        //new eachCrawlerJob(new main.java.teachercn.Crawler()).start();
        new eachCrawlerJob(new main.java.snedu.Crawler()).start();
        //new eachCrawlerJob(new main.java.qq.Crawler()).start();
        new eachCrawlerJob(new main.java.zhihu.Crawler()).start();
        new eachCrawlerJob(new main.java.sohu.Crawler()).start();
        new eachCrawlerJob(new main.java.sina.Crawler()).start();
        //new eachCrawlerJob(new main.java.ifeng.Crawler()).start();
        new eachCrawlerJob(new main.java.tjmec.Crawler()).start();
    }
    private class eachCrawlerJob extends Thread{
        private EduCrawler crawler;
        public eachCrawlerJob(EduCrawler crawler){
            this.crawler=crawler;
        }
        @Override
        public void run() {
            this.crawler.crawl();
            System.out.println(crawler.getClass().getName()+"Finished!");
        }
    }
}
