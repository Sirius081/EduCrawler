package main.java.teachercn;



import main.java.general.*;
import main.java.report.Visitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * ��ȡ�б�ҳ��
 * Created by Zwj on 15/7/27.
 */
public class PageCrawlJob {
    private static Logger logger = Logger.getLogger(PageCrawlJob.class.getName());

    Visitor v = new Visitor();
    static int count = 0;             //ͳ���Ѿ���ȡ��url����
    static int sourceId = 38;

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
                content = new BasicCrawler().crawlPage(exceptionUrl, "gbk");
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

    public void crawlPageList() {
        String url = "";
        while (!(url = pDao.crawlList(sourceId)).equals("")) {
            try {
                logger.info("Start crawl list page:" + url);
                String content = "";
                content = new BasicCrawler().crawlPage(url, "gbk");
                //���������б�ҳ�棬��ȡ��Ҫ��ȡҳ��URL
                Document doc = Jsoup.parse(content);
                writeToDB(url, doc);
            } catch (Exception e) {
                //��ȡʧ��url+pageId+NowTime+exceptionInfo+crawlerIP+crawlerId+typeId
                e.printStackTrace();
                try {
                    pDao.insertExceptionList(url, sourceId, e.getClass().getName());
                    pDao.deleteList(url);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        }
        Crawler.sc.sendMessage(sourceId + "\t" + 2);
    }
    //parse and write page info  into database
    public void writeToDB( String url, Document doc) throws Exception {
        int pageId=0;
        String imgUrl="";
        try {
            Calendar nowDate=Calendar.getInstance();

            String titleStr = PageParserJob.parseTitle(url, doc);
            String date=PageParserJob.parseDate(url, doc);
            String pubSource=PageParserJob.parsePublisher(url, doc);

            StringBuffer contentBuffer = new StringBuffer("");
            PageParserJob.parseContent(contentBuffer, url, doc);
            String content = PageParserJob.reformat(contentBuffer.toString());

            imgUrl=PageParserJob.parseFirstImg(url,doc);
            // insert into database
            pageId=pDao.insertPage(sourceId, url, titleStr, date, pubSource, content);
            pDao.deleteList(url);


            //��ȡpage���:url+pageId+imgUrl+count(�����Ѿ���ȡ������)+crawlerId+typeId(��Ϣ����)
            //pageId=pDao.getPageIdByUrl(url);
            count++;
            String message=url+"\t"+pageId+"\t"+imgUrl+"\t"+count+"\t"+"1";
            Crawler.sc.sendMessage(message);
        }catch ( Exception e){
            throw e;
        }
    }
}
