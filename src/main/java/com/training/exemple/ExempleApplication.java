package com.training.exemple;

import com.training.exemple.training.controller.testController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExempleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExempleApplication.class, args);
//        testController con = new testController();
//        System.out.println(con.hello2("N").get("result"));
    }

}
