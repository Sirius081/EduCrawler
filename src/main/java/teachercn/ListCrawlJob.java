package main.java.teachercn;

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
import java.util.*;
import java.util.logging.Logger;

public class ListCrawlJob {
    private static Logger logger = Logger.getLogger(ListCrawlJob.class.getName());
    private static final int sourceId = 38;
    public static int getUrlNum() {
        return urlNum;
    }
    static  int urlNum=0;
    private static HashSet<String> urlList = new HashSet<>();
    private static String sectionUrl;
    private static int sectionNum = 0;
    private PageDao pDao;
    private int flag = 1;

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
                flag = 0;
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
    private void processLiType(Document doc, String linkUrl,String lasrUrl)throws Exception{
        Elements eles = doc.select("a.14");
        for (Element ele : eles) {
            String title = ele.text();
            String url = "http://www.teachercn.com" + ele.attr("href");
            //ToDO 结束条件
            if(url.equals(lasrUrl)||!url.contains("2015")){//爬取urllist结束条件
                writeToLinkFile(urlList);
                urlNum+=urlList.size();
                urlList.clear();
                return;
            }
            if (url.contains("teachercn")) {
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

        //TODO  翻页，根据不同页面规则不同
        String nextHref = "";
        if (flag == 0) {
            nextHref = linkUrl.substring(0, linkUrl.lastIndexOf("/") + 1)+"index_2.html";
            flag = 1;
            if (!linkUrl.equals(nextHref))
                getUrlList(nextHref, lasrUrl);
        }
        else
        {
            nextHref = linkUrl.substring(0, linkUrl.lastIndexOf("_") + 1) + (Integer.parseInt(linkUrl.substring(linkUrl.lastIndexOf("_") + 1, linkUrl.lastIndexOf(".html"))) + 1) + ".html";
            if (!linkUrl.equals(nextHref))
                getUrlList(nextHref, lasrUrl);
        }

    }
}
