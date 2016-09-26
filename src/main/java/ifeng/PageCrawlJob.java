package main.java.ifeng;

import main.java.general.BasicCrawler;
import main.java.general.PageDao;
import main.java.report.Visitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Calendar;
import java.util.logging.Logger;

/**
 * ��ȡ�б�ҳ��
 * Created by leyi on 14/12/5.
 */
public class PageCrawlJob {
    private static Logger logger = Logger.getLogger(PageCrawlJob.class.getName());
    Visitor v = new Visitor();
    static int count = 0;             //ͳ���Ѿ���ȡ��url����
    int flag = 1;
    static Calendar runDate = Calendar.getInstance();
    static int sourceId =20;
    private static String ip;
    private static PageDao pDao;
    public PageCrawlJob() {
        pDao = new PageDao();

    }
    public void crawlExceptionList() {
        String exceptionUrl = "";
        while (!(exceptionUrl = pDao.crawlExceptionList(sourceId)).equals("")) {
            try {
                logger.info("Start crawl list page:" +exceptionUrl);
                String content = "";
                content = new BasicCrawler().crawlPage(exceptionUrl, "utf-8");
                //���������б�ҳ�棬��ȡ��Ҫ��ȡҳ��URL
                Document doc = Jsoup.parse(content);
                writeToDB(exceptionUrl, doc);
                pDao.deleteExceptionList(exceptionUrl);
            } catch (Exception e) {
                e.printStackTrace();
                pDao.deleteExceptionList(exceptionUrl);
            }
        }
    }

    public void crawlPageList() throws Exception{
        String url = "";
        while (!(url = pDao.crawlList(sourceId)).equals("")) {
            try {
                logger.info("Start crawl list page:" + url);
                String content = "";
                content =new BasicCrawler().crawlPage(url, "utf-8");
                //���������б�ҳ�棬��ȡ��Ҫ��ȡҳ��URL
                Document doc = Jsoup.parse(content);
                writeToDB(url, doc);
            } catch (Exception e) {
                //��ȡʧ��url+pageId+NowTime+exceptionInfo+crawlerIP+crawlerId+typeId
                e.printStackTrace();
                pDao.insertExceptionList(url, sourceId, e.getClass().getName());
                pDao.deleteList(url);
            }
        }
        Crawler.sc.sendMessage(sourceId + "\t" + 2);
    }

    public static void writeToDB(String url, Document doc) throws Exception {
        int pageId = 0;
        String imgUrl = "";
        StringBuffer content=new StringBuffer("");
        try {
            String datestr = PageParserJob.parseDate(url, doc);
            String date = datestr.substring(0,datestr.indexOf("年"))+"-"+datestr.substring(datestr.indexOf("年")+1, datestr.indexOf("月"))+"-"+datestr.substring(datestr.indexOf("月")+1, datestr.indexOf("日"));
            pageId=pDao.insertPage(sourceId, url, PageParserJob.parseTitle(url, doc), date, PageParserJob.parsePublisher(url, doc), PageParserJob.parseContent(content,url, doc),PageParserJob.parseComments(url));
            pDao.deleteList(url);
            imgUrl = PageParserJob.parseFirstImg(url, doc);
            //��ȡpage���:url+pageId+imgUrl+count(�����Ѿ���ȡ������)+crawlerId+typeId(��Ϣ����)
            //pageId = pDao.getPageIdByUrl(url);
            count++;
            String message = url + "\t" + pageId + "\t" + imgUrl + "\t" + count +"\t" + "1";
            Crawler.sc.sendMessage(message);
        } catch (Exception e) {
            throw e;
        }
    }
}
