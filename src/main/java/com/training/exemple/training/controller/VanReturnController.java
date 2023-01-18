package com.training.exemple.training.controller;

import com.training.exemple.training.service.KsnetReturnService;
import com.training.exemple.utils.slackMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("return")
@Slf4j
public class ksnetController {

    @Autowired
    private KsnetReturnService ksnetReturnService;

    @GetMapping("/getReturnFile")
    public String getReturnFile(String vanNm) {
        String resCd = "";

        log.info("step0. 입반송파일 대상 : " + vanNm);
        try {
            //입반송 파일 다운로드
            if (vanNm.equalsIgnoreCase("ksnet")) {
                resCd = ksnetReturnService.ksnetReturn();
            } else if (vanNm.equalsIgnoreCase("nicepg")) {

            } else if (vanNm.equalsIgnoreCase("kcp")) {
                // kcp service
            } else {
                slackMsgUtil.msaSend("존재하지 않는 VAN명입니다.");
                return "fail";
            }

            if(resCd == "success") {
                //관련 db저장

            }
        } catch (Exception e) {

        }

        log.info("step7. 입반송 파일 저장 END-");
        return "success";

    }
}
