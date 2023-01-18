package com.training.exemple.training.controller;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@RestController
public class httpUrlConnectionController {
    /**
     * httpUrlConnection
     * URL을 통해서 데이터를 주고받는 방법.
     * REST API라고 보면 됨.
     */

    @GetMapping("/httpUrlConn")
    public static JSONObject httpUrlConn() throws Exception {
        String resultJson = "";
        JSONObject responseJson = null;
        JSONParser parser = new JSONParser();
        HashMap<String, Object> map = new HashMap<>();

        URL url = new URL("http://localhost:8088/testRequest");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");   //request 방식 설정
        conn.setRequestProperty("Content-type", "application/json");  //기타 필요한 값 세팅
        conn.setDoOutput(true);     //받아온 json을 출력 가능한 상태로 변경. 기본값은 false

        try {
            int responsCode = conn.getResponseCode();   //결과 코드값 가져오기

            if ( responsCode == 401 ) {
                System.out.println("401:: Header를 확인 해 주세요.");
            } else if ( responsCode == 500 ) {
                System.out.println("500:: server error!!");
            } else {
                StringBuffer sb = new StringBuffer();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                while ((resultJson = br.readLine()) != null) {
                    sb.append(resultJson);
                }
                responseJson = (JSONObject) parser.parse(sb.toString());
                System.out.println("JSON : " + responseJson);

                br.close();
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseJson;
    }

    @GetMapping("/json")
    public String jsonData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID", "0101");
        jsonObject.put("NAME", "HOSHI");
        jsonObject.put("AGE", "22");
        jsonObject.put("COMPANY", "WISECARE");
        jsonObject.put("COMMENT", "TEST");

        System.out.println(jsonObject.toJSONString());

        return jsonObject.toJSONString();
    }

}
