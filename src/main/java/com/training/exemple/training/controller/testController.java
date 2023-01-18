package com.training.exemple.training.controller;

import com.training.exemple.utils.slackMsgUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "test")
public class testController {

    @Value("${test.jsonfile}")
    private static String jsonPath;

    @PostMapping("/hello")
    public static String hello(String equ){

        if(equ.equals("Y"))
            return "hello!";
        else
            return "not found";
    }

    @GetMapping("/hello2")
    public static HashMap<String, Object> hello2(String equ) throws Exception {
        ArrayList<Map<String, Object>> list = new ArrayList();
        HashMap<String, Object> map = new HashMap<>();
        String result = "";

        Connection conn = null;
        PreparedStatement pstmt = null;

        JSONParser parser = new JSONParser();
        File file = new File(".");
        String abPath = file.getCanonicalPath();  //최상위 절대경로

        String testTxt = "066320603000202209290718072011501701P1117502220929071807C862960151000220000000000000  와이즈케어                                                                                                       0와이즈케어 제품                                   K10           1   0000000000202209290718081000    1001183100017494O2022092907180804    04    87351487    삼성카드        OK: 87351487    5188317003815462240100000220000105284556      010000000000000000000000000000000000000000000000006883100268110Y                                                                                                                                                                          ";
        byte[] bt = testTxt.getBytes();

        String gubun = testTxt.substring(2);
        Reader reader = new FileReader(abPath + "/src/main/java/com/training/exemple/json/test.json");

//        if( gubun.equals("50") ) {
//            reader = new FileReader(abPath + "/src/main/java/com/training/exemple/json/test.json");
//        } else if ( gubun.equals("51") ) {
//
//        } else if ( gubun.equals("52") ) {
//
//        } else if ( gubun.equals("60") ) {
//
//        }

        JSONObject jsonObject = (JSONObject) parser.parse(reader);
//        System.out.println(jsonObject);

        JSONArray jsonArray = (JSONArray) jsonObject.get("testJson");

        /* 실제 입반송 배치파일이 들어오는 경우 row 수가 많으므로 중첩 for문으로 처리해줘야 함. */
        for (int i=0; i < jsonArray.size(); i++) {
            JSONObject jobj = (JSONObject) jsonArray.get(i);
            String name = jobj.get("name").toString();
//            int str =((Long)jobj.get("str_idx")).intValue();
//            int leng =((Long)jobj.get("length")).intValue();

            //json의 경우 object type이기 때문에 정수로 저장해도 꺼낼때는 object로 인식함
            //int로 받기 위해서는 parse해서 받아야 함.
            int str =Integer.parseInt(jobj.get("str_idx").toString());
            int leng =Integer.parseInt(jobj.get("length").toString());
            String type = jobj.get("type").toString();

            // type = integer 라면 int로 저장
            //substring의 경우 문자열만 자를 수 있으므로 byte로 자르는 경우 new String()을 써야함
            //new String(byte, 시작, 길이);
            if(type.equals("integer")) {
                map.put(name, Integer.parseInt(new String(bt, str, leng)));
            } else {
                result = new String(bt, str, leng, "UTF-8");
                map.put(name, result.trim());
            }
        }

        list.add(map);
        System.out.println(list);

        /* ArrayList를 만들어서 map을 담은 후, batch service에서 처리 */
//        conn.setAutoCommit(false);   //db auto commit false

//        String sql = "service";
//        pstmt = conn.prepareStatement(sql);  //만들어진 sql구문 넣어주기
//        pstmt.addBatch();                    //query 메모리에 올리기

//        if ( (i%10000) == 0 ) {
//            pstmt.executeBatch();   //Batch 실행
//            pstmt.clearBatch();     //Batch 초기화
//            conn.commit();          //db commit
//        }

        //커밋되지 못한 나머지 구문에 대하여 커밋
//        pstmt.executeBatch();
//        conn.commit();

        if(equ.equals("Y"))
            map.put("result", "hello");
        else
            map.put("result", "not found");

        return map;
    }

    public static String numbering(int init, int limit) {
        String output = "";
        while (init < limit) {
            output += init;
            init++;
        }
        return output;
    }

    @PostMapping(path = "/numbering")
    public static String output(int init, int limit) {
        String output = numbering(init, limit);

        return output;
    }

    @GetMapping(path = "/extendsTest")
    public static void extendsTest(int i, int j) {
        SubstractionableCalculator sb = new SubstractionableCalculator();
        sb.setOprands(i, j);
        sb.sum();
        System.out.println(sb.avg());
        sb.minus();
        System.out.println("요기까지------------");
    }

    @GetMapping(path ="/filedownloadtest")
    public void filedownload() throws Exception {
        int cur = 0;

        File file = new File("D:/sample/test.txt");

        if (!file.isFile()) {
            System.out.println("파일이 존재하지 않습니다.");
            return;
        }

        /**
         * FileReader
         * FileWriter
         **/
        // 파일 입출력시 기본으로 사용하는 FileReader, FileWriter는 운영체제의 기본 한글 인코딩을 사용하기 때문에 한글이 깨짐.
        BufferedReader fr = new BufferedReader(new FileReader(file));

        //입력
        BufferedWriter fw = new BufferedWriter(new FileWriter("D:/sample/result.txt"));

        String str;
        while ((str = fr.readLine()) != null) {
            fw.write(str);
            fw.flush();
            System.out.println(str);
        }
        fw.close();


        /**
         * BufferedInputStream
         * BufferedOutputStream
         **/
        //바이트 입력
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        //입력 스트림 생성
        //BufferedOutputStrema : 바이트 출력 스트림에 연결되어 버퍼를 제공해주는 보조 스트림.
        //스스로 파일에 접근하지 못하기 때문에 FileOutputStream을 사용해 접근해야 함.
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("D:/sample/result.txt"), 1024);

//        byte[] b = new byte[1024];
//        while((cur = bis.read(b))!=-1) {
//            bos.write(b, 0, cur);
//        }
//
//        bos.close();


        /**
         * BufferedReader
         * BufferedWirter
        **/
        //encoding 후 읽어오기(문자 입력) : 인코딩 설정을 위해서는 아래와 같이 buffer로 가져오면서 인코딩 지정해줘야 함.
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));

        //입력 스트림 생성 - encoding 필요할 경우
        //BufferWriter : 문자 출력 스트림에 연결되어 버퍼를 제공해즈는 스트림.
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/sample/result.txt"), "UTF-8"));

//        while((cur = br.read()) != -1) {
//            bw.write(cur);
//            bw.flush();
//        }
//
//        bw.close();
//        br.close();

    }

    @GetMapping("slackTest")
    public void slackTest(String msg) {
        System.out.println("슬렉테스트!!");
        slackMsgUtil.msaSend(msg);

    }

    @GetMapping("jsonTest")
    public void jsonTest() {
        test test = new test();
        Map<String, String> map = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        map.put("test", "test");
        map1.put("0000", "0000");
        test.test1(map, map1);

        System.out.println(map.get("test2"));
        System.out.println(map1.get("2222"));
    }

}


class Calculator {
    int left, right;

   public void setOprands(int left, int right) {
       this.left = left;
       this.right = right;
   }

   public void sum() {
       System.out.println(this.left + this.right);
   }

   public int avg() {
       return (this.left + this.right)/2;
   }

   public void minus() {
       System.out.println(this.left - this.right);
   }

}

class SubstractionableCalculator extends Calculator {
    @Override
    public void sum() {
        System.out.println("실행 결과는 " + (this.left + this.right) + " 입니다.");
    }

    @Override
    public int avg() {
        return super.avg();
    }

}

class test {

    public void test1(Map<String, String> map, Map<String, String> map1) {
        map.put("test1", "aaa");
        map.put("test2", "bbb");
        map1.put("1111", "1111");
        map1.put("2222", "2222");

    }
}

