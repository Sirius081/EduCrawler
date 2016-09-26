package main.java.sina;

import main.java.general.BasicCrawler;
import main.java.general.PageDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class ListCrawlJob {
    private static Logger logger = Logger.getLogger(ListCrawlJob.class.getName());
    private static final int sourceId =34;
    public static int getUrlNum() {
        return urlNum;
    }

    private static String sectionUrl;
    static int urlNum = 0;
    private static int sectionNum = 0;
    private PageDao pDao;
    private static HashSet<String> urlSet;
    public int geturlSetSize(){
        return urlSet.size();
    }
    public ListCrawlJob() {
        pDao = new PageDao();
        urlSet = new HashSet<String>();
    }
    public void crawlList() {
        try {
            HashMap<String, ArrayList<String>> sectionInfo = pDao.getSectionInfoById(sourceId);
            ArrayList<String> sectionUrlList = sectionInfo.get("sectionUrl");
            ArrayList<String> lastUrlList = sectionInfo.get("lastUrl");
            String lastUrl;
            for (int i = 0; i < sectionUrlList.size(); i++) {
                sectionUrl = sectionUrlList.get(i);
                lastUrl = lastUrlList.get(i);
                sectionNum = 0;
                getUrlList(sectionUrl, lastUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUrlList(String linkUrl, String lastUrl)throws Exception{
        //爬取列表页面
        logger.info("Start crawl list page:" + linkUrl);
        String content = "";
        content = new BasicCrawler().crawlPage(linkUrl);
        //TODO  解析文章列表页面，获取需要爬取页面URL
        Document doc = Jsoup.parse(content);
        processListType(doc, linkUrl, lastUrl);

    }

    /**
     * Type 2 Page
     */
    private void processListType(Document doc, String linkUrl, String lastUrl)throws Exception{
        List<String> urlList = new ArrayList<String>();
        Elements eles = doc.select("ul.list_009 li");
        String url="";
        for(Element ele : eles) {
            Element href = ele.select("a[href]").first();
            String title = href.text();
            url=href.attr("href");
            if (url.equals(lastUrl)) {//爬取urllist结束条件
                urlList.clear();
                return;
            }
            if (url.contains("2015")||url.startsWith("http://blog.sina.com.cn")) {
                urlSet.add(url);
                sectionNum++;
                pDao.insertList(url, sourceId);   //写入数据库
                if (sectionNum == 1) {
                    try {
                          pDao.updateLastUrl(sourceId, sectionUrl, url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                return;
            }
        }
        //TODO  翻页，根据不同页面规则不同
        Elements nextPage = doc.select("span.pagebox_next").select("a[href]");
        String nextHref=linkUrl.substring(0,linkUrl.lastIndexOf("/"))+nextPage.attr("href").substring(1);
        if (!linkUrl.equals(nextHref))
            getUrlList(nextHref,lastUrl);
    }
}
