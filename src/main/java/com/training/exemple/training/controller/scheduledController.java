package com.training.exemple.training.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@EnableScheduling
public class scheduledController {

//    @Scheduled(cron = "0/5 * * * * ?")
    public void scheduledTest() {
        System.out.println("스케줄러 테스트!!");

    }
}
