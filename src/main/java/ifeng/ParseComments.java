package main.java.ifeng;

/**
 * Created by sirius-.- on 2015/9/14.
 */
import main.java.general.BasicCrawler;
import main.java.general.Comment;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class ParseComments {
    private static int sourceId=20;
    public ArrayList<Comment> getComments(String url){
        System.out.println("Add comments for "+url);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        int page=0;
        while (true){
            page++;
            String content = tryCommentGet(url,page);
            if(content.length()<100){
                break;
            }
            JSONObject jsonObject = JSONObject.fromObject(content);
            JSONArray array=null;
            if(jsonObject.containsKey("comments")){
                array = jsonObject.getJSONArray("comments");
            }

            if(array!=null){
                Iterator<JSONObject> iter = array.iterator();
                while (iter.hasNext()){
                    try {
                        JSONObject obj = iter.next();

                        String text = "", user_id = "", user_gender = "0", user_region = "", time = "", user_ip = "", user_type = "";

                        if (obj.containsKey("client_ip")) {
                            user_ip = obj.getString("client_ip");
                        }
                        if (obj.containsKey("ip_from")) {
                            user_region = obj.getString("ip_from");
                        }
                        if (obj.containsKey("comment_contents")) {
                            text = reformat(obj.getString("comment_contents"));
                        }
                        if (obj.containsKey("user_id")) {
                            user_id = reformat(obj.getString("user_id"));
                        }
                        if (obj.containsKey("create_time")) {
                            time = parseUnixTime(obj.getString("create_time"));
                        }
                        if (obj.containsKey("useragent")) {
                            user_type = obj.getString("useragent");
                        }

                        Comment cmt = new Comment(text,time,user_id,user_gender,user_region,url,sourceId);

                        comments.add(cmt);
                    }catch (ClassCastException e){

                    }
                }
            }
        }


        return comments;
    }

    public String tryCommentGet(String url,int page){
        url = url.replace("/","%2F").replace(":","%3A").trim();
        url="http://comment.ifeng.com/get.php?docurl="+url+
                "&format=json&job=1&pagesize=100&p="+page+"&callback=newCommentCallback";
        System.out.println(url);
        String content = new BasicCrawler().crawlPage(url);
        return content;

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
