package main.java.general;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PageDao {


    public static int insertPage(int sourceId, String url, String title, String pubTime, String pubSource, String content) throws SQLException {
        int pageId = -1;
        Connection connection=SimpleConnectionPool.getInstance().getConnection();
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try {
            Date date = Date.valueOf(pubTime);
            String sql = "insert into pages_2015(source_Id,url,title,pubTime,pubSource,content) values(?,?,?,?,?,?)";
            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, sourceId);
            pstmt.setString(2, url);
            pstmt.setString(3, title);
            pstmt.setDate(4, date);
            pstmt.setString(5, pubSource);
            pstmt.setString(6, content);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if(rs.next())
                pageId = rs.getInt(1);
            rs.close();
            pstmt.close();
            release(connection,pstmt,rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageId;
    }

        //20150922前用的
//    public  synchronized void insertPage(int sourceId, String url, String title, String pubTime, String pubSource, String content)throws  Exception {
//        Connection connection=SimpleConnectionPool.getInstance().getConnection();
//        Date date = Date.valueOf(pubTime);
//        String sql = "insert into pages_2015(source_Id,url,title,pubTime,pubSource,content) values(?,?,?,?,?,?)";
//        PreparedStatement pstmt = connection.prepareStatement(sql);
//        try {
//
//            pstmt.setInt(1, sourceId);
//            pstmt.setString(2, url);
//            pstmt.setString(3, title);
//            pstmt.setDate(4, date);
//            pstmt.setString(5, pubSource);
//            pstmt.setString(6, content);
//            pstmt.executeUpdate();
//
//            release(connection, pstmt, null);
//        }catch (Exception e){
//
//            e.printStackTrace();
//            throw e;
//        }
//    }
//    public  synchronized void insertPage2(int sourceId, String url, String title, String pubTime, String pubSource, String content)throws  Exception {
//        Connection connection=SimpleConnectionPool.getInstance().getConnection();
//        Date date = Date.valueOf(pubTime);
//        String sql = "insert into pages_2015(source_Id,url,title,pubTime,pubSource,content) values(?,?,?,?,?,?)";
//        Statement pstmt = connection.createStatement();
//        try {
//
//            pstmt.setInt(1, sourceId);
//            pstmt.setString(2, url);
//            pstmt.setString(3, title);
//            pstmt.setDate(4, date);
//            pstmt.setString(5, pubSource);
//            pstmt.setString(6, content);
//            pstmt.executeUpdate();
//
//            release(connection, pstmt, null);
//        }catch (Exception e){
//
//            e.printStackTrace();
//            throw e;
//        }
//    }
    public  synchronized int insertPage(int sourceId, String url, String title, String pubTime, String pubSource, String content,ArrayList<Comment> comments)throws  Exception {
        Connection connection=SimpleConnectionPool.getInstance().getConnection();
        Date date = Date.valueOf(pubTime);
        String sql = "insert into pages_2015(source_Id,url,title,pubTime,pubSource,content) values(?,?,?,?,?,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ResultSet rs=null;
        int pageId=-1;

        try {

            pstmt.setInt(1, sourceId);
            pstmt.setString(2, url);
            pstmt.setString(3, title);
            pstmt.setDate(4, date);
            pstmt.setString(5, pubSource);
            pstmt.setString(6, content);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if(rs.next())
                pageId = rs.getInt(1);

        }catch (Exception e){

            e.printStackTrace();
            throw e;
        }
        try {
            sql="insert into comments_2015 (author,gender,location,content,page_id,source_id) values(?,?,?,?,?,?)";
            pstmt=connection.prepareStatement(sql);
            for(Comment cmt:comments){
                pstmt.setString(1,cmt.getId());
                pstmt.setString(2,cmt.getGender());
                pstmt.setString(3,cmt.getRegion());
                pstmt.setString(4,cmt.getText());
                pstmt.setInt(5, pageId);
                pstmt.setInt(6,cmt.getSourceId());
                pstmt.executeUpdate();
            }
            release(connection,pstmt,null);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return pageId;
    }
    //?????��?
    public  synchronized void insertList(HashSet<String> urlSet,int sourceId) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            PreparedStatement pstmt=connection.prepareStatement("");
            for(String url:urlSet){
                String sql = "insert into urlList_2015 values(?,?)";
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, url);
                pstmt.setInt(2, sourceId);
                pstmt.executeUpdate();

            }
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public  synchronized void insertList(String url,int sourceId) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "insert into urlList_2015 values(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, url);
            pstmt.setInt(2, sourceId);
            pstmt.executeUpdate();
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //???????,???????????url
    public synchronized  String crawlList(int sourceId)  {
        String url = "";

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "select url from urlList_2015 where source_id=" + sourceId;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                url = rs.getString(1);
            }
            release(connection, pstmt, rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    //????��?(????????????????��??)
    public synchronized  void deleteList(String url,int id) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "delete from urlList_2015 where source_id="+id+" and url='" + url + "'";
            // String sql="delete from pages_2015";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    //????��?(????????????????��??)
    public synchronized  void deleteList(String url) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "delete from urlList_2015 where url='" + url + "'";
            // String sql="delete from pages_2015";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    //?????????��?
    public synchronized  void insertExceptionList(String url, int sourceId,String exception_type) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "insert into exception_2015(url,source_Id,exception_type) values(?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,url);
            pstmt.setInt(2,sourceId);
            pstmt.setString(3,exception_type);
            pstmt.executeUpdate();
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    //???????,???????????exceptionurl
    public synchronized  String crawlExceptionList(int sourceId)  {
        String url = "";

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "select url from exception_2015 where source_id=" + sourceId;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                url = rs.getString(1);
            }
            release(connection, pstmt, rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
    //??????��?(????????????????��??,??????��???????????)
    public synchronized  void deleteExceptionList(String url) {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "delete from exception_2015 where url='" + url + "'";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //?????????????????source_id
    public synchronized  int searchSource(String sourceName) throws SQLException {

        int source_id = 0;
        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "select id from dataSource_2015 where sourceName=?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, sourceName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                source_id = rs.getInt(1);
            }
            release(connection, pstmt, rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return source_id;
    }

    //??????????Url
    public synchronized  void updateLastUrl(int crawlerId,String sectionUrl,String lastUrl) throws Exception{

        Connection connection=SimpleConnectionPool.getInstance().getConnection();
        String sql="update section_info_2015 set last_url='"+lastUrl+"'"+" where section_url='"+sectionUrl+"'";
        PreparedStatement pstmt=connection.prepareStatement(sql);
        pstmt.executeUpdate();
        release(connection, pstmt, null);
    }

    public synchronized HashMap<String,ArrayList<String>> getSectionInfoById(int crawlerId)throws Exception{

        Connection connection=SimpleConnectionPool.getInstance().getConnection();
        HashMap<String,ArrayList<String>> sectionInfo=new HashMap<String,ArrayList<String>>();
        String sql="select * from section_info_2015 where source_Id="+crawlerId;
        PreparedStatement pstmt=connection.prepareStatement(sql);
        ResultSet rs=pstmt.executeQuery();
        ArrayList<String> sectionUrlList=new ArrayList<String>();
        ArrayList<String> lastUrlList=new ArrayList<String>();
        while(rs.next()){
            sectionUrlList.add(rs.getString(2));
            lastUrlList.add(rs.getString(3));
            sectionInfo.put("sectionUrl",sectionUrlList);
            sectionInfo.put("lastUrl",lastUrlList);
        }
        release(connection,pstmt,rs);
        return sectionInfo;

    }

    //??????????????url
    public synchronized  void recordLastUrl(String url, int sourceId) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "select dataTime from scheduleStatistics_2015 where dataSource_id=" + sourceId;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date d = rs.getDate(1);
                Date dt = new Date(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dstr = sdf.format(d);
                String dtstr = sdf.format(dt);
                if (dstr.equals(dtstr)) {
                    String sql1 = "update scheduleStatistics_2015 set dataTime=?,last_Crawl_url=? where dataSource_id=" + sourceId;
                    PreparedStatement pstmt1 = connection.prepareStatement(sql1);
                    pstmt1.setDate(1, dt);
                    pstmt1.setString(2, url);
                    pstmt1.executeUpdate();
                } else {
                    String sql2 = "insert into scheduleStatistics_2015(dataSource_id,dataTime,last_Crawl_url) values(?,?,?)";
                    PreparedStatement pstmt2 = connection.prepareStatement(sql2);
                    pstmt2.setInt(1, sourceId);
                    pstmt2.setDate(2, dt);
                    pstmt2.setString(3, url);
                    pstmt2.executeUpdate();
                    pstmt2.close();
                }
            }
            else {
                String sql3 = "insert into scheduleStatistics_2015(dataSource_id,dataTime,last_Crawl_url) values(?,?,?)";
                PreparedStatement pstmt3 = connection.prepareStatement(sql3);
                Date dt = new Date(System.currentTimeMillis());
                pstmt3.setInt(1, sourceId);
                pstmt3.setDate(2, dt);
                pstmt3.setString(3, url);
                pstmt3.executeUpdate();
                pstmt3.close();
            }
            release(connection,pstmt,rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //???url?????????pageId
    public  synchronized  int getPageIdByUrl(String url,int id)throws  Exception{
        int pageId=0;

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "select page_Id from pages_2015 where source_id="+id+" and url='" + url+"'";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                pageId = rs.getInt(1);
            }
            release(connection, pstmt, rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return pageId;
    }
    //???url?????????pageId
    public  synchronized  int getPageIdByUrl(String url)throws  Exception{
        int pageId=0;

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "select page_Id from pages_2015 where  url='" + url+"'";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                pageId = rs.getInt(1);
            }
            release(connection, pstmt, rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return pageId;
    }

    //??????????????url
    public synchronized  String searchLastUrl(int dataSource_id) throws SQLException {
        String url = "";

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "select last_Crawl_url from scheduleStatistics_2015 where dataSource_id=" + dataSource_id;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                url = rs.getString(1);
            }
            release(connection, pstmt, rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return url;
    }

    //?????????????????????
    public synchronized  void Sum(int sourceId, int num) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "update dataSource_2015 set cur_Crawl_listSize=? where id=" + sourceId;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //???????????url????
    public synchronized  void finishedUrl(int dataSource_id, int compeleteNum) {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            Date dt = new Date(System.currentTimeMillis());
            String sql = "select compeleteNum from scheduleStatistics_2015 where dataSource_id=" + dataSource_id+" and dataTime='"+dt+"'";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                compeleteNum = compeleteNum + rs.getInt(1);
                String sql2 = "update scheduleStatistics_2015 set compeleteNum=? where dataSource_id=" + dataSource_id;
                PreparedStatement pstmt2 = connection.prepareStatement(sql2);
                pstmt2.setInt(1, compeleteNum);
                pstmt2.executeUpdate();
            }
            else {
                String sql3 ="insert into scheduleStatistics_2015(dataSource_id,dataTime,compeleteNum) values(?,?,?)";
                PreparedStatement pstmt3 = connection.prepareStatement(sql3);
                pstmt3.setInt(1, dataSource_id);
                pstmt3.setDate(2, dt);
                pstmt3.setInt(3, compeleteNum);
                pstmt3.executeUpdate();
            }
            release(connection,pstmt,rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //??link???��???????
    public  synchronized void insertLinkurl(String linkurl, int sourceId,int panelId) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "insert into l(linkurl,source_Id,panel_id) values(?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,linkurl);
            pstmt.setInt(2, sourceId);
            pstmt.setInt(3,panelId);
            pstmt.executeUpdate();
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //?????????????linkUrl
    public  synchronized int searchLinkUrl(String linkUrl) throws SQLException {
        int  panelId =0;

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "select panel_id from l where linkUrl='" + linkUrl+"'" ;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                panelId = rs.getInt(1);
            }
            release(connection, pstmt, rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return panelId;
    }

    //?????��?
    public synchronized  void insertSection(int sourceId,String url) throws SQLException {

        try {
            Connection connection=SimpleConnectionPool.getInstance().getConnection();
            String sql = "insert into section_info_2015(source_Id,section_url) values(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, sourceId);
            pstmt.setString(2, url);
            pstmt.executeUpdate();
            release(connection, pstmt, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public synchronized  static void release(Connection conn,PreparedStatement st,ResultSet rs){

        if(rs!=null){
            try{
                //???��????????ResultSet????
                rs.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
            rs = null;
        }
        if(st!=null){
            try{
                //?????????SQL?????Statement????
                st.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(conn!=null){
            try{
                //???Connection????????????
                conn.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
