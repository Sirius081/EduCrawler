package main.java.sina;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import main.java.general.Comment;
import main.java.general.BasicCrawler;
import javax.xml.stream.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 读取新浪爬取文章XML文件并添加评论数据
 * Created by leyi on 15/1/12.
 */
public class ParseComments {


    private static int sourceId=34;

    public ArrayList<Comment> getComments(String url){
        System.out.println("Add comments for "+url);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String meta = getCmtMeta(url);
        if("".equals(meta)){
            return comments;
        }
        String cmtID = getCmtID(meta);
        String channel = getChannel(meta);

        String content = tryCommentGet(cmtID,channel);
        JSONObject jsonObject = JSONObject.fromObject(content);
        jsonObject = jsonObject.getJSONObject("result");

        JSONArray array=null;
        if(jsonObject.containsKey("cmntlist")){
            array = jsonObject.getJSONArray("cmntlist");
        }

        if(array!=null){
            Iterator<JSONObject> iter = array.iterator();
            while (iter.hasNext()){
                JSONObject obj = iter.next();

                String text="",user_id="",user_gender="0",user_region="",time="",user_ip="",user_type="";

                if(obj.containsKey("ip")){
                    user_ip=obj.getString("ip");
                }
                if(obj.containsKey("area")) {
                    user_region = obj.getString("area");
                }
                if(obj.containsKey("content")) {
                    text=reformat(obj.getString("content"));
                }
                if(obj.containsKey("nick")){
                    user_id=reformat(obj.getString("nick"));
                }
                if(obj.containsKey("time")){
                    time = obj.getString("time");
                }
                if(obj.containsKey("usertype")){
                    user_type = obj.getString("usertype");
                }

                Comment cmt = new Comment(text,time,user_id,user_gender,user_region,url,sourceId);

                comments.add(cmt);
            }
        }

        return comments;
    }

    public String tryCommentGet(String cmt_id,String channel){
        String url="http://comment5.news.sina.com.cn/page/info?version=1&format=json&channel="+channel+
                "&newsid="+cmt_id+"&group=0&compress=1&ie=gbk&oe=gbk&page=1&page_size=200";
        String content = new BasicCrawler().crawlPage(url);
        return content;

    }


    public String getCmtMeta(String url){
        String content = new BasicCrawler().crawlPage(url);
        Document doc = Jsoup.parse(content);
        Elements eles = doc.select("meta[name=comment]");
        if(eles!=null && eles.size()>0){
            Element ele = eles.get(0);
            String chanID = ele.attr("content");
            String[] temp = chanID.split(":");
            if(temp.length>1){
                return chanID;
            }
        }
        return "";
    }

    public String getChannel(String meta){
        if(!"".equals(meta)){
            return meta.split(":")[0];
        }
        return "";
    }

    public String getCmtID(String meta){
        if(!"".equals(meta)){
            return meta.split(":")[1];
        }
        return "";
    }

    public static void main(String[] args) throws IOException, XMLStreamException {
        //ParseComments sina = new ParseComments();
//        sina.getComments("http://edu.sina.com.cn/gaokao/2015-01-12/0759453900.shtml");
       // sina.addComments("/Users/leyi/Projects/edu/sina/oﬃcial/pages.xml","/Users/leyi/Projects/edu/sina/oﬃcial/pagesnew.xml");
    }
    public static String reformat(String message) {
        StringBuilder sb = new StringBuilder("");

        try {
            byte[] utf8 = message.getBytes("UTF-8");
            message = new String(utf8,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


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
    public static String parseUnixTime(String unixTime){
        Long timestamp = Long.parseLong(unixTime);
        if(unixTime.length()==10){
            timestamp=timestamp*1000;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
    }
}
