package com.training.exemple.training.dao;

import com.training.exemple.dto.SftpInfoMap;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface cmmMapper {
    SftpInfoMap getSftpInfo(String vanNm);
}
