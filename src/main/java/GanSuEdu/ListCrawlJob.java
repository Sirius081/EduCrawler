package main.java.GanSuEdu;


import main.java.general.BasicCrawler;

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
import main.java.general.PageDao;
/**
 * Get News List from the given URL
 * Created by leyi on 14/12/5.
 */
public class ListCrawlJob {
    private static Logger logger = Logger.getLogger(ListCrawlJob.class.getName());
    private static final int sourceId = 4;
    public static int getUrlNum() {
        return urlNum;
    }
    static  int urlNum=0;
   private static HashSet<String> urlList = new HashSet<>();
    private static String sectionUrl;
    private static int sectionNum = 0;
    private PageDao pDao;

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
    /**
     * Type 1 Page
     */
    private void processLiType(Document doc, String linkUrl,String lasrUrl)throws Exception{
        Elements eles = doc.select("div.page_left_list2 li");//新闻列表中的新闻标题及url
        for(Element ele : eles) {
            Element href = ele.select("a[href]").first();

            if(href!=null){
            String url = href.attr("href");
            String date=ele.select("span").first().text();

                if(url.equals(lasrUrl)||date.contains("2014")){//爬取urllist结束条件
                    writeToLinkFile(urlList);
                    urlNum+=urlList.size();
                    urlList.clear();
                    return;
                }

                if(url.contains("gsedu.cn")){//urllist开始条件
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
        Element nextPage = doc.select("span.NaviBar a[title=下一页]").first();
        if(nextPage!=null){
            String nextHref=nextPage.attr("href");
            getUrlList(nextHref,lasrUrl);
        }
    }



    /**
     * Write URL list to link files
     * @param urlList
     */
    private void writeToLinkFile(HashSet<String> urlList) throws SQLException {

        pDao.insertList(urlList,sourceId);
    }
}
