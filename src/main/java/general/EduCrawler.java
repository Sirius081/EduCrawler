package main.java.general;

import java.util.ResourceBundle;

/**
 * Created by sirius-.- on 2015/8/3.
 */
public class EduCrawler {
    public void crawl(){}

    protected  ResourceBundle rb;
    protected  String ip;
    protected PageDao pDao;
    public EduCrawler(){
        rb = ResourceBundle.getBundle("db");
        ip = rb.getString("ipAdd");

        pDao=new PageDao();
    }
}
