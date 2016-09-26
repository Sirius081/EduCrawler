package main.java.sohu;


import main.java.general.Comment;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import main.java.general.BasicCrawler;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by leyi on 15/1/13.
 */
public class ParseComments {
    private static int sourceId=36;
    int cmt_sum=0;





    public ArrayList<Comment> getComments(String url) throws Exception{
        System.out.println("Add comments for "+url);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String topicID = getTopicID(url);

        String content = tryCommentGet(topicID);
        JSONObject jsonObject = JSONObject.fromObject(content);
        jsonObject = jsonObject.getJSONObject("listData");

        if(jsonObject!=null && jsonObject.containsKey("cmt_sum")){
            cmt_sum = jsonObject.getInt("cmt_sum");
        }

        JSONArray array;
        if(jsonObject!=null && jsonObject.containsKey("comments")){
            array=jsonObject.getJSONArray("comments");
        }
        else{
            return comments;
        }

        Iterator<JSONObject> iter = array.iterator();
        while(iter.hasNext()){
            JSONObject comment = iter.next();
            String text="",user_id="",user_gender="0",user_region="",time="",user_ip="";
            if(comment.containsKey("content")){
                text =reformat(comment.getString("content"));
            }
            if(comment.containsKey("create_time")){
                time =parseUnixTime(comment.getString("create_time"));
            }
            if(comment.containsKey("user_id")){
                user_id=comment.getString("user_id");
            }
            if(comment.containsKey("ip")){
                user_ip=comment.getString("ip");
            }
            if(comment.containsKey("ip_location")){
                user_region = stripHtml(comment.getString("ip_location"));
            }

            Comment cmt = new Comment(text,time,user_id,user_gender,user_region,url,sourceId);
            comments.add(cmt);
        }
        return comments;
    }

    public static String getTopicID(String url) {
        String last = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf('.'));
        last = last.replace("n","");
        return last;
    }

    public static String tryCommentGet(String cmt_id) throws Exception{
        String url="http://changyan.sohu.com/node/html?t="+System.currentTimeMillis()+
                "callback=fn&appid=cyqemw6s1&client_id=cyqemw6s1&page_size=100&topicsid=" +cmt_id;
        String content = new BasicCrawler().crawlPage(url, "UTF-8");
        return content;
    }



    public static String stripHtml(String text){
        return text.replaceAll("<[^>]*>","");
    }
    /**
     * Reformat String literal
     */
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
