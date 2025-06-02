package com.dk.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.dk.api.dto.FileSaveResultDto;
import com.dk.api.dto.UploadResponseDto;
import com.dk.api.lib.FileUtil;
import com.dk.api.repo.FileRepository; // Keep if rpt is used for other things, though not visible in 'save'

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @Autowired
    FileRepository rpt;

    @Autowired
    FileUtil fu;

    /**
     * 파일 업로드
     * 
     * @param files
     * @param fileMasterSeq
     * @return
     */
    public FileSaveResultDto save(MultipartFile[] files, String fileMasterSeq) {
        log.info("Attempting to save files. Count: {}, MasterSeq provided: {}", files != null ? files.length : 0, fileMasterSeq);
        Path targetUploadPath = fu.getUploadPath("image"); // Assuming "image" is a generic type or should be determined dynamically

        if (targetUploadPath == null) {
            log.error("Upload path could not be determined. Aborting save operation.");
            // Consider how to signal this error. Returning a DTO indicating failure:
            FileSaveResultDto errorResult = new FileSaveResultDto();
            errorResult.setFileMasterSeq(Long.parseLong(fileMasterSeq)); // Or 0 if not parsable / appropriate
            errorResult.setUploadResults(Arrays.stream(files).map(f -> {
                UploadResponseDto urd = new UploadResponseDto();
                urd.setStatus("fail");
                urd.setMessage("Upload path could not be determined.");
                // urd.setFileInfo(null); // fileInfo would be null
                return urd;
            }).collect(Collectors.toList()));
            return errorResult;
        }

        long currentMasterSeq = (fileMasterSeq != null && !fileMasterSeq.equals("0") && !fileMasterSeq.isEmpty())
                                ? Long.parseLong(fileMasterSeq)
                                : fu.getMasterSeq();

        if (currentMasterSeq <= 0 && (fileMasterSeq == null || fileMasterSeq.equals("0") || fileMasterSeq.isEmpty())) {
             log.error("Failed to obtain a valid master sequence. Current masterSeq: {}", currentMasterSeq);
             FileSaveResultDto errorResult = new FileSaveResultDto();
             errorResult.setFileMasterSeq(currentMasterSeq);
             errorResult.setUploadResults(Arrays.stream(files).map(f -> {
                 UploadResponseDto urd = new UploadResponseDto();
                 urd.setStatus("fail");
                 urd.setMessage("Failed to obtain a valid master sequence for file operations.");
                 return urd;
             }).collect(Collectors.toList()));
             return errorResult;
        }

        List<UploadResponseDto> uploadResults = new ArrayList<>();
        if (files != null) {
            Arrays.asList(files).forEach(file -> {
                if (!file.isEmpty()) {
                    log.info("Processing file: {}", file.getOriginalFilename());
                    // fu.upload now returns UploadResponseDto
                    uploadResults.add(fu.upload(file, targetUploadPath, (int) currentMasterSeq));
                } else {
                    log.warn("Encountered an empty file part, skipping.");
                    UploadResponseDto emptyFileResponse = new UploadResponseDto();
                    emptyFileResponse.setStatus("skipped");
                    emptyFileResponse.setMessage("Empty file part encountered.");
                    emptyFileResponse.setFileMasterSeq(currentMasterSeq);
                    uploadResults.add(emptyFileResponse);
                }
            });
        } else {
            log.warn("Files array is null. No files to process.");
        }

        FileSaveResultDto finalResult = new FileSaveResultDto();
        finalResult.setFileMasterSeq(currentMasterSeq);
        finalResult.setUploadResults(uploadResults);

        log.info("File save process completed. MasterSeq: {}, Results count: {}", currentMasterSeq, uploadResults.size());
        return finalResult;
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
