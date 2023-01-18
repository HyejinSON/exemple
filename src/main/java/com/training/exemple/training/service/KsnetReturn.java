package com.training.exemple.training.service;

import com.training.exemple.training.dao.KsnetReturnDAO;
import com.training.exemple.training.dto.KsnetDTO;
import com.training.exemple.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ksnet 입반송 관련 내용
* */
@Service("ksnetReturnService")
@Slf4j
public class KsnetReturnService {

    @Autowired
    private DataSource ds;

    @Autowired
    private KsnetReturnDAO dao;

    public  String ksnetReturn() throws SQLException {
        log.info("stpe1. ksent return file download start.");

        KSNetLib_FileDataComm comm = new KSNetLib_FileDataComm();
        KsnetDTO dto = new KsnetDTO();
        HashMap<String, Object> resMap = new HashMap<>();
        
        Reader jsonTemp = null;
        JSONParser parser = new JSONParser();
        HashMap<String, Object> insMap = new HashMap<>();
        ArrayList<KsnetDTO> dataList = new ArrayList<>();
        ArrayList<KsnetDTO> resultList = new ArrayList<>();

        Connection con = null;

        try {
            //file 가져오기
//            resMap = comm.get_ksnet_file();

            //db연결
            con = ds.getConnection();

            if(/*resMap.get("resCd").equals(0000)*/ 1==1) {
                log.info("step3. ksnet");

                // 가져올 파일 경로
                File file = new File("C:\\ksnet\\131212.txt");
                System.out.println(file);

                // 파일 읽어들이기
                if(file.exists()) {
                    log.info("step4. ksnet return file load......");
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

                    //seq 가져오기
                    con.setAutoCommit(false);
                    int seq = dao.selectSeq();

                    String sLine = "";
                    String serviceFlg = "";
                    while((sLine = br.readLine()) != null) {
                        serviceFlg = sLine.substring(0, 1);

                        // type에 맞는 json 포멧 세팅
                        if (serviceFlg == "D" || serviceFlg.equalsIgnoreCase("D")) {
                            dto = dataRecordSet(sLine);
                            dto.setSeq(seq);
//                            jsonTemp = new FileReader("D:\\personal\\exemple\\src\\main\\java\\com\\training\\exemple\\json\\ksnet_batch_data_record.json");
//                            insMap = jsonParse(jsonTemp, sLine);
                            dataList.add(dto);
                        } else if (serviceFlg == "R" || serviceFlg.equalsIgnoreCase("R")) {
                            dto = resultRecordSet(sLine);
//                            jsonTemp = new FileReader("D:\\personal\\exemple\\src\\main\\java\\com\\training\\exemple\\json\\ksnet_batch_result_record.json");
//                            insMap = jsonParse(jsonTemp, sLine);
                            resultList.add(dto);
                            seq ++;
                        } else {
                            break;
                        }
//                        insMap = new HashMap<>();   //초기화
                    }

                    //data record insert
                    if (dataList.size() > 0) {
                        log.info("step6. data record insert.");
                        dao.insertReturnDataRecord(con, dataList);
                    }
                    //result record insert
                    if (resultList.size() > 0) {
                        log.info("step6. result record insert.");
//                        dao.insertReturnResultRecord(con, resultList);
                    }
                }

            } else {
                // fild 가져오기 실패 시 슬랙으로 실패 메세지 발송
                slackMsgUtil.msaSend("ksnet 입반송 파일 다운로드 실패 >>> "
                                                + "errorCode= " + resMap.get("resCd")
                                                + ", errorMsg= " + resMap.get("resMsg"));

                return "fail";
            }

            //입금일자 update
            for (KsnetDTO ksnetDTO : dataList) {
                //가맹점번호, 매출일자, 승인번호, 고유번호
            }


        } catch (Exception e) {
            con.rollback();
            log.info(e.toString());
//            slackMsgUtil.msaSend("ksnet return file insert fail >>> " + e.toString());
        } finally {
            if(con != null) {
                try { con.close(); } catch (SQLException e) { }
            }
        }

        return "success";
    }
    /**
     * JSON parsing
     * 각 record json별로 읽어들이면서 파싱
     * 저장방식이 MONNGO같은 NO-SQL일 경우에는 효율적일 수 있으나, RDBMS에서는 작업이 더 번거로워지므로 사용X
     * */
    private HashMap<String, Object> jsonParse(Reader jsonTemp, String sLine) {
        JSONParser parser = new JSONParser();
        HashMap<String, Object> insMap = new HashMap<>();

        try {
            JSONObject jobj = (JSONObject) parser.parse(jsonTemp);
            JSONArray jarr = (JSONArray) jobj.get("record");

            // json type에 맞게 data setting
            byte[] line = sLine.getBytes();
            for (int i = 0; i < jarr.size(); i++) {
                JSONObject job = (JSONObject) jarr.get(i);

                String culNm = job.get("name").toString().replaceAll("[_]", "");
                int strIdx = Integer.parseInt(job.get("str_idx").toString());
                int len = Integer.parseInt(job.get("len").toString());
                String type = job.get("type").toString();

                System.out.println(culNm);

                //공백삭제
                String val = new String(line, strIdx, len);

                if (type.equals("integer")) {
                    insMap.put(culNm, Integer.parseInt(val.trim()));
                } else {
                    insMap.put(culNm, val.trim());
                }

            }
        } catch (Exception e) {
            log.error("json parse error " + e.toString());
        }

        return insMap;
    }

    /**
     * 입반송 DATA_RECORD DTO SETTING
     * new String() : byte 단위로 자르기.
     *                입력되는 형식을 모를때는 잘못 자르면 한글이 깨질 수 있으나,
     *                전문에서는 byte수가 명확하게 정해져 있기 때문에 new String()으로 자른다.
     * */
    private KsnetDTO dataRecordSet(String sLine) {
        KsnetDTO dto = new KsnetDTO();
        byte[] line = sLine.getBytes();

        dto.setRecordFg(new String(line, 0, 1).trim());                 //레코드구분
        dto.setTranFg(new String(line, 1, 2).trim());                   //데이터구분
        dto.setSalesDt(new String(line, 3, 6).trim());                  //매출일자
        dto.setAcceptDt(new String(line, 9, 6).trim());                 //접수일자
        dto.setCardNo(new String(line, 15, 20).trim());                 //카드번호
        dto.setIns(new String(line, 35, 2).trim());                     //할부기간
        dto.setApprNo(new String(line, 37, 8).trim());                  //승인번호
        dto.setCardReturnCd(new String(line, 45, 4).trim());            //카드사 반송코드
        dto.setKsnetReturncd(new String(line, 49, 2).trim());           //KSNET 반송코드
        dto.setSalesAmt(Integer.parseInt(new String(line, 51, 12)));    //거래금액
        dto.setCardCode(new String(line, 63, 2).trim());                //카드사코드
        dto.setMid(new String(line, 65, 15).trim());                    //가맹점 KYE값
        dto.setSalesFg(new String(line, 80, 5).trim());                 //매입/매입취소 구분
        dto.setPaymentDt(new String(line, 85, 6).trim());               //지급일자
        dto.setStoreSpace(new String(line, 91, 20).trim());             //가맹점 사용영역
        dto.setFee(Integer.parseInt(new String(line, 111, 9)));         //수수료

        System.out.println(dto);
        return dto;
    }

    /**
     * 입반송 RESULT_RECORD DTO SETTING
     * */
    private KsnetDTO resultRecordSet(String sLine) {
        KsnetDTO dto = new KsnetDTO();
        byte[] line = sLine.getBytes();

        dto.setRecordFg(new String(line, 0, 1).trim());                 //레코드구분
        dto.setReqCnt(Integer.parseInt(new String(line, 1, 7)));        //접수건수
        dto.setReqAmt(Integer.parseInt(new String(line, 8, 12)));       //접수금액
        dto.setRtnCnt(Integer.parseInt(new String(line, 20, 7)));       //반송건수
        dto.setRtnAmt(Integer.parseInt(new String(line, 27, 12)));      //반송금액
        dto.setDefCnt(Integer.parseInt(new String(line, 39, 7)));       //보류건수
        dto.setDefAmt(Integer.parseInt(new String(line, 46, 12)));      //보류금액
        dto.setDefRtnCnt(Integer.parseInt(new String(line, 58, 7)));    //보류해제건수
        dto.setDefRtnAmt(Integer.parseInt(new String(line, 65, 12)));   //보류해제금액
        dto.setSumCnt(Integer.parseInt(new String(line, 77, 7)));       //합계건수
        dto.setSumAmt(Integer.parseInt(new String(line, 84, 12)));      //합계금액
        dto.setFee(Integer.parseInt(new String(line, 96, 12)));         //수수료
        dto.setPaymentDt(new String(line, 108, 6).trim());              //입금일자
        dto.setPaymentAmt(Integer.parseInt(new String(line, 114, 12))); //입금액
        dto.setMid(new String(line, 126, 13).trim());                   //가맹점번호
        dto.setSendDt(new String(line, 139, 6).trim());                 //전송일자

        System.out.println(dto);
        return dto;
    }

}
