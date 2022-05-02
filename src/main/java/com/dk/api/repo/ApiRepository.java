package com.dk.api.repo;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface ApiRepository {

    public List<HashMap> list(HashMap paramMap);

    public List<HashMap> getFileList(HashMap paramMap);

    public HashMap info(HashMap paramMap);

    public int insert(HashMap paramMap);

    public int update(HashMap paramMap);

    public int delete(HashMap paramMap);

}
