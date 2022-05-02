package com.dk.api.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dk.api.service.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
public class MainController {

    @Autowired
    ApiService svc;

    /**
     * 서비스 로딩 시, 초기화 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/init")
    public HashMap init(@RequestBody HashMap paramMap, HttpServletRequest request) {
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + paramMap + "＃＃＃＃＃＃＃＃＃＃＃");
        return svc.init(paramMap);
    }

    /**
     * 메뉴 추가 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/insert")
    public int insert(@RequestBody HashMap paramMap, HttpServletRequest request) {
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + paramMap + "＃＃＃＃＃＃＃＃＃＃＃");
        return svc.insert(paramMap);
    }

    /**
     * 클릭한 메뉴의 컨텐츠 가져오는 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/getContents")
    public HashMap getContents(@RequestBody HashMap paramMap, HttpServletRequest request) {
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + paramMap + "＃＃＃＃＃＃＃＃＃＃＃");
        return svc.getContents(paramMap);
    }

    /**
     * 컨텐츠 추가 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/addContents")
    public int addContents(@RequestBody HashMap paramMap, HttpServletRequest request) {
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + paramMap + "＃＃＃＃＃＃＃＃＃＃＃");
        return svc.addContents(paramMap);
    }

    /**
     * 컨텐츠 삭제 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public int delete(@RequestBody HashMap paramMap, HttpServletRequest request) {
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + paramMap + "＃＃＃＃＃＃＃＃＃＃＃");
        return svc.delete(paramMap);
    }

    /**
     * 첨부파일 가져오기
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/getFileList")
    public List<HashMap> getFileList(@RequestBody HashMap paramMap, HttpServletRequest request) {
        System.out.println("＃＃＃＃＃＃＃＃＃＃＃ [LOG] : " + paramMap + "＃＃＃＃＃＃＃＃＃＃＃");
        return svc.getFileList(paramMap);
    }
}
