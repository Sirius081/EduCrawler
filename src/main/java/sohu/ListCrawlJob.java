package main.java.sohu;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by song on 2015/5/6.
 */
public class ListCrawlJob {
    private static Logger logger = Logger.getLogger(ListCrawlJob.class.getName());
    private static final int sourceId =36;
    public static int getUrlNum() {
        return urlNum;
    }
    int number=100;
    int flag=1;
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
                flag=0;
                number=number-1;
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
        content = new BasicCrawler().crawlPage(linkUrl, "gb2312");
        //TODO  解析文章列表页面，获取需要爬取页面URL
        Document doc = Jsoup.parse(content);
        processLiType(doc, linkUrl, lastUrl);

    }

    /**
     * Type 1 Page
     */
    private void processLiType(Document doc, String linkUrl, String lastUrl)throws Exception{

        List<String> urlList = new ArrayList<String>();
        Elements eles = doc.select("div.published span.content-title");
        String url="";
        for(Element ele : eles) {
            Element href = ele.select("a[href]").first();
            url =href.attr("href");
            if (url.equals(lastUrl)) {//爬取urllist结束条件
                urlList.clear();
                return;
            }
            if (url.contains("2015")) {
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
        int count=0;
        if(flag==0) {
            Element e = doc.select("div.pages").first().select("script").first();
            String s=e.toString();
            s.replaceAll("\\n", "");
            Pattern p = Pattern.compile("var maxPage = (.*);");
            Matcher m = p.matcher(s);
            while(m.find()){
               count= Integer.parseInt(m.group(1));
            }
            int pageNum=count-1;
            number=number-1;
            String  nextHref=linkUrl.substring(0,linkUrl.lastIndexOf("/")+1)+"index_"+pageNum+".shtml";
            flag=1;
            if (!linkUrl.equals(nextHref))
                getUrlList(nextHref,lastUrl);
        }
        else {
            if(number>0)
            {
                number--;
                String nextHref = linkUrl.substring(0, linkUrl.lastIndexOf("_")+1) + (Integer.parseInt(linkUrl.substring(linkUrl.lastIndexOf("_")+1,linkUrl.lastIndexOf(".shtml")))-1)+".shtml";
                if (!linkUrl.equals(nextHref))
                getUrlList(nextHref,lastUrl);

            }
            else
            {
                number=100;
                return;
            }
        }
    }
}
