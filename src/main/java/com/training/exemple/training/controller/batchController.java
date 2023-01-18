package com.training.exemple.training.controller;

import com.training.exemple.dto.sftpInfoDTO;
import com.training.exemple.utils.addBatch;
import com.training.exemple.utils.SftpUtils;
import com.training.exemple.utils.slackMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * step1. sftp에서 file download
 * step2. 다운 받은 file 읽어오기
 * step3. 서비스구분에 따라 json setting
 * step4. db insert
 * */
@Slf4j
public class batchController {
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    SftpUtils sftpUtils;
    @Autowired
    addBatch addBatchService;

    public void sftpBatch(String van_name) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;
        Reader jsonTemp = null;
        int cnt = 0;

        HashMap<String, Object> insertMap = new HashMap<>();
        List<HashMap<String, Object>> list = new ArrayList<>();
        sftpInfoDTO dto = new sftpInfoDTO();
        JSONParser parser = new JSONParser();

        try {
            File file = new File(".");
            String jsonPath = file.getAbsolutePath() + "/src/main/java/com/training/exemple/json";  //최상위경로


            /* sftp 정보 setting */
            sqlSessionFactory.openSession();
            /* step1.sftp file download */
            sftpUtils.sftpDownload(dto);

            String serviceFg = "";
            String sLine = "";
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

                while ((sLine = br.readLine())!= null) {
                    serviceFg = sLine.substring(0, 1);
                    /* sftp에서 다운받은 파일의 서비스구분에 따라 json 가져오기 */
                    switch (serviceFg) {
                        //start record
                        case "51" :
                            jsonTemp = new FileReader(jsonPath + "/nice_batch_start_record.json");
                            break;
                        //header record
                        case "52" :
                            jsonTemp = new FileReader(jsonPath + "/nice_batch_header_record.json");
                        //data record
                        case "D": case "60": case "61": case "62": case "63": case "64": case "65": case "66": case "67":
                            if (van_name.equalsIgnoreCase("ksnet")) {
                                jsonTemp = new FileReader(jsonPath + "/ksnet_batch_data_record.json");
                            } else if (van_name.equalsIgnoreCase("nice")) {
                                jsonTemp = new FileReader(jsonPath + "/nice_batch_data_record.json");
                            } else {
                                log.error("등록되지 않은 VAN 입니다.");
                            }
                            break;
                        //result record
                        case "R": case "53":
                            if (van_name.equalsIgnoreCase("ksnet")) {
                                jsonTemp = new FileReader(jsonPath + "/ksnet_batch_result_record.json");
                            } else if (van_name.equalsIgnoreCase("nice")) {
                                jsonTemp = new FileReader(jsonPath + "/nice_batch_trailer_record.json");
                            } else {
                                log.error("등록되지 않은 VAN 입니다.");
                            }
                            break;
                    }

                    //json parser
                    JSONObject jsonObject = (JSONObject) parser.parse(jsonTemp);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("record");

                    byte[] line = sLine.getBytes();
                    for (int i=0; i<jsonArray.size(); i++) {
                        JSONObject job = (JSONObject) jsonArray.get(i);

                        String name = job.get("name").toString();
                        int strIdx = Integer.parseInt(job.get("str_idx").toString());
                        int len = Integer.parseInt(job.get("len").toString());
                        String type = job.get("type").toString();

                        if (type.equals("integer")) {
                            insertMap.put(name, Integer.parseInt(new String(line, strIdx, len)));
                        } else {
                            insertMap.put(name, new String(line, strIdx, len));
                        }
                    }

                    list.add(insertMap);
                    cnt += 1;
                }

                for (int j=0; j<list.size(); j++) {
                    con.setAutoCommit(false);

//                    String sql = addBatchService.insertData(insertMap, "tableNm");
//                    pstmt = con.prepareStatement(sql);
//                    pstmt.addBatch();

                    //10만건씩 나눠서 insert
                    if ((cnt % 100000) == 0) {
                        pstmt.executeBatch();
                        pstmt.clearBatch();
                        con.commit();
                    }
                }
                //남은 데이터 commit
                pstmt.executeBatch();
                con.commit();
            }

        } catch (Exception e) {
            log.error("sftp download error : " + e.getMessage());
            slackMsgUtil.msaSend("sftp download fail : " + e.getMessage());  //실패 시 슬렉으로 메세지 보내기
        }
    }

}

