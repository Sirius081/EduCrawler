package main.java.zhihu;


import main.java.general.BasicCrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import main.java.general.PageDao;


public class ListCrawlJob {
    private static Logger logger = Logger.getLogger(ListCrawlJob.class.getName());
    private static final int sourceId = 51;
    public static int getUrlNum() {
        return urlNum;
    }
    static  int urlNum=0;
    private static HashSet<String> urlList = new HashSet<>();
    private static String sectionUrl;
    private static int sectionNum = 0;
    private PageDao pDao;
    private int currentNextPage=11462 ;//当前页的下一页的页数
    public ListCrawlJob() {
        pDao = new PageDao();
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

    private void getUrlList(String linkUrl, String lastUrl) throws Exception{
        try{
            logger.info("Start crawl list page:" + linkUrl);
            String content = "";
            content = new BasicCrawler().crawlPage(linkUrl, "utf-8");
            Document doc = Jsoup.parse(content);
            processLiType(doc, linkUrl, lastUrl);
        }catch(StackOverflowError e){

        }


    }
    /**
     * Type 1 Page
     */
    private int processLiType(Document doc, String linkUrl,String lastUrl)throws Exception{
        try{
            Elements eles = doc.select("div.feed-item");//?????��??��????????url
            for(Element ele : eles) {
                Element href = ele.select("a.question_link").first();
                Element dateEle=ele.select("span.time").first();
                if(href!=null){
                    String url = "http://www.zhihu.com"+href.attr("href");
                    String date=dateEle.text();

                    if(date.equals("9 月前")){//???urllist????????

                        writeToLinkFile(urlList);
                        urlNum+=urlList.size();
                        urlList.clear();
                        return 1;
                    }

                    if(url.contains("zhihu.com")){//urllist???????
                        urlList.add(url);
                        sectionNum++;
                        if(sectionNum==1){
                            try {
                                //pDao.updateLastUrl(sourceId, sectionUrl, url);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        finally {
            String nextHref=linkUrl.replaceAll("page=\\d*","page="+currentNextPage++);
            writeToLinkFile(urlList);
            urlNum+=urlList.size();
            urlList.clear();
            getUrlList(nextHref,lastUrl);
        }

//        Element nextPage = doc.select("div.border-pager a").last();
//        if(nextPage!=null){
//            String nextHref="http://www.zhihu.com/topic/19553176/questions"+nextPage.attr("href");
//            writeToLinkFile(urlList);
//            urlNum+=urlList.size();
//            urlList.clear();
//            getUrlList(nextHref,lastUrl);
//        }
        return 1;
    }



    /**
     * Write URL list to link files
     * @param urlList
     */
    private void writeToLinkFile(HashSet<String> urlList) throws SQLException {

        pDao.insertList(urlList,sourceId);
    }
}
