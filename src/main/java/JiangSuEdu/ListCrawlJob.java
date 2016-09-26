package main.java.JiangSuEdu;

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
 */
public class ListCrawlJob {
    private static Logger logger = Logger.getLogger(ListCrawlJob.class.getName());
    private static final int sourceId = 22;
    private static String baseUrl;
    private static String sectionUrl;
    private static int sectionNum = 0;
    private PageDao pDao;
    private static HashSet<String> urlList = new HashSet<>();
    public ListCrawlJob() {
        pDao = new PageDao();
    }
    public static int getUrlNum() {
        return urlNum;
    }
    static  int urlNum=0;
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

    private void getUrlList(String linkUrl, String lastUrl)throws Exception{
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
    private void processLiType(Document doc, String linkUrl,String lasrUrl) throws Exception{
        Elements eles = doc.select("div.index2_right_imglist ul li");//新闻列表中的新闻标题及url
        for(Element ele : eles) {
            Element href = ele.select("a[href]").first();
            Element dateEle=ele.select("span").first();
            if(href!=null){
            String url ="http://www.jsenews.com/site/boot"+ href.attr("href").substring(2);
                String date=dateEle.text();
                if(url.equals(lasrUrl)||date.contains("2014")){//爬取urllist结束条件
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
        Elements nextPage = doc.select("div.center_fy p a");
        for(Element href : nextPage) {
            String s=href.text();
            String nextHref="";
            if(s.equals("下一页")){
                String []temp=linkUrl.split("/");
                String baseUrl="";
                for(int i=0;i<temp.length-1;i++){

                    baseUrl+=temp[i]+"/";
                }
                nextHref=baseUrl+href.attr("href");
                getUrlList(nextHref,lasrUrl);
            }
        }
    }
}
