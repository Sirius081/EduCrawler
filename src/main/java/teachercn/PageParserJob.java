package main.java.teachercn;

import main.java.general.BasicCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageParserJob {
    private static Logger logger = Logger.getLogger(PageParserJob.class.getName());
    //private static Pattern p=Pattern.compile("2015��\\d{1,2}��\\d{1,2}");
    /* ��ȡʱ���ֶ�*/
    public static String parseDate(String url, Document doc) {
        Elements dateDiv = doc.select("div#CC,div.text");
        String dateStr = dateDiv.text();

        dateStr = dateStr.substring(dateStr.indexOf("2015"), dateStr.indexOf("来源"));//��Դ
//        Matcher m=p.matcher(dateStr);
//        if(m.find()){
//            dateStr=m.group().replaceAll("[����]","-");
//        }
        dateStr=dateStr.replaceAll("[年月]", "-");
        dateStr=dateStr.substring(0,dateStr.length()-2);
        return dateStr;
    }


    /*��ȡ�����ֶ�*/
    public static String parseTitle(String url, Document doc) {

        String titleStr = "";
        Element titleDiv = doc.select("h1:not(.hidden)").first();
        return reformat(titleDiv.text());

    }

    /* ��ȡ������Դ*/
    public static String parsePublisher(String url, Document doc) {
        Elements publisherDiv = doc.select("div.text,Div.text");
        String str = publisherDiv.text();
        String publisherStr = str.substring(str.indexOf("来源"),str.lastIndexOf("作者"));
        if (publisherStr.length()<5)
            return "中教网";
        else
            return reformat(publisherStr);
    }

    //��ȡ��������
    public static void parseContent(StringBuffer contentStr,String url, Document doc) throws Exception{
        Elements contentDiv = doc.select("div#CC,div#PP,div#HH,div#ZZ,div#XX,div#UU,div#BB,div#OO,div#FF,div#AA,div#DD,div#EE,div#GG,div#JJ,div#RR,div#QQ,div#WW,div#MM,div#SS,div#NN,div#TT,div#II,div#KK,div#LL,div#VV,div#YY,div#jsjs");
        contentStr.append(contentDiv.text());
        Elements nextpage = doc.select("div[align=right]").select("a[href]");
        Elements currentDiv = doc.select("div[align=right] font[color=red]");
        if (nextpage != null && currentDiv != null) {
            for (Element e : nextpage) {
                Element currentD = currentDiv.get(1);
                int next = Integer.parseInt(e.text());
                int current = Integer.parseInt(currentD.text());
                if (next > current) {
                    String nextHref = url.substring(0, url.lastIndexOf("/") + 1) + e.attr("href");
                    logger.info("Get next page:" + nextHref);
                    String nextContent = new BasicCrawler().crawlPage(nextHref, "gb2312");
                    Document nextdoc = Jsoup.parse(nextContent);
                     parseContent(contentStr,nextHref, nextdoc);
                    break;
                }
            }
        }
    }
    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";
        Elements contentDiv=doc.select("div#CC,div#PP,div#HH,div#ZZ,div#XX,div#UU,div#BB,div#OO,div#FF,div#AA,div#DD,div#EE,div#GG,div#JJ,div#RR,div#QQ,div#WW,div#MM,div#SS,div#NN,div#TT,div#II,div#KK,div#LL,div#VV,div#YY,div#jsjs");
        Element img=contentDiv.first().select("img").first();
        if(img!=null){
                imgUrl="http://www.gsedu.cn"+img.attr("src");
        }
        return imgUrl;
    }

    //���и�ʽת��
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



