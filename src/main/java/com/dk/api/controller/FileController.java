package com.dk.api.controller;

import java.io.IOException;
import java.util.HashMap;

import com.dk.api.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fsvc;

    /**
     * 파일업로드 - 서버에 업로드 후, 파일 정보 DB 저장 및 예외 처리 등
     * 
     * @param req
     */
    @PostMapping("/upload.do")
    public HashMap upload3(
            @RequestParam(value = "fileMasterSeq", required = false, defaultValue = "0") String fileMasterSeq,
            @RequestParam MultipartFile[] file) {
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + fileMasterSeq + "＃＃＃＃＃＃＃＃＃＃＃");
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + file + "＃＃＃＃＃＃＃＃＃＃＃");
        return fsvc.save(file, fileMasterSeq);
    }

    /**
     * 파일 다운로드 - DB 저장된 정보 기반으로 파일 다운로드
     * 
     * @param seq
     * @return
     * @throws IOException
     */
    @GetMapping("/download.do")
    public ResponseEntity<Resource> download(@RequestParam String seq) throws IOException {
        return fsvc.download(seq);
    }

    /**
     * 파일 삭제
     * 
     * @param seq
     * @return
     * @throws IOException
     */
    @ResponseBody
    @PostMapping("/delete.do")
    public ResponseEntity<Resource> delete(@RequestBody HashMap paramMap) throws IOException {
        int seq = Integer.parseInt(paramMap.get("seq").toString());
        return fsvc.delete(seq);
    }

}
