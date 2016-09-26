package main.java.zhiwang;

import java.util.logging.Logger;
public class EduListCrawler {
    private static Logger logger = Logger.getLogger(EduListCrawler.class.getName());

    public static void main(String[] args) {
        logger.info("Start Crawl Pages on news.haedu.cn");
        String directory =".\\zhiwang";
        ListCrawlJob crawler = new ListCrawlJob();
        crawler.crawlList(directory);
        logger.info("Crawl Job Finished");
    }
}
