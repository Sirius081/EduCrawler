package main.java.zhihu;
import main.java.general.BasicCrawler;
import main.java.general.Comment;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sirius-.- on 2015/9/16.
 */
public class ParseComments {
    private HashMap<String, String> cookies;
    private HashMap<String,String> data;
    private int nowOffset=0;
    private String url;
    private String params="";
    private static int sourceId=51;
    private static final String JSONUrl="http://www.zhihu.com/node/QuestionAnswerListV2";
    public ParseComments(String url)throws Exception{
        this.url=url;

        cookies = new HashMap<>();
        cookies.put("_za","f48d0d70-0acc-422a-9767-5fc3823e1036");
        cookies.put("q_c1","2d9b8ca227c6475cac883954443c5068|1440118767000|1436347526000");
        cookies.put("_xsrf","1552a87ca18f570aa659321fdd452f87");
        cookies.put("cap_id","\"YjBkYzVjYjRiN2I5NDcyZTk5NjBmZDliNWFlNzgxMGI=|1442322237|0a2723107b3d441167362b04a10b75d0091477b1\"");
        cookies.put("z_c0","\"QUFBQVh2UWRBQUFYQUFBQVlRSlZUVU9rSDFacjM3SjNYT21hYjZob2RiSzBlM2ZqcFI3SENnPT0=|1442322244|bfd269d17d3df5f0d775da9d1b336d879b56cc3a\"");
        cookies.put("_ga","GA1.2.1327342179.1436347588");
        cookies.put("__utmt","1");
        cookies.put("__utma","155987696.1327342179.1436347588.1442390700.1442390700.1");
        cookies.put("__utmb","155987696.3.10.1442390700");
        cookies.put("__utmc","155987696");
        cookies.put("__utmz","155987696.1442390700.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
        cookies.put("__utmv","51854390.100-1|2=registration_date=20130909=1^3=entry_date=20130909=1");
        String content=new BasicCrawler().crawlPage(url, "utf-8");
        Document doc= Jsoup.parse(content);

        Element initDataEle=doc.select("div[data-init]").first();

        if(initDataEle!=null){
            params =initDataEle.attr("data-init");
            params = params.substring(10, params.indexOf("},") + 1);
            data=new HashMap<>();
            data.put("_xsrf","1552a87ca18f570aa659321fdd452f87");
            data.put("method", "next");
            data.put("params", params);
            nowOffset=20;
        }

    }

    private void setParameter() {
        data.clear();
        data.put("_xsrf","1552a87ca18f570aa659321fdd452f87");
        data.put("method", "next");
        params=params.replaceAll("\"offset\":\\s\\d*","\"offset\": "+nowOffset);
        data.put("params", params);
        nowOffset=nowOffset+50;
    }
    public static void main(String[] args)  {
        try{
            ParseComments jli = new ParseComments("http://www.zhihu.com/question/35624941");
            jli.getComments();
        }catch(Exception e){

        }


    }
    //TODO 解析评论
    public ArrayList<Comment> getComments() throws Exception{

        ArrayList<Comment> comments=new ArrayList<>();
        if(params.equals("")){
            return  comments;
        }
        JSONArray commentJSOnArr=getJSONByUrl();
        Iterator<String> iter;
        Pattern pattern=Pattern.compile("^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$");//日期的格式
        Matcher matcher;
        int num=0;
        while(commentJSOnArr.size()>0){
            iter=commentJSOnArr.iterator();

            while(iter.hasNext()){
                String commentContent=iter.next();
                Document commentDoc=Jsoup.parse(commentContent);
                Element commentDiv=commentDoc.select("div.zm-item-answer").first();
                String text=commentDiv.select("div.zm-editable-content").first().text();//comment content
                String time=commentDiv.select("a.answer-date-link").first().text();//comment time
                matcher=pattern.matcher(time);
                if(matcher.find()&&matcher.groupCount()>0){
                    time=time.substring(matcher.start(),matcher.end());
                }else {
                    Date dt = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    time=sdf.format(dt);
                }

                Element userEle=commentDiv.select("h3.zm-item-answer-author-wrap").first();
                Element idEle=userEle.select("a[data-tip]:matchesOwn(.+)").first();
                String infoUrl="";

                if(idEle==null){//匿名用户
                    idEle=userEle;
                    System.out.println(++num+"\tcrawl user:"+"匿名用户");
                }else {
                    infoUrl="http://www.zhihu.com"+idEle.attr("href");//用户资料页面
                    System.out.println(++num+"\tcrawl user:"+infoUrl);
                }





                String id=idEle.text();                     //该comment的用户名

                String []info=getInfo(infoUrl).split("\\t");
                String gender=info[0];                      //该comment的用户的性别 1=male  2=female
                String region="";                           //用户的地区
                if(info.length>1)
                region=info[1];

                //public Comment(String text, String time, String id, String gender, String region,String url)
                Comment comment=new Comment(text,time,id,gender,region,url,sourceId);
                comments.add(comment);
            }
            commentJSOnArr=getJSONByUrl();
        }

        return comments;
    }

    /**
     *
     * @param url
     * @return 用户性别+\t+地区
     */
    private String getInfo(String url) throws Exception{
        String gender="";
        String region="";
        if(url.equals("")){
            return "0\t";
        }
        String content =new BasicCrawler().crawlPage(url,"utf-8");
        Document doc=Jsoup.parse(content);
        Element genderEle=doc.select("span.item i").first();
        Element regionEle=doc.select("span.location a.topic-link").first();
        if(genderEle!=null){
            //1=male  2=female
            gender=genderEle.attr("class").equals("icon icon-profile-male")?"1":"2";
        }else{
            gender="0";
        }
        if(regionEle!=null){
            region=regionEle.text();
        }
        return gender+"\t"+region;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public  String getContentByUrl() throws IOException {
        String content=null;

        Connection connection = Jsoup.connect(JSONUrl);
        //connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        //设置cookie和post上面的map数据
        connection.header("Host","www.zhihu.com");
        connection.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
        connection.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        connection.header("Accept-Encoding", "gzip, deflate");
        connection.header("Connection", "keep-alive");
        connection.header("Cache-Control", "max-age=0");

        Connection.Response response = connection.ignoreContentType(true).method(Connection.Method.POST).data(data).cookies(cookies).timeout(30000).execute();
        //document = connection.ignoreContentType(true).method(Connection.Method.POST).data(data).cookies(cookies).timeout(30000).get();
        content=response.body();
        setParameter();
        return content;
    }

    public  JSONArray getJSONByUrl()throws Exception{
        String content =getContentByUrl();
        JSONArray jsonArray= JSONObject.fromObject(content).getJSONArray("msg");
        return jsonArray;
    }
}
