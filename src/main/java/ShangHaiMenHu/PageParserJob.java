package main.java.ShangHaiMenHu;

import main.java.general.BasicCrawler;
import main.java.ifeng.PageCrawlJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.logging.Logger;

/**
 * Created by leyi on 14/12/5.
 */
public class PageParserJob {
    private static Logger logger = Logger.getLogger(PageCrawlJob.class.getName());
    private static String baseUrl="";
    /**
     * 获取时间字段
     * @param url
     * @param doc
     * @return
     */
    public static String parseDate(String url, Document doc) {
        String dateStr="";
        Element dateDiv = doc.select("div.newsdetailread font").first();
        String s=dateDiv.text();
        return reformat(s);
    }

    /**
     * 获取发布来源
     * @param url
     * @param doc
     * @return
     */
    public static String parsePublisher(String url, Document doc) {
        String publisherStr="";

        Element article = doc.select("div#ivs_content").first();
        String content=article.text();
        if(content.contains("（摘自：")){
            publisherStr=content.substring(content.lastIndexOf("（摘自：") + 4, content.length() - 1);
        }else {
            publisherStr="上海教育门户网站";
        }

//        String s=bk.text();
//        String []temp=s.split("\\[来源\\]");
//        s=temp[1];
//        temp=s.split("\\[发布时间\\]");
//        s=temp[0];
//        if(publisher!=null)
//        return reformat(publisher.text());
//        else
            return reformat(publisherStr);
    }

    /*获取标题字段*/
    public static String parseTitle(String url, Document doc) {
        String titleStr = "";
        Element titleDiv = doc.select("div#ivs_title").first();
        return reformat(titleDiv.text());

    }

    public static void parseContent(StringBuffer content, String url, Document doc) {
        Element article = doc.select("div#ivs_content").first();
        content.append(article.text());
        //TODO  翻页，根据不同页面规则不同

    }

    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";
        Element img=doc.select("div#ivs_content img").first();
        if(img!=null){
            imgUrl="http://www.gsedu.cn"+img.attr("src");
        }
        return imgUrl;
    }
    /**
     * Reformat String literal
     */
    public static String reformat(String message) {
        StringBuffer sb = new StringBuffer("");

        message = message.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&apos;").replace("^", "").replace("\b", "");

        for (int i = 0; i < message.length(); i++) {
            char ch = message.charAt(i);
            if ((ch == 0x9) || (ch == 0xA) || (ch == 0xD)
                    || ((ch >= 0x20) && (ch <= 0xD7FF))
                    || ((ch >= 0xE000) && (ch <= 0xFFFD))
                    || ((ch >= 0x10000) && (ch <= 0x10FFFF)))
                sb.append(ch);
        }
        return sb.toString();
    }
}
