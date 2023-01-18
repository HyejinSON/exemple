package com.training.exemple.utils;

import ksnetlib.filedatacomm.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;

public class KSNetLib_FileDataComm {
    @Value("${ksnet.ip}")
    private String ip;

    @Value("${ksnet.port}")
    private int port;

    public HashMap<String, Object> get_ksnet_file() {
        System.out.println("step2. ksnet server connect start-!");
        // 모듈 클래스 생성

        HashMap<String, Object> resultMap = new HashMap<>();
        KSNetLib ksnetlib = new KSNetLib();

        try {
            //생성하고자 하는 로그의 경로를 입력한다(파일명은 기존 로그명과 동일하게 생성되며, 생성될 경로를 입력한다)
            ksnetlib.SetLogPath("C://Log");

            //LOG 생성을 하려면(기본적으로 로그생성은 하지 않도록 되어 있음)
            ksnetlib.EnableLog(1);

            //송수신 Sleep 설정
            ksnetlib.SetSleepTime(100000);      //마이크로초 단위로 셋팅(예시는 0.1초로 셋팅)1/1000000(백만분의 1초)
            //설정하지 않을 경우 기본 네트워크 속도 전송
            //압축 or 압축+암호화를 이용한 통신 사용의 경우 반드시 셋팅 후 사용
            //권장사용(0.1초)


            // 파일 데이터 송신
            //		int sendResult = ksnetlib.sendFileData(
            //				3,                   // 암호화 :1 , 비암호화 : 0
            //				//=======IP , PORT는 변경될수 있음 ========
            //				"210.181.29.37",         // 서버 아이피
            //				30189,                // 서버 포트
            //				30000,                // 연결, 송수신 타임아웃
            //				"./sendData/01_20110816.req",     // 경로를 포함한 보낼 파일명
            //				'T',                  // 구분 코드 (문서 참고)
            //				200,                  // 구분 코드에 따른 한 레코드 크기 (문서 참고)
            //				"TESTMER001",           // 기관 코드
            //				1,                   // 순번
            //				"110812"               // 작업 일자 (YYMMDD)
            //		);
            //
            //		System.out.println( ksnetlib.getErrorMessage(sendResult) );


            // 파일 데이터 수신 (socket 통신)
            int recvResult = ksnetlib.recvFileData(
                    1,                   // 암호화 :1 , 비암호화 : 0
                    //=======IP , PORT는 변경될수 있음 ========
                    //"210.181.29.207",          // 서버 아이피
                    "210.181.28.207",         // 서버 아이피
                    20280,                    // 서버 포트
                    10000,                    // 연결, 송수신 타임아웃
                    "./recvData/221123.txt",   // 수신 받은 데이터를 저장할 경로를 포함한 파일명
                    'T',                    // 구분 코드 (문서 참고)
                    "AT0214911A",           // 기관 코드
                    "REPLY",                // 입금 내역 코드 (문서 참고)
                    150,                    // 입금 내역 코드에 따른 한 레코드 크기 (문서 참고)
                    "221123"                // 수신 일자 (YYMMDD)
            );

            System.out.println(ksnetlib.getErrorMessage(recvResult));

            if(recvResult == 0000) {
                resultMap.put("resCd", recvResult);
                resultMap.put("resMsg", "success");
                resultMap.put("filePath", "C:/ksnet");  //파일 저장경로
            } else {
                resultMap.put("resCd", recvResult);
                resultMap.put("resMsg", ksnetlib.getErrorMessage(recvResult));
            }

        } catch (Exception e) {
            System.out.println("err ::: " + e.getMessage());
        }
        return resultMap;
    }
}
