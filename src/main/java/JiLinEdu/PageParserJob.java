package main.java.JiLinEdu;

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
        Element dateDiv = doc.select("div.text").first();
        String s=dateDiv.text();
        String date=s.substring(0, 10);
        return reformat(date);
    }

    /**
     *first Img
     * @param url
     * @param doc
     * @return
     */
    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";
        Element img=doc.select("div.content img").first();
        if(img!=null){
            imgUrl="http://www.jledu.gov.cn"+img.attr("src");
        }
        return imgUrl;
    }
    /**
     * 获取发布来源
     * @param url
     * @param doc
     * @return
     */
    public static String parsePublisher(String url, Document doc) {
        String publisherStr="";

        Element bk = doc.select("div.text").first();
        String s=bk.text();
        String source=s.substring(s.indexOf("来源：")+3,s.indexOf("来源：")+11);
//        String s=bk.text();
//        String []temp=s.split("\\[来源\\]");
//        s=temp[1];
//        temp=s.split("\\[发布时间\\]");
//        s=temp[0];
//        if(publisher!=null)
//        return reformat(publisher.text());
//        else
            return reformat(source.trim());
    }

    /*获取标题字段*/
    public static String parseTitle(String url, Document doc) {
        String titleStr = "";
        Element titleDiv = doc.select("h1#articletit").first();
        return reformat(titleDiv.text());

    }

    public static void parseContent(StringBuffer content, String url, Document doc) {
            Elements paras = doc.select("div.content p");
            for(Element para : paras) {
                content.append(para.text()).append("\n");
            }
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
