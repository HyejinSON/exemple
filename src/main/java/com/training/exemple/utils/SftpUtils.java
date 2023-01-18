package com.training.exemple.utils;

import com.jcraft.jsch.*;
import com.training.exemple.dto.sftpInfoDTO;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.Properties;

@Repository
public class SftpUtils {
    private Logger log;
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;


    /**
     * sftp 접속
     * @Param host 접속주소
     * @Param userName 접속아이디
     * @Param password 비밀번호
     * @Param port 포트번호
     * */
    public String connect(sftpInfoDTO dto) throws Exception {
        log.info("1.sftp 접속");
        //JSch 객체 생성
        JSch jsch = new JSch();

        try {
            //JSch 세션 객체를 생성(사용자이름, 접속할 호스트, 포트 전달)
            session = jsch.getSession(dto.getUserName(), dto.getHost(), dto.getPort());
            //패스워드설정
            session.setPassword(dto.getPassword());
            //기타설정 적용
            Properties config = new Properties();
            config.put("StricHostKeyChecking", "no");
            session.setConfig(config);

            //접속
            session.connect();
            //sftp 채널열기
            channel = session.openChannel("sftp");
            //sftp 채널연결
            channelSftp = (ChannelSftp) channel;
            channel.connect();

            log.info("1-1.sftp 접속 성공");
            return "success";
        } catch (Exception e) {
            log.error("1-2.sftp connect error : " + e.getMessage());
            return "fail";
        }
    }

    /**
     * sftp 접속해제
     */
    public void disconnect() {
        log.info("3.sftp 접속해제");
        try {
            if(channelSftp != null && channelSftp.isConnected())
                channelSftp.disconnect();

            if(channel != null && channel.isConnected())
                channel.disconnect();

            if(session != null && session.isConnected())
                session.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            channelSftp = null;
            channel = null;
            session = null;
        }
    }

    /**
     * sftp 업로드
     * */
    public void upload(sftpInfoDTO dto) throws Exception {
        log.info("2.sftp 업로드");
        FileInputStream fis = null;

        try {
            //sftp접속
            connect(dto);
            //sftp 업로드 디렉토리 접근
            channelSftp.cd(dto.getRemoteDir());  //업로드경로

            //file 업로드
            File file = new File(dto.getFileName());
            fis = new FileInputStream(file);
            channelSftp.put(fis, file.getName());

            fis.close();
            //sftp 접속해제
            disconnect();
            
            log.info("2-1.sftp 업로드 성공!!");
        } catch (Exception e) {
            log.error("2-2.sftp 업로드 실패 : " + e.getMessage());
            slackMsgUtil.msaSend("sftp 업로드 실패 : " + e.getMessage());
        }

    }

    /**
     * sftp 다운로드
     */
    public void sftpDownload(sftpInfoDTO dto) {
        log.info("2.sftp 다운로드");
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            //sftp 접속
            String result = connect(dto);

            if ( result.equals("success") ) {
                //다운로드 받을 sftp 경로 이동
                channelSftp.cd(dto.getRemoteDir());
                //file 다운로드
                is = channelSftp.get(dto.getFileName());

                //localDir에 파일 생성
                File localFile = new File(dto.getLocalDir() + "/" + dto.getFileName());
                fos = new FileOutputStream(localFile);

                int rowCnt = 0;
                while ((rowCnt = is.read()) > 0) {
                    fos.write(rowCnt);
                }
                is.close();
                fos.close();

                //sftp접속해재
                disconnect();
            }

        } catch (Exception e) {
            log.error("sftp 다운로드 실패 : " + e.getMessage());
            slackMsgUtil.msaSend("sftp 다운로드 실패 : " + e.getMessage());
        }

    }
}
