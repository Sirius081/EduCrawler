package main.java.snedu;

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
     * ��ȡʱ���ֶ�
     * @param url
     * @param doc
     * @return
     */
    public static String parseDate(String url, Document doc) throws Exception{
        String dateStr="";
        Element dateEle = doc.select("span.date").first();
        dateStr=dateEle.text();
        dateStr=dateStr.replaceAll("[年月]", "-");
        dateStr=dateStr.substring(3, dateStr.length() - 1);
        return reformat(dateStr);
    }
    public static String parseFirstImg(String url,Document doc) throws Exception{
        String imgUrl="";
        Element img=doc.select("div#MyContent img").first();
        if(img!=null){
            imgUrl=img.attr("src");
        }
        return imgUrl;
    }
    /**
     * ��ȡ������Դ
     * @param url
     * @param doc
     * @return
     */
    public static String parsePublisher(String url, Document doc)throws Exception {
        String publisherStr="";
        Element bk = doc.select("span.where").first();
        String temp=bk.text();
        publisherStr=temp.substring(3,temp.length());
        return reformat(publisherStr);
    }

    /*��ȡ�����ֶ�*/
    public static String parseTitle(String url, Document doc) throws Exception{
        String titleStr = "";
        Element titleDiv = doc.select("div.ArticleTitle").first();
        titleStr=titleDiv.text();
        return reformat(titleStr);

    }

    public static void parseContent(StringBuffer content, String url, Document doc) {
        Element contentEle = doc.select("div#MyContent").first();
        content.append(contentEle.text());
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
