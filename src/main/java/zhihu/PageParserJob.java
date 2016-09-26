package main.java.zhihu;

import main.java.general.Comment;
import main.java.ifeng.PageCrawlJob;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Element dateEle=doc.select("a.answer-date-link").first();

        if(dateEle==null){
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateStr=sdf.format(dt);
            return reformat(dateStr);
        }
        dateStr=dateEle.text();
        Pattern pattern=Pattern.compile("^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$");
        Matcher matcher=pattern.matcher(dateStr);
        if(matcher.find()&&matcher.groupCount()>0){
            dateStr=dateStr.substring(matcher.start(),matcher.end());
        }else {
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateStr=sdf.format(dt);
        }
        return reformat(dateStr);
    }
    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";
        Element img=doc.select("div#zh-question-detail  div  img").first();
        if(img!=null){
            imgUrl="http:"+img.attr("src");
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
        publisherStr="知乎";

            return reformat(publisherStr);
    }

    /*获取标题字段*/
    public static String parseTitle(String url, Document doc) {
        String titleStr = "";

        Element titleDiv = doc.select("h2.zm-item-title").first();
        if(titleDiv!=null){
            titleStr=titleDiv.text();
        }
        return reformat(titleStr);

    }

    public static void parseContent(StringBuffer content, String url, Document doc) {
        Element contentDiv=doc.select("div#zh-question-detail  div").first();
        content.append(contentDiv.text());
        System.out.println();
    }

    public static ArrayList<Comment> parseComments(String url) throws  Exception{
        return  new ParseComments(url).getComments();

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
