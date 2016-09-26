package main.java.ifeng;
import main.java.general.EduCrawler;
import main.java.general.PageDao;
import main.java.general.SocketCommunicationClientNew;

import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Crawler extends EduCrawler {
    private static Logger logger = Logger.getLogger(Crawler.class.getName());
    private static int sourceId=20;
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
        logger.info("Start Crawl urlList on ifeng.");
        try {
            //��ȡ�б�
            ListCrawlJob crawler1 = new ListCrawlJob();
            crawler1.crawlList();
            pDao.Sum(sourceId,crawler1.getUrlNum());        //��ÿ���������д�����ݿ�
            sc.sendMessage(sourceId+"\t"+crawler1.getUrlNum()+"\t"+3);
            logger.info("urlList Crawl Job Finished");
            //��ȡҳ��
            logger.info("Start Crawl page on ifeng");
            PageCrawlJob crawler2 = new PageCrawlJob();
            //�ȼ���Ƿ����쳣δ����
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