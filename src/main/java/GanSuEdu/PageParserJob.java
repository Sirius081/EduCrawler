package main.java.GanSuEdu;

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
    public static String parseDate(String url, Document doc) {
        String dateStr="";
        Element dateP = doc.select("div.page_content_title dl dd").first();
        int i=dateP.text().indexOf("阅读");
        String s=dateP.text();
        dateStr=dateP.text().substring(0, dateP.text().indexOf("阅读")-1);
        return reformat(dateStr);
    }
    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";
        Element img=doc.select("div.page_content img").first();
        if(img!=null){
            imgUrl="http://www.gsedu.cn"+img.attr("src");
        }
        return imgUrl;
    }
    /**
     * ��ȡ������Դ
     * @param url
     * @param doc
     * @return
     */
    public static String parsePublisher(String url, Document doc) {
        String publisherStr="";

        Element bk = doc.select("div.page_content_title dl dd").first();
        String temp=bk.text();
        publisherStr=temp.substring(temp.indexOf("出处:")+4,temp.indexOf("作者:")-1);
        if(publisherStr.equals("本站")){
            publisherStr="甘肃教育网";
        }



//        String s=bk.text();
//        String []temp=s.split("\\[��Դ\\]");
//        s=temp[1];
//        temp=s.split("\\[����ʱ��\\]");
//        s=temp[0];
//        if(publisher!=null)
//        return reformat(publisher.text());
//        else
            return reformat(publisherStr);
    }

    /*��ȡ�����ֶ�*/
    public static String parseTitle(String url, Document doc) {
        String titleStr = "";

        Element titleDiv = doc.select("div.page_content_title dl dt").first();
        if(titleDiv!=null){
            titleStr=titleDiv.text();
        }
        return reformat(titleStr);

    }

    public static void parseContent(StringBuffer content, String url, Document doc) {
        Elements contentEle = doc.select("div.page_content p");


            for(Element para : contentEle) {

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
