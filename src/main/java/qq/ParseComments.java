package main.java.qq;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import main.java.general.Comment;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.java.general.BasicCrawler;
/**
 * Created by leyi on 15/1/10.
 */
public class ParseComments {

    private static int sourceId=32;


    public void addComments(String xmlFile,String outputfile) throws IOException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEventWriter writer;

        if(xmlFile.endsWith("xml")){
            InputStream stream = new FileInputStream(xmlFile);

            XMLEventReader eventReader = factory.createXMLEventReader(stream);

            writer = outputFactory.createXMLEventWriter(new FileWriter(outputfile));

            String url="";

            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                writer.add(event);

                if(event.getEventType() == XMLEvent.START_ELEMENT){
                    if(event.asStartElement().getName().toString().equals("url")){
                        event = eventReader.nextEvent();
                        writer.add(event);
                        if(event.getEventType() == XMLEvent.CHARACTERS){
                             url = event.asCharacters().getData();
                        }
                    }
                    else if(event.asStartElement().getName().toString().equals("comments")) {
                        if(!"".equals(url)){
                            List<Comment> comments = getComments(url);
                            for(Comment cmt: comments) {
                                writer.add(eventFactory.createStartElement("",null,"comment"));
                                writer.add(eventFactory.createStartElement("",null,"user-id"));
                                writer.add(eventFactory.createCharacters(cmt.getId()));
                                writer.add(eventFactory.createEndElement("",null,"user-id"));
                                writer.add(eventFactory.createStartElement("",null,"user-gender"));
                                writer.add(eventFactory.createCharacters(cmt.getGender()));
                                writer.add(eventFactory.createEndElement("",null,"user-gender"));
                                writer.add(eventFactory.createStartElement("",null,"user-region"));
                                writer.add(eventFactory.createCharacters(cmt.getRegion()));
                                writer.add(eventFactory.createEndElement("",null,"user-region"));
                                writer.add(eventFactory.createStartElement("",null,"text"));
                                writer.add(eventFactory.createCharacters(cmt.getText()));
                                writer.add(eventFactory.createEndElement("",null,"text"));
                                writer.add(eventFactory.createStartElement("",null,"time"));
                                writer.add(eventFactory.createCharacters(cmt.getTime()));
                                writer.add(eventFactory.createEndElement("",null,"time"));
                                writer.add(eventFactory.createEndElement("",null,"comment"));
                            }
                        }
                    }
                }
                if(event.getEventType() == XMLEvent.END_ELEMENT) {
                    if(event.asEndElement().getName().toString().equals("page")){
                        url="";
                    }
                }
            }
            writer.close();
        }
    }

    public ArrayList<Comment> getComments(String url){
        System.out.println("Add comments for "+url);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String cmtID = getCmtID(url);
        String content = tryCommentGet(cmtID);
        content = content.replace("mainComment(","");
        content = content.substring(0,content.length()-1);
        JSONObject jsonObject = JSONObject.fromObject(content);
        jsonObject = jsonObject.getJSONObject("data");

        JSONArray array;
        if(jsonObject!=null && jsonObject.containsKey("commentid")){
            array=jsonObject.getJSONArray("commentid");
        }
        else{
            return comments;
        }

        Iterator<JSONObject> iter = array.iterator();
        while(iter.hasNext()){
            JSONObject comment = iter.next();
            String text="",user_id="",user_gender="",user_region="",time="";
            if(comment.containsKey("content")){
                text =reformat(comment.getString("content"));
            }
            if(comment.containsKey("time")){
                time = parseUnixTime(comment.getString("time"));
            }
            if(comment.containsKey("userinfo")){
                JSONObject userinfo = comment.getJSONObject("userinfo");
                if(userinfo.containsKey("gender")){
                    user_gender = userinfo.getString("gender");
                }
                if(userinfo.containsKey("region")){
                    user_region=userinfo.getString("region");
                }
                if(userinfo.containsKey("userid")){
                    user_id=userinfo.getString("userid");
                }
            }
            Comment cmt = new Comment(text,time,user_id,user_gender,user_region,url,sourceId);
            comments.add(cmt);
        }
        return comments;
    }

    public String tryCommentGet(String cmt_id){
        String url="http://coral.qq.com/article/"+cmt_id+"/comment?commentid=0&reqnum=1000&tag=&callback=mainComment&_="
                +System.currentTimeMillis()/1000+"018";
        String content = new BasicCrawler().crawlPage(url);
        return content;
    }

    public String getCmtID(String url){
        String content = new BasicCrawler().crawlPage(url);
        Pattern p = Pattern.compile("(^|&|\\\\?)cmt_id = (\\d{10})(;)");
        Matcher matcher = p.matcher(content);
        if(matcher.find() && matcher.groupCount()>=1){
            String match = matcher.group(0);
            match = match.replace("cmt_id = ","");
            match = match.replace(";","");
            return match;
        }
        else return "";
    }

    public static void main(String[] args) throws IOException, XMLStreamException {

        ParseComments qq = new ParseComments();
        qq.addComments(args[0],args[1]);
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
