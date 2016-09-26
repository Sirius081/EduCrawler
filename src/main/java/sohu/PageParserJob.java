package main.java.sohu;

import main.java.general.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by song on 2015/5/6.
 */
public class PageParserJob {
    private static Logger logger = Logger.getLogger(PageParserJob.class.getName());
    /**
     * 获取时间字段
     * @param url
     * @param doc
     * @return
     */
    public static String parseDate(String url, Document doc) {
        Element dateDiv = doc.select("span.Time").first();
        return reformat(dateDiv.text());
    }

    /*获取标题字段*/
    public static String parseTitle(String url, Document doc) {

        String titleStr = "";
        Element titleDiv = doc.select("div.news-title h1").first();
        return reformat(titleDiv.text());

    }
    //获取评论内容
    public static ArrayList<Comment> parseComments(String url) throws Exception{
        ArrayList<Comment> comments=new ParseComments().getComments(url);
        return comments;

    }
    /**
     * 获取发布来源
     */
    public static String parsePublisher(String url, Document doc) {
        Element publisherDiv = doc.select("span.writer").first();
        String publisherStr = publisherDiv.text();
        publisherStr = publisherStr.replace("来源：","");
        return reformat(publisherStr);
    }
    //获取正文内容
    public static String parseContent(String url, Document doc) {
        Elements contentDiv= doc.select("div#contentText");
        return reformat(contentDiv.text());

    }

    //获取文章内的图片
    public static String parseFirstImg(String url,Document doc)throws Exception{
        String imgUrl="";
        Element img=doc.select("div#contentText img").first();
        if(img!=null){
            if(img.attr("src").contains("http"))
                imgUrl=img.attr("src");
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
