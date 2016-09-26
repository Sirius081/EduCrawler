package main.java.XinHuaEdu;

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
        Element dateEle = doc.select("span.time").first();
        if(dateEle==null){
            dateEle=doc.select("span#pubtime").first();
        }
        dateStr=dateEle.text();
        String []temp=dateStr.split(" ");
        dateStr=temp[0];
        dateStr=dateStr.replaceAll("[年月]","-");
        //dateStr.replaceAll("��","-");
        dateStr=dateStr.replaceAll("日","");
        return reformat(dateStr);
    }

    /**
     * ��ȡ������Դ
     * @param url
     * @param doc
     * @return
     */
    public static String parsePublisher(String url, Document doc) {
        String publisherStr="";

        Element bk = doc.select("em#source").first();

        if(bk==null){
            bk=doc.select("span#source").first();
            if(bk!=null){
                publisherStr=bk.text();
                publisherStr=publisherStr.substring(3, publisherStr.length());
                return reformat(publisherStr);
            }else {
                publisherStr="�»���";
                return reformat(publisherStr);
            }
        }
        publisherStr=bk.text();
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
    public static String parseTitle(String url, Document doc)throws Exception{
        String titleStr = "";
        Element titleDiv = doc.select("h1#title").first();
        if(titleDiv==null){
            titleDiv=doc.select("span#title").first();
            if (titleDiv==null){
                System.out.println(url);
            }
        }
        return reformat(titleDiv.text());
    }

    public static void parseContent(StringBuffer content, String url, Document doc)throws Exception {
            Elements paras = doc.select("div.article p");
            if(paras.size()==0){
                paras=doc.select("div#content p");
            }
            for(Element para : paras) {
                content.append(para.text()).append("\n");
            }

            Elements nextPages = doc.select("a.nextpage");
        //TODO  ��ҳ�����ݲ�ͬҳ�����ͬ(û�ҵ��з�ҳ)

            if(nextPages!=null){
                for(Element nextPage:nextPages){
                    if(nextPage.text().equals("��һҳ")){
                        String nextHref = nextPage.attr("href");
                        logger.info("Get next page:" + nextHref);
                        nextHref= baseUrl+nextHref;

                        String nextContent = new BasicCrawler().crawlPage(nextHref, "utf-8");
                        Document nextdoc = Jsoup.parse(nextContent);
                        parseContent(content, nextHref, nextdoc);
                        break;
                    }
                }

            }

    }
    public static String parseFirstImg(String url,Document doc){
        String imgUrl="";

        Element img=doc.select("div.article img").first();

        if(img!=null){
            img=doc.select("div#content img").first();
            if(img!=null){
                StringBuffer dateStr=new StringBuffer(parseDate(url,doc));
                //2015-07-27   2015-07/27
                dateStr.setCharAt(7,'/');
                imgUrl="http://education.news.cn/"+dateStr+"/"+img.attr("src");
            }
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
