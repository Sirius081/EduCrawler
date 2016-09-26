package main.java.ifeng;

import main.java.general.BasicCrawler;
import main.java.general.Comment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by leyi on 14/12/5.
 */
public class PageParserJob {
    private static Logger logger = Logger.getLogger(PageCrawlJob.class.getName());

    /*获取时间字段 */
    public static String parseDate(String url, Document doc) {
        String dateStr="";
        Element dateDiv = doc.select("span.ss01,div.title h4").first();
        return reformat(dateDiv.text());
    }

    /*获取发布来源*/
    public static String parsePublisher(String url, Document doc) {
        String publisherStr="";
        Element publisher = doc.select("span.ss03").first();
        if(publisher!=null)
        return reformat(publisher.text());
        else
            return "凤凰网教育";
    }

    /*获取标题字段*/
    public static String parseTitle(String url, Document doc) {
        String titleStr = "";
        Element titleDiv = doc.select("h1#artical_topic,div.title h1").first();
        return reformat(titleDiv.text());

    }

    public static String parseContent(StringBuffer content, String url, Document doc) throws Exception{
            Elements paras = doc.select("div#main_content p,p.text_con");
            for(Element para : paras) {
                content.append(para.text()).append("\n");
            }
            Elements nextpage = doc.select("div.next a[id=pagenext]");
            if(nextpage.size()>0){
                String nextUrl = nextpage.first().attr("href");
                if(nextUrl.startsWith("http://edu.ifeng.com/a/")){
                    logger.info("Get next page:"+nextUrl);
                    String nextContent = new BasicCrawler().crawlPage(nextUrl,"utf-8");
                    Document nextdoc = Jsoup.parse(nextContent);
                    parseContent(content,nextUrl,nextdoc);
                }
            }
            return content.toString();
    }


    //获取文章内的图片
    public static String parseFirstImg(String url,Document doc)throws Exception{
        String imgUrl="";
        Element img=doc.select("div#main_content img").first();
        if(img!=null){
            if(img.attr("src").contains("http"))
                imgUrl=img.attr("src");
        }
        return imgUrl;
    }
    //获取评论内容
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
