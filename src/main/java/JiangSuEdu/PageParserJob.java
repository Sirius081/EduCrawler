package main.java.JiangSuEdu;

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
        Elements dateP = doc.select("p.biao2 span");
        Element dateSpan=dateP.get(1);
        dateStr=dateSpan.text();
        return reformat(dateStr);
    }

    /**
     * 获取发布来源
     * @param url
     * @param doc
     * @return
     */
    public static String parsePublisher(String url, Document doc) {
        String publisherStr="";

        Element bk = doc.select("p.biao2 span").get(2);
        publisherStr=bk.text();


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
        Element titleDiv = doc.select("p.biao1").first();
        return reformat(titleDiv.text());

    }

    /**
     * get content
     * @param content
     * @param url
     * @param doc
     */
    public static void parseContent(StringBuffer content, String url, Document doc) {
        Element contentEle = doc.select("Content").first();
        String contentStr=contentEle.text();
        Document contentDoc=Jsoup.parse(contentStr,"gbk");
        Elements paras=contentDoc.select("P");
            for(Element para : paras) {

                    content.append(para.text()).append("\n");
            }

    }
    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";
        Element img=doc.select("Content img").first();
        if(img!=null){
            imgUrl="http://www.jsenews.com"+img.attr("src");
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
