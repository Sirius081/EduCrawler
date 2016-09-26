package main.java.qq;
import main.java.general.SocketCommunicationClientNew;
import java.util.logging.Logger;
import main.java.general.EduCrawler;
public class Crawler extends EduCrawler{
    private static Logger logger = Logger.getLogger(Crawler.class.getName());
    private static int sourceId=32;
    static SocketCommunicationClientNew sc;

    public Crawler(){
        sc = new SocketCommunicationClientNew(ip,4700,sourceId);
    }
    public static void main (String args[]) {
        //Crawl();
        new Crawler().crawl();
    }

    @Override
    public void crawl()  {
        String directory =".\\GanSuEdu";
        logger.info("Start Crawl urlList on qq");
        try {
            //????��?
            ListCrawlJob crawler1 = new ListCrawlJob();
            crawler1.crawlList();
            pDao.Sum(sourceId,crawler1.getUrlNum());        //????????????��???????
            sc.sendMessage(sourceId+"\t"+3558+"\t"+3);
            logger.info("urlList Crawl Job Finished");
            //??????
            logger.info("Start Crawl page on qq");
            PageCrawlJob crawler2 = new PageCrawlJob();
            //???????????��????
            //crawler2.crawlExceptionList();
            crawler2.crawlPageList();
            pDao.finishedUrl(sourceId, crawler2.count);
            //PageDao.closeAll();
            logger.info("Page Crawl Job Finished");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
