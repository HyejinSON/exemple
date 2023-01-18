package com.training.exemple.utils;

import org.springframework.stereotype.Component;

import java.io.Reader;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

@Component
public class addBatch {
    /**
     * addBatch
     * */
    public void batch(List<HashMap<String, Object>> list) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "test", "1234");

            int cnt = 0;
            if(list.size() > 0) {
                for(HashMap<String, Object> map : list) {
                    //auto commit 풀기
                    con.setAutoCommit(false);

                    String sql = "";
                    if(map.get("RECORD_FG").equals("D")) {
                        sql = insertDataRecord(map);
                    } else if (map.get("RECORD_FG").equals("R")) {
                        sql = insertResultRecord(map);
                    }

                    cnt ++;

                    pstmt = con.prepareStatement(sql);
                    pstmt.addBatch();       //addBatch에 담기

                    //1만 건씩 나눠서 insert
                    if ((cnt % 10) == 0) {
                        System.out.println(pstmt);
                        pstmt.executeBatch();  //실행
                        pstmt.clearBatch();    //초기화
                        con.commit();          //DB commit
                    }
                }

                //실행 후 남은 data 처리
                pstmt.executeBatch();
                con.commit();
            }
        } catch (Exception e) {
            con.rollback();
            slackMsgUtil.msaSend("addBatch rollback >>> " + e.toString());
        } finally {
            if(pstmt!=null) try{pstmt.close();}catch(SQLException e){};
            if(con!=null) try{con.close();}catch(SQLException e){};
        }

    }

    /**
     * 입반송 원천데이터(DATA_RECORD)
     * */
    public String insertDataRecord(HashMap<String, Object> map) throws Exception{
        StringBuffer sql = new StringBuffer();

        // insert query 생성
        sql.append("INSERT INTO KSNET_RETURN_DATA_RECORD (" );
        sql.append("RECORD_FG");
        sql.append(", TRAN_FG");
        sql.append(", SALES_DT");
        sql.append(", ACCEPT_DT");
        sql.append(", CARD_NO");
        sql.append(", INS");
        sql.append(", APPR_NO");
        sql.append(", CARD_RETURN_CD");
        sql.append(", KSNET_RETURN_CD");
        sql.append(", SALES_AMD");
        sql.append(", CARD_CODE");
        sql.append(", MID");
        sql.append( ", SALES_FG");
        sql.append(", PAYMENT_DT");
        sql.append(", STORE_SPACE");
        sql.append(", FEE");
        sql.append(", REG_NM");
        sql.append(") VALUES (");
        sql.append("'" + map.get("RECORD_FG") + "'");
        sql.append(", '" + map.get("TRAN_FG") + "'");
        sql.append(", '" + map.get("SALES_DT") + "'");
        sql.append(", '" + map.get("ACCEPT_DT") + "'");
        sql.append(", '" + map.get("CARD_NO") + "'");
        sql.append(", " + map.get("INS"));
        sql.append(", '" + map.get("APPR_NO") + "'");
        sql.append(", '" + map.get("CARD_RETURN_CD") + "'");
        sql.append(", '" + map.get("KSNET_RETURN_CD") + "'");
        sql.append(", " + map.get("SALES_AMT"));
        sql.append(", '" + map.get("CARD_CODE") + "'");
        sql.append(", '" + map.get("MID") + "'");
        sql.append(", '" + map.get("SALES_FG") + "'");
        sql.append(", '" + map.get("PAYMENT_DT") + "'");
        sql.append(", '" + map.get("STORE_SPACE") + "'");
        sql.append(", " + map.get("FEE"));
        sql.append(", '" + "ADMIN" + "'");
        sql.append(")");

        System.out.println("data_record = " + sql);
        return sql.toString();
    }

    /**
     * 인반송 원천데이터(RESULT_RECORD)
     * */
    public String insertResultRecord(HashMap<String, Object> map) {
        StringBuffer sql = new StringBuffer();

        sql.append("INSERT INTO KSNET_RETURN_RESULT_RECORD (");
        sql.append("RECORD_FG");
        sql.append(", REQ_CNT");
        sql.append(", REQ_AMT");
        sql.append(", RTN_CNT");
        sql.append(", RTN_AMT");
        sql.append(", DEF_CNT");
        sql.append(", DEF_AMT");
        sql.append(", DEF_RTN_CNT");
        sql.append(", DEF_RTN_AMT");
        sql.append(", SUM_CNT");
        sql.append(", SUM_AMT");
        sql.append(", FEE");
        sql.append(", PAYMENT_DT");
        sql.append(", PAYMENT_AMT");
        sql.append(", MID");
        sql.append(", SEND_DT");
        sql.append(", REG_NM");
        sql.append(") VALUES (");
        sql.append("'" + map.get("RECORD_FG") + "'");
        sql.append(", " + map.get("REQ_CNT"));
        sql.append(", " + map.get("REQ_AMT"));
        sql.append(", " + map.get("RTN_CNT"));
        sql.append(", " + map.get("RTN_AMT"));
        sql.append(", " + map.get("DEF_CNT"));
        sql.append(", " + map.get("DEF_AMT"));
        sql.append(", " + map.get("DEF_RTN_CNT"));
        sql.append(", " + map.get("DEF_RTN_AMT"));
        sql.append(", " + map.get("SUM_CNT"));
        sql.append(", " + map.get("SUM_AMT"));
        sql.append(", " + map.get("FEE"));
        sql.append(", '" + map.get("PAYMENT_DT") + "'");
        sql.append(", " + map.get("PAYMENT_AMT"));
        sql.append(", '" + map.get("MID") + "'");
        sql.append(", '" + map.get("SEND_DT") + "'");
        sql.append(", '" + "ADMIN" + "'");
        sql.append(")");

        System.out.println("result_record = " + sql);
        return sql.toString();

    }
}
