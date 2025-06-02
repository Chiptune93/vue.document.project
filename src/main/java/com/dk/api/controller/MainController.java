package com.dk.api.controller;

import java.util.HashMap; // Still needed for some service method calls if they expect HashMap
import java.util.List;
import java.util.Map; // For @RequestBody(required = false) Map<String, Object> paramMap

import javax.servlet.http.HttpServletRequest;

import com.dk.api.dto.*; // Import all DTOs
import com.dk.api.service.ApiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    // ObjectMapper instance removed

    @Autowired
    ApiService svc;

    /**
     * 서비스 로딩 시, 초기화 작업
     * 
     * @param initRequestDto
     * @param request
     * @return
     */
    @PostMapping("/init")
    public List<MenuNodeDto> init(@RequestBody(required = false) InitRequestDto initRequestDto, HttpServletRequest request) {
        log.info("Init request received with InitRequestDto: {}", initRequestDto);
        // svc.init now expects InitRequestDto
        return svc.init(initRequestDto == null ? new InitRequestDto() : initRequestDto); // Pass empty DTO if null
    }

    /**
     * 메뉴 추가 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody DocumentDto documentDto, HttpServletRequest request) {
        log.info("Insert request received with DocumentDto: {}", documentDto);
        try {
            int result = svc.insert(documentDto); // svc.insert now expects DocumentDto
            if (result <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inserting data, service returned: " + result);
            }
            HashMap<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Insert successful");
            responseBody.put("seq", result); // result is the new seq from the service
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("Error during insert with DocumentDto: {}", documentDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing insert request: " + e.getMessage());
        }
    }

    /**
     * 클릭한 메뉴의 컨텐츠 가져오는 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/getContents")
    public ResponseEntity<?> getContents(@RequestBody DocumentDto documentDto, HttpServletRequest request) {
        log.info("GetContents request received with DocumentDto: {}", documentDto);
        // svc.getContents now expects DocumentDto
        DocumentDto contents = svc.getContents(documentDto);
        if (contents == null) {
            // Consider what fields from documentDto are relevant for the "not found" message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contents not found for request: " + documentDto);
        }
        return ResponseEntity.ok(contents);
    }

    /**
     * 컨텐츠 추가 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/addContents")
    public ResponseEntity<?> addContents(@RequestBody DocumentDto documentDto, HttpServletRequest request) {
        log.info("AddContents request received with DocumentDto: {}", documentDto);
        try {
            int result = svc.addContents(documentDto); // svc.addContents now expects DocumentDto
            if (result == -1) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding/updating contents.");
            }
            HashMap<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Add/Update successful");
            responseBody.put("seq", result); // result is the seq from the service
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("Error during addContents with DocumentDto: {}", documentDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing addContents request: " + e.getMessage());
        }
    }

    /**
     * 컨텐츠 삭제 작업
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DocumentDto documentDto, HttpServletRequest request) {
        log.info("Delete request received with DocumentDto: {}", documentDto);
        try {
            // Ensure 'seq' is present for delete operations
            if (documentDto.getSeq() == null || documentDto.getSeq() <= 0) {
                log.error("Delete operation requires a valid 'seq' in DocumentDto: {}", documentDto);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid 'seq' for delete operation.");
            }
            int result = svc.delete(documentDto); // svc.delete now expects DocumentDto
            if (result <= 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting data or data not found. Service returned: " + result);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error during delete with DocumentDto: {}", documentDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing delete request: " + e.getMessage());
        }
    }

    /**
     * 첨부파일 가져오기
     * 
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/getFileList")
    public List<FileInfoDto> getFileList(@RequestBody FileInfoDto fileInfoDto, HttpServletRequest request) {
        log.info("GetFileList request received with FileInfoDto: {}", fileInfoDto);
        // svc.getFileList now expects FileInfoDto
        return svc.getFileList(fileInfoDto);
    }
}
