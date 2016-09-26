package main.java.sina;
import main.java.general.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by song on 2015/5/24.
 */
public class PageParserJob {
    private static Logger logger = Logger.getLogger(PageParserJob.class.getName());

    // 获取时间字段

    public static String parseDate(String url, Document doc)  {
        Elements dateDiv = doc.select("span#pub_date,span.time");
        String dateStr=dateDiv.text();
        return reformat(dateStr);
    }

    /*获取标题字段*/
    public static String parseTitle(String url, Document doc) {
        Elements titleDiv = doc.select("h1#artibodyTitle,h2.titName");
        String titleStr=titleDiv.text();
        return reformat(titleStr);
    }
    /**
     * 获取发布来源
     * @param url
     * @param doc
     * @return
     */
    public static String parsePublisher(String url, Document doc){
        Element publisherDiv = doc.select("span#media_name").first();
        if(publisherDiv==null)
            return "新浪博客";
        else {
            String publisherStr = publisherDiv.text();
            return reformat(publisherStr);
        }
    }
    //获取正文内容
    public static String parseContent(String url, Document doc) {
            Elements contentDiv= doc.select("div#artibody p,div.articalContent");
            return reformat(contentDiv.text());
    }

    //获取文章内的图片
    public static String parseFirstImg(String url,Document doc)throws Exception{
        String imgUrl="";
        Element img=doc.select("div.img_wrapper img").first();
        if(img!=null){
            if(img.attr("src").contains("http"))
                imgUrl=img.attr("src");
        }
        return imgUrl;
    }
    public static ArrayList<Comment> parseComments(String url) {
        ArrayList<Comment> comments=new ParseComments().getComments(url);
        return comments;

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
