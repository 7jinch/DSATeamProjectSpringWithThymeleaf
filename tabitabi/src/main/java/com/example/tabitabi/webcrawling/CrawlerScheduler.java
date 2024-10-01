package com.example.tabitabi.webcrawling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CrawlerScheduler {

    private final CrawlerController crawlerController;

    public CrawlerScheduler(CrawlerController crawlerController) {
        this.crawlerController = crawlerController;
    }

    @Scheduled(cron = "0 0 10 * * *") // 매일 오전 10시에 실행
    public void performCrawling() {
        crawlerController.crawling(null);
    }
}
