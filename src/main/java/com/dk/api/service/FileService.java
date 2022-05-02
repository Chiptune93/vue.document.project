package com.dk.api.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.dk.api.lib.FileUtil;
import com.dk.api.repo.FileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @Autowired
    FileRepository rpt;

    @Autowired
    FileUtil fu;

    /**
     * 파일 업로드
     * 
     * @param req
     * @return
     */
    public HashMap save(MultipartFile[] files, String fileMasterSeq) {
        HashMap result = new HashMap();
        List<HashMap> uploadFileList = new ArrayList<HashMap>();
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + files + "＃＃＃＃＃＃＃＃＃＃＃");
        Path uploadPath = fu.getUploadPath("image");
        int masterSeq = Integer.parseInt(fileMasterSeq) > 0 ? Integer.parseInt(fileMasterSeq) : fu.getMasterSeq();
        Arrays.asList(files).stream().forEach(file -> {
            uploadFileList.add(fu.upload(file, uploadPath, masterSeq));
        });
        result.put("fileMasterSeq", masterSeq);
        result.put("uploadFileList", uploadFileList);
        return result;
    }

    /**
     * 파일 다운로드
     * 
     * @param fileSeq
     * @return
     */
    public ResponseEntity<Resource> download(String fileSeq) {
        return fu.fileDownload(fileSeq);
    }

    /**
     * 파일 삭제
     * 
     * @param fileSeq
     * @return
     */
    public ResponseEntity<Resource> delete(int fileSeq) {
        return fu.fileDelete(fileSeq);
    }
}
