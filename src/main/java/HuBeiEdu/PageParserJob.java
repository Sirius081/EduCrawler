package main.java.HuBeiEdu;

import main.java.general.BasicCrawler;
import main.java.ifeng.PageCrawlJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
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
        Element dateDiv = doc.select("p.bk").first();
        String s=dateDiv.text();
        String [] temp=s.split("\\[.*?\\]");
        s=temp[3];
        s=s.substring(1,s.length());
        return reformat(s);
    }

    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";
        Element img=doc.select("div.zw img").first();
        if(img!=null){
            imgUrl="http://news.e21.cn"+img.attr("src");
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

        Element bk = doc.select("p.bk").first();
        String s=bk.text();
        String [] temp=s.split("\\[.*?\\]");
        publisherStr=temp[2];
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
        Element titleDiv = doc.select("div.zw h3").first();
        return reformat(titleDiv.text());

    }

    public static void parseContent(StringBuffer content, String url, Document doc)throws Exception {
            Elements paras = doc.select("div.zw p");
            for(Element para : paras) {
                if(!para.className().equals("bk"))
                content.append(para.text()).append("\n");
            }
        if(content.toString().equals("")){
            String contStr=doc.select("div.zw").text();
            String []temp=contStr.split("\\d{4}-\\d{2}-\\d{2}");
            content.append(temp[temp.length-1]);

        }
            Elements nextPage = doc.select("div.main_fy a.red2");
        //TODO  翻页，根据不同页面规则不同
        if(nextPage.size()==3){
            baseUrl=url;
        }
        for(Element href : nextPage) {
            String s=href.text();
            s=s.substring(1,s.length()-1);
            if(s.equals("下一页")){
                String nextHref = href.attr("href");
                    if(!nextHref.equals(baseUrl)) {
                        logger.info("Get next page:" + nextHref);
                        nextHref= baseUrl+nextHref;
                    }

                String nextContent = new BasicCrawler().crawlPage(nextHref, "utf-8");
                Document nextdoc = Jsoup.parse(nextContent);
                parseContent(content, nextHref, nextdoc);
                }
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
