package main.java.sina;

import main.java.general.BasicCrawler;
import main.java.general.PageDao;
import main.java.report.Visitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by song on 2015/5/9.
 */
public class PageCrawlJob {
    private static Logger logger = Logger.getLogger(PageCrawlJob.class.getName());
    Visitor v = new Visitor();
    static int count = 0;             //统计已经爬取的url数量
    static Calendar runDate = Calendar.getInstance();
    static int sourceId =34;
    private static String ip;
    private static PageDao pDao;
    public PageCrawlJob(){
        pDao=new PageDao();
    }
    public void crawlExceptionList() {
        String exceptionUrl = "";
        while (!(exceptionUrl = pDao.crawlExceptionList(sourceId)).equals("")) {
            try {
                logger.info("Start crawl list page:" +exceptionUrl);
                String content = "";
                if(exceptionUrl.startsWith("http://blog.sina.com.cn"))
                    content = new BasicCrawler().crawlPage(exceptionUrl,"utf-8");
                else
                    content =new BasicCrawler().crawlPage(exceptionUrl);
                //解析文章列表页面，获取需要爬取页面URL
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
        //crawlExceptionList();
        while (!(url = pDao.crawlList(sourceId)).equals("")) {
            try {
                logger.info("Start crawl list page:" + url);
                String content = "";
                if(url.startsWith("http://blog.sina.com.cn"))
                    content = new BasicCrawler().crawlPage(url,"utf-8");
                else
                    content = new BasicCrawler().crawlPage(url);
                //解析文章列表页面，获取需要爬取页面URL
                Document doc = Jsoup.parse(content);
                writeToDB(url, doc);
            } catch (Exception e) {
                //爬取失败url+pageId+NowTime+exceptionInfo+crawlerIP+crawlerId+typeId
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
        try {
            String datestr = PageParserJob.parseDate(url, doc);
            String date = datestr.substring(0,datestr.indexOf("年"))+"-"+datestr.substring(datestr.indexOf("年")+1, datestr.indexOf("月"))+"-"+datestr.substring(datestr.indexOf("月")+1, datestr.indexOf("日"));
            pageId=pDao.insertPage(sourceId, url, PageParserJob.parseTitle(url, doc), date, PageParserJob.parsePublisher(url, doc), PageParserJob.parseContent(url, doc),PageParserJob.parseComments(url));
            pDao.deleteList(url);
            imgUrl = PageParserJob.parseFirstImg(url, doc);
            //爬取page完成:url+pageId+imgUrl+count(本次已经爬取的数量)+crawlerId+typeId(消息类型)
            //pageId = pDao.getPageIdByUrl(url);
            count++;
            String message = url + "\t" + pageId + "\t" + imgUrl + "\t" + count +"\t" + "1";
            Crawler.sc.sendMessage(message);
        } catch (Exception e) {
            throw e;
        }
    }
}
