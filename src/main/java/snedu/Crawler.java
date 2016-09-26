package main.java.snedu;

/**
 * Created by sirius-.- on 2015/8/2.
 */




import main.java.general.EduCrawler;
import main.java.general.SocketCommunicationClientNew;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Crawler extends EduCrawler {
    private static Logger logger = Logger.getLogger(Crawler.class.getName());
    private static int sourceId=35;
    static SocketCommunicationClientNew sc;

    public Crawler(){
        sc = new SocketCommunicationClientNew(ip,4700,sourceId);
    }
    public static void main (String args[]) throws IOException, InterruptedException, SQLException {
        //Crawl();
        new Crawler().crawl();
    }

    @Override
    public void crawl()  {
        String directory =".\\GanSuEdu";
        logger.info("Start Crawl urlList on GanSuEdu");
        try {
            //????��?
            ListCrawlJob crawler1 = new ListCrawlJob();
            crawler1.crawlList(directory);
            pDao.Sum(sourceId,crawler1.getUrlNum());        //????????????��???????
            sc.sendMessage(sourceId+"\t"+crawler1.getUrlNum()+"\t"+3);
            logger.info("urlList Crawl Job Finished");
            //??????
            logger.info("Start Crawl page on GanSuEdu");
            PageCrawlJob crawler2 = new main.java.snedu.PageCrawlJob();
            //???????????��????
            crawler2.crawlExceptionList();
            crawler2.crawlPageList();
            pDao.finishedUrl(sourceId, crawler2.count);
            //PageDao.closeAll();
            logger.info("Page Crawl Job Finished");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

