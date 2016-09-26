package main.java.zhihu;

/**
 * Created by sirius-.- on 2015/8/2.
 */



import main.java.general.EduCrawler;
import main.java.general.SocketCommunicationClientNew;

import java.util.logging.Logger;

public class Crawler extends EduCrawler {
    private static Logger logger = Logger.getLogger(Crawler.class.getName());
    private static int sourceId=51;
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
        logger.info("Start Crawl urlList on ZhiHu");
        try {
//            //��ȡ�б�
//            ListCrawlJob crawler1 = new ListCrawlJob();
//            crawler1.crawlList();
//            pDao.Sum(sourceId,crawler1.getUrlNum());        //��ÿ���������д�����ݿ�
            sc.sendMessage(sourceId+"\t"+0+"\t"+3);
            logger.info("urlList Crawl Job Finished");
            //��ȡҳ��
            logger.info("Start Crawl page on zhihu");
            PageCrawlJob crawler2 = new PageCrawlJob();
            //�ȼ���Ƿ����쳣δ����
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

