package com.training.exemple.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SftpInfoDTO {
    String host = "";               //host ip
    int port = 0;                   //port
    String id = "";                 //username
    String pw = "";                 //pw
    String FileName = "";           //파일명
    String remoteDir = "";          //가져 올 디렉토리
    String localDir = "";           //저장 디렉토리
}
