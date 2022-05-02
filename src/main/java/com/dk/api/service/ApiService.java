package com.dk.api.service;

import java.util.HashMap;
import java.util.List;

import com.dk.api.lib.ApiInterface;
import com.dk.api.repo.ApiRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiService implements ApiInterface {

    @Autowired
    ApiRepository repo;

    @Override
    public List<HashMap> list(HashMap paramMap) {
        return repo.list(paramMap);
    }

    @Override
    public HashMap info(HashMap paramMap) {
        return repo.info(paramMap);
    }

    @Override
    public int insert(HashMap paramMap) {
        return repo.insert(paramMap);
    }

    @Override
    public int delete(HashMap paramMap) {
        return repo.delete(paramMap);
    }

    public HashMap init(HashMap paramMap) {
        HashMap result = new HashMap<>();

        // 1depth
        paramMap.put("type", "1");
        List<HashMap> depth1 = repo.list(paramMap);

        // 2depth
        for (HashMap map1 : depth1) {
            HashMap forMap1 = new HashMap<>();
            forMap1.put("type", "2");
            forMap1.putAll(map1);
            List<HashMap> depth2 = repo.list(forMap1);
            map1.put("depth2", depth2);

            // 3depth
            for (HashMap map2 : depth2) {
                HashMap forMap2 = new HashMap<>();
                forMap2.put("type", "3");
                forMap2.putAll(map2);
                List<HashMap> depth3 = repo.list(forMap2);
                map2.put("depth3", depth3);
            }
        }

        result.put("menu", depth1);

        System.out.println("File: ApiService.java ~ line: (58)  ---> result: " + result);

        return result;
    }

    public HashMap getContents(HashMap paramMap) {
        HashMap result = new HashMap<>();

        // get contents
        HashMap contents = repo.info(paramMap);
        result.put("contents", contents);

        return result;
    }

    public int addContents(HashMap paramMap) {
        int result = 0;

        try {
            int seq = (paramMap.get("seq").toString() == null || paramMap.get("seq").toString().equals("")) ? 0
                    : Integer.parseInt(paramMap.get("seq").toString());
            if (seq > 0) {
                repo.update(paramMap);
                result = seq;
            } else {
                result = repo.insert(paramMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] add Content Exception : " + e.getMessage() + "＃＃＃＃＃＃＃＃＃＃＃");
            return -1;
        }
        System.out.println("File: ApiService.java ~ line: (103)  ---> result: " + result);
        return result;
    }

    public List<HashMap> getFileList(HashMap paramMap) {
        return repo.getFileList(paramMap);
    }

}
