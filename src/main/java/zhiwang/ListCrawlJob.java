package main.java.zhiwang;


import main.java.general.Recorder;


import org.apache.xpath.SourceTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by song on 2015/5/5.
 */
public class ListCrawlJob {
    private static Logger logger = Logger.getLogger(ListCrawlJob.class.getName());
    private Crawler crawler;
    private String sleepLabel = "<p><label>验证码：</label><input id=\"CheckCode\" name=\"CheckCode\" type=\"text\" /><input type=\"button\" value=\"提交\" onclick=\"javascript:CheckCodeSubmit()\" /></p>";


    public ListCrawlJob() {
        crawler = new Crawler();
    }

    /**
     * /**
     * 爬取包含一个文件link.txt文件夹下的候选页面列表
     *
     * @param outputFolder
     */
    public void crawlList(String outputFolder) {
        //判断子目录或者link.txt文件
        String dirPath = outputFolder;
        File dir = new File(dirPath);
        String linkPath = "";
        String mPath = "";
        if (dir.isDirectory()) {
            File[] fileList = dir.listFiles();
            if (fileList == null || fileList.length <= 0) {
                return;
            }
            for (File file : fileList) {
                if (file.toString().endsWith("link.txt")) {
                    linkPath = file.toString();
                    mPath = dirPath;
                    //TODO 默认每个link.txt中只有一个url
                    ArrayList<String> linkUrlList = getLinkURL(linkPath);
                    for(String linkUrl : linkUrlList){
                        if (linkUrl == null || "".equals(linkUrl)) {
                            return;
                        }
                        getUrlList(linkUrl, mPath);
                    }
                    break;
                } else {
                    crawlList(file.toString());
                }
            }
        }
        logger.info("Finished crawling dir:" + dirPath);
    }

    /**
     * Get Document URLs
     *
     * @param linkUrl
     * @param mPath
     */
    private void getUrlList(String linkUrl, String mPath) {
        //爬取列表页面
        logger.info("Start crawl list page:" + linkUrl);
        Document doc = null;
        int tryCount = 1;
        while (true) {
            doc = crawler.getDocumentByUrl(linkUrl);
            if (doc.toString().contains(sleepLabel)) {
                crawler = null;
                if(tryCount>=2)
                    System.out.println("请输入验证码！！！");
                Longsleep();
                crawler = new Crawler();
                tryCount++;
            }
            else break;

        }

        processLiType(doc, mPath);

    }

    /**
     * Type 1 Page
     */
    private void processLiType(Document doc, String mPath) {
        List<String> urlList = new ArrayList<String>();
        Elements eles = doc.select("a.fz14");

        for (Element ele : eles) {
            String href = ele.attr("href");
            href = href.replaceAll("amp;", "");
            href = "http://www.cnki.net/KCMS/detail/detail.aspx?" + href;
            urlList.add(href);
        }
        //TODO 写入List文件
        writeToLinkFile(urlList, mPath);
        sleep();
        urlList.clear();
        nextPage(doc, mPath);
    }

    //TODO  翻页，根据不同页面规则不同
    private void nextPage(Document doc, String mPath) {
        String nextHref = "http://epub.cnki.net/kns/brief/brief.aspx";
        String next = "下一页";
        Elements eles = doc.select("a");
        for (Element ele : eles) {
            if (next.equals(ele.text())) {
                nextHref += ele.attr("href");
                getUrlList(nextHref, mPath);
                break;
            }
        }

    }

    private void writeToLinkFile(List<String> urlList, String mPath) {
        Recorder linkRecoder = new Recorder(mPath +
                File.separator + "pagelists.txt", false);
        for (String url : urlList) {

            linkRecoder.writeRecordUTF8(url + "\r\n");
        }
    }


    private ArrayList<String> getLinkURL(String linkPath) {
        ArrayList<String> result = new ArrayList<String>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(linkPath));
            String line = "";
            while ((line = br.readLine()) != null) {
                result.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            logger.info("File not found" + linkPath);
            return null;
        } catch (IOException e) {
            logger.info("IO exception" + linkPath);
            return null;
        }
        return result;
    }

    public static void sleep() {

        int max = 10;
        int min = 1;
        Random random = new Random();

        int s = random.nextInt(max) % (max - min + 1) + min;

        System.out.println("Sleep " + s + "s");
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException e) {
            logger.info("Thread sleep interrupted!");
        }

    }

    public static void Longsleep() {

        System.out.println("Sleep " + 1 + "m");

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            logger.info("Thread sleep interrupted!");
        }

    }
}
