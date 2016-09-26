package main.java.zhiwang;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;


public class Crawler {

    private HashMap<String, String> cookies;
    private String sleepLabel = "<p><label>验证码：</label><input id=\"CheckCode\" name=\"CheckCode\" type=\"text\" /><input type=\"button\" value=\"提交\" onclick=\"javascript:CheckCodeSubmit()\" /></p>";


    public Crawler() {

        cookies = new HashMap<String, String>();
        cookies.put("ASP.NET_SessionId", "md4d3obwenau3c45mfbmjczn");
        cookies.put("RsPerPage", "50");

    }


    public static void main(String[] args) throws Exception {

        Crawler crawler = new Crawler();
        System.out.println(crawler.getDocumentByUrl("http://epub.cnki.net/kns/brief/brief.aspx?ctl=fe99fe27-e20c-464f-90a6-e2dd8f1471b2&dest=%u5206%u7EC4%uFF1A%u53D1%u8868%u5E74%u5EA6%20%u662F%202015&action=5&dbPrefix=SCDB&PageName=ASP.brief_default_result_aspx&Param=%E5%B9%B4+%3d+%272015%27&SortType=%E5%B9%B4&ShowHistory=1").body());

    }

    public Document getDocumentByUrl(String url){

        Document document;
        Connection connection = Jsoup.connect(url);
        Response response;
        int tryCount = 0;
        while(true){
            try {
                response = connection.ignoreContentType(true).method(Method.GET).cookies(cookies).execute();
                document = response.parse();
                String content = document.body().toString();
                if(content.contains(sleepLabel)||(content.length()>1000&&content.contains("GridTableContent")))
                    break;
                else
                    continue;
            } catch (IOException e) {
                tryCount++;
                System.out.println("try again!");
                if(tryCount>6)
                    System.out.println("请刷新知网首页！！！");
                continue;
            }
        }

        return document;

    }


}
