package main.java.YunNanEdu;

import main.java.general.BasicCrawler;
import main.java.general.PageDao;
import main.java.general.Recorder;
import main.java.report.Visitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Get News List from the given URL
 * Created by leyi on 14/12/5.
 * Give end url
 */
public class ListCrawlJob {
    private static Logger logger = Logger.getLogger(ListCrawlJob.class.getName());
    private static final int sourceId = 43;
    public static int getUrlNum() {
        return urlNum;
    }
    static  int urlNum=0;
    private static String baseUrl;
    private static String sectionUrl;
    private static int sectionNum = 0;
    private PageDao pDao;
    private static HashSet<String> urlList = new HashSet<>();
    public ListCrawlJob() {
        pDao = new PageDao();
    }

    public void crawlList(String outputFolder) {
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

    private void getUrlList(String linkUrl, String lastUrl) throws Exception{
        //爬取列表页面
        logger.info("Start crawl list page:" + linkUrl);
        String content = "";
        content = new BasicCrawler().crawlPage(linkUrl, "gbk");
        //TODO  解析文章列表页面，获取需要爬取页面URL
        Document doc = Jsoup.parse(content);
        processLiType(doc, linkUrl, lastUrl);

    }
    private void writeToLinkFile(HashSet<String> urlList) throws SQLException {

        pDao.insertList(urlList,sourceId);
    }
    /**
     * Type 1 Page
     */
    private void processLiType(Document doc, String linkUrl, String lastUrl)throws Exception{
        Elements eles = doc.select("div#mainsub li");//新闻列表中的新闻标题及url
        for(Element li :eles){
            Element ele=li.select("a[href]").get(1);
            if(!ele.attr("href").contains("http:")) {
                String url = "http://www.ynjy.cn/" + ele.attr("href");

                if(url.equals(lastUrl)){//爬取urllist结束条件
                    writeToLinkFile(urlList);
                    urlNum+=urlList.size();
                    urlList.clear();
                    return;
                }
                if(url.contains("http")){
                    urlList.add(url);
                    sectionNum++;
                    if(sectionNum==1){
                        try {
                                pDao.updateLastUrl(sourceId, sectionUrl, url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //TODO  翻页，根据不同页面规则不同
        Elements nextPage = doc.select("div.showpage").select("a[href]");

        for(Element href : nextPage) {
            String s=href.text();
            if(s.equals("下一页")){
                String nextHref = href.attr("href");
                String nextUrl="http://www.ynjy.cn/"+nextHref;
                getUrlList("http://www.ynjy.cn/"+nextHref,lastUrl);
            }
        }

    }
    //云南教育厅网站新闻列表页面内的新闻链接不包含时间  到新闻页面去爬取
    private String getUrlDate(String url)throws Exception{
        String date="";
        String content = "";
        content = new BasicCrawler().crawlPage(url, "gbk");
        //TODO  解析文章列表页面，获取需要爬取页面URL
        Document doc = Jsoup.parse(content, "gbk");
        Element dateDiv = doc.select("span.Article_tdbgall").first();
        String s=dateDiv.text();
        if(s.contains("更新时间：")){
            String[] temp=s.split("更新时间：");
            date=temp[temp.length-1];
        }

        return date;
    }

}
