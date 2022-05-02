package com.dk.api.repo;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository {
    List<HashMap<String, String>> fileList(HashMap<String, String> parameterMap);

    int getMasterSeq();

    int insertFile(HashMap<String, String> parameterMap);

    int deleteFile(int fileSeq);

    HashMap<String, String> info(int fileSeq);
}
