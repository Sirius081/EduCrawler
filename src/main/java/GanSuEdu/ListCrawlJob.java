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
        //��ȡ�б�ҳ��
        logger.info("Start crawl list page:" + linkUrl);
        String content = "";
        content = new BasicCrawler().crawlPage(linkUrl, "gbk");
        //TODO  ���������б�ҳ�棬��ȡ��Ҫ��ȡҳ��URL
        Document doc = Jsoup.parse(content);
        processLiType(doc, linkUrl, lastUrl);

    }
    /**
     * Type 1 Page
     */
    private void processLiType(Document doc, String linkUrl,String lasrUrl)throws Exception{
        Elements eles = doc.select("div.page_left_list2 li");//�����б��е����ű��⼰url
        for(Element ele : eles) {
            Element href = ele.select("a[href]").first();

            if(href!=null){
            String url = href.attr("href");
            String date=ele.select("span").first().text();

                if(url.equals(lasrUrl)||date.contains("2014")){//��ȡurllist��������
                    writeToLinkFile(urlList);
                    urlNum+=urlList.size();
                    urlList.clear();
                    return;
                }

                if(url.contains("gsedu.cn")){//urllist��ʼ����
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

        //TODO  ��ҳ�����ݲ�ͬҳ�����ͬ
        Element nextPage = doc.select("span.NaviBar a[title=��һҳ]").first();
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
