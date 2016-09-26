package main.java.YunNanEdu;

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
        Element dateDiv = doc.select("span.Article_tdbgall").first();
        String s=dateDiv.text();
        if(s.contains("更新时间：")){
            String[] temp=s.split("更新时间：");
            dateStr=temp[temp.length-1];
        }
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

        Element bk = doc.select("span.Article_tdbgall").first();
        String s=bk.text();
        String source="";
        if(s.contains("文章来源：")&&s.contains("点击数：")) {
            source = s.substring(s.indexOf("文章来源：") + 5, s.indexOf("点击数：") - 4);
        }
        if(source.equals("")||source.equals("原创")){
            return "云南教育网";
        }
//        String s=bk.text();
//        String []temp=s.split("\\[来源\\]");
//        s=temp[1];
//        temp=s.split("\\[发布时间\\]");
//        s=temp[0];
//        if(publisher!=null)
//        return reformat(publisher.text());
//        else
            return reformat(source);
    }

    /*获取标题字段*/
    public static String parseTitle(String url, Document doc) {
        String titleStr = "";
        Element titleDiv = doc.select("span.main_ArticleTitle").first();
        return reformat(titleDiv.text());

    }
    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";
        Elements paras=doc.select("p.MsoNormal");
        Element imgEle;
        for(Element para:paras){
            imgEle=para.select("img").first();
            if(imgEle!=null){
                imgUrl=imgEle.attr("src");
                break;
            }
        }
        return imgUrl;
    }
    public static void parseContent(StringBuffer content, String url, Document doc) {
            Elements paras = doc.select("p.MsoNormal");
            for(Element para : paras) {
                content.append(para.text()).append("\n");
            }

//            Elements nextPage = doc.select("div.main_fy a.red2");
//        //TODO  翻页，根据不同页面规则不同(没找到有翻页)
//        if(nextPage.size()==3){
//            baseUrl=url;
//        }
//        for(Element href : nextPage) {
//            String s=href.text();
//            s=s.substring(1,s.length()-1);
//            if(s.equals("下一页")){
//                String nextHref = href.attr("href");
//                    if(!nextHref.equals(baseUrl)) {
//                        logger.info("Get next page:" + nextHref);
//                        nextHref= baseUrl+nextHref;
//                    }
//
//                String nextContent = BasicCrawler.crawlPage(nextHref, "utf-8");
//                Document nextdoc = Jsoup.parse(nextContent);
//                parseContent(content, nextHref, nextdoc);
//                }
//        }
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
