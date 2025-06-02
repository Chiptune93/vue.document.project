package com.dk.api.controller;

import java.io.IOException;
// HashMap removed as FileInfoDto will be used for delete method's RequestBody
import java.util.List;

import com.dk.api.dto.FileInfoDto; // Added for delete method
import com.dk.api.dto.FileSaveResultDto;
import com.dk.api.dto.UploadResponseDto;
import com.dk.api.service.FileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    FileService fsvc;

    /**
     * 파일업로드 - 서버에 업로드 후, 파일 정보 DB 저장 및 예외 처리 등
     * 
     * @param req
     */
    @PostMapping("/upload.do")
    public ResponseEntity<FileSaveResultDto> upload3(
            @RequestParam(value = "fileMasterSeq", required = false, defaultValue = "0") String fileMasterSeq,
            @RequestParam MultipartFile[] file) {
        log.info("Upload request received. FileMasterSeq: {}, Number of files: {}", fileMasterSeq, file != null ? file.length : 0);

        FileSaveResultDto resultFromService = fsvc.save(file, fileMasterSeq);

        // Check if any of the uploads failed
        boolean anyFailed = false;
        if (resultFromService != null && resultFromService.getUploadResults() != null) {
            for (UploadResponseDto responseDto : resultFromService.getUploadResults()) {
                if ("fail".equals(responseDto.getStatus())) {
                    anyFailed = true;
                    break;
                }
            }
        } else {
            // This case might indicate a more fundamental issue with the save operation itself
            log.error("File save service returned null or null uploadResults. FileMasterSeq: {}", fileMasterSeq);
            // Construct a basic error response if resultFromService or its list is null
             FileSaveResultDto errorResult = new FileSaveResultDto();
             errorResult.setFileMasterSeq(fileMasterSeq != null && !fileMasterSeq.isEmpty() ? Long.parseLong(fileMasterSeq) : 0L);
             // Add a single UploadResponseDto indicating a general failure
             UploadResponseDto generalFailure = new UploadResponseDto();
             generalFailure.setStatus("fail");
             generalFailure.setMessage("File service failed to process the request.");
             errorResult.setUploadResults(List.of(generalFailure));
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }

        if (anyFailed) {
            log.warn("One or more file uploads failed. FileMasterSeq: {}", resultFromService.getFileMasterSeq());
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(resultFromService);
        }

        log.info("All files processed successfully. FileMasterSeq: {}", resultFromService.getFileMasterSeq());
        return ResponseEntity.ok(resultFromService);
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
    public ResponseEntity<Resource> delete(@RequestBody FileInfoDto fileInfoDto) throws IOException {
        log.info("Delete request received for file: {}", fileInfoDto);
        if (fileInfoDto.getFileSeq() == null || fileInfoDto.getFileSeq() <= 0) {
            log.error("Invalid fileSeq for delete operation: {}", fileInfoDto.getFileSeq());
            // Consider returning a more specific error response, though current fsvc.delete might handle it
            // For now, letting it proceed to fsvc.delete which should handle it via FileUtil
        }
        // fsvc.delete expects an int fileSeq.
        // FileUtil.fileDelete returns ResponseEntity which is appropriate.
        return fsvc.delete(fileInfoDto.getFileSeq() != null ? fileInfoDto.getFileSeq().intValue() : 0);
    }

}
