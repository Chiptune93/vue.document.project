package com.dk.api.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
// HashMap is no longer the primary return type for upload, but might be used internally or for other methods.
// Keep if other methods still use it, or remove if upload was the only user.
// For now, let's assume it might be used by other methods or was intended for a different part of the refactor.
import java.util.HashMap;

import com.dk.api.dto.FileInfoDto;
import com.dk.api.dto.UploadResponseDto;
import com.dk.api.repo.FileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 유틸 클래스
 */
@Component
public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    // 서버 업로드 경로
    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    // 최대 파일 크기
    private long MAX_SIZE = 50 * 1024 * 1024;

    // 파일 정보 저장 시, 레파지토리
    @Autowired
    FileRepository rpt;

    /**
     * 업로드 경로 구하기
     * 
     * @param type
     * @return
     */
    public Path getUploadPath(String type) {
        // 파일은 기본적으로 날짜 기준 (yyyymmdd) 으로 폴더를 구분
        LocalDate ld = LocalDate.now();
        String date = ld.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String typeFolder = "";
        // 타입에 따라 날짜 내부에 폴더 구분
        if (type.equals("image")) {
            typeFolder = "image";
        } else if (type.equals("document")) {
            typeFolder = "document";
        } else {
            typeFolder = "";
        }
        // 업로드 경로를 조합
        String uploadPathInMethod = uploadPath + File.separator + date + File.separator + typeFolder;
        // 조합된 경로 체크
        Path dir = Paths.get(uploadPathInMethod);
        // 해당 경로 존재하는지 체크
        if (!Files.exists(dir)) {
            try {
                // 경로가 없다면 생성
                Files.createDirectories(dir);
            } catch (Exception e) {
                log.error("Error creating directories: {}", dir, e);
                return null;
            }
        }
        return dir;
    }

    /**
     * 파일 업로드 마스터 시퀀스 구하기
     * 
     * @return
     */
    public int getMasterSeq() {
        int masterSeq = rpt.getMasterSeq();
        return masterSeq;
    }

    /**
     * 업로드 하기
     * 
     * @param file
     * @param path
     */
    public UploadResponseDto upload(MultipartFile file, Path path, int masterSeq) {
        UploadResponseDto responseDto = new UploadResponseDto();
        responseDto.setFileMasterSeq((long) masterSeq); // Set masterSeq in response DTO

        String originalFileName = file.getOriginalFilename();
        String fileSize = Long.toString(file.getSize());
        String fileExt = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExt = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        }
        String fileType = file.getContentType();
        String finalFilePath = "";

        // 1. 파일 사이즈 체크
        if (file.getSize() > MAX_SIZE) {
            responseDto.setStatus("fail");
            responseDto.setMessage("File over max upload size: " + MAX_SIZE / (1024 * 1024) + "MB");
            return responseDto;
        }

        // 2. 파일 확장자 체크
        if (!Arrays.asList("jpg", "png", "gif", "jpeg", "bmp", "xlsx", "ppt", "pptx", "txt", "hwp", "exe")
                .contains(fileExt.toLowerCase())) {
            responseDto.setStatus("fail");
            responseDto.setMessage("File type is not allowed: " + fileExt);
            return responseDto;
        }

        String tempName = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            tempName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        } else {
            tempName = originalFileName != null ? originalFileName : "untitled";
        }
        String encFileName = Base64.getEncoder().encodeToString(tempName.getBytes(StandardCharsets.UTF_8));
        finalFilePath = path.resolve(encFileName + "." + fileExt).toString();

        FileInfoDto fileInfoDtoToSave = new FileInfoDto();
        fileInfoDtoToSave.setFileName(originalFileName);
        // fileInfoDtoToSave.setEncFileName(encFileName); // Not a field in FileInfoDto, path includes it
        fileInfoDtoToSave.setFileSize(fileSize);
        fileInfoDtoToSave.setFileExt(fileExt);
        fileInfoDtoToSave.setFileType(fileType);
        fileInfoDtoToSave.setFilePath(finalFilePath);
        fileInfoDtoToSave.setFileMasterSeq((long) masterSeq);
        // regId is not set here, MyBatis mapper uses coalesce(#{regId},'system')

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, Paths.get(finalFilePath), StandardCopyOption.REPLACE_EXISTING);

            int newFileSeq = rpt.insertFile(fileInfoDtoToSave);
            if (newFileSeq > 0) {
                FileInfoDto uploadedFileInfo = rpt.info(newFileSeq); // rpt.info now returns FileInfoDto
                responseDto.setStatus("success");
                responseDto.setMessage("Upload complete");
                responseDto.setFileInfo(uploadedFileInfo); // Set the FileInfoDto in the response
            } else {
                // This case should ideally not happen if insertFile throws an exception on failure
                // or returns a clear error indicator that's handled.
                log.error("File insert failed for {}, but no exception was thrown. New fileSeq was not positive.", originalFileName);
                responseDto.setStatus("fail");
                responseDto.setMessage("Upload failed during database insert, no positive sequence returned.");
            }
        } catch (Exception e) {
            log.error("Error uploading file: {}", originalFileName, e);
            // Attempt to delete partially uploaded file if it exists, to prevent orphans
            try {
                Files.deleteIfExists(Paths.get(finalFilePath));
            } catch (IOException ex) {
                log.error("Could not delete partially uploaded file: {}", finalFilePath, ex);
            }
            responseDto.setStatus("fail");
            responseDto.setMessage("Upload failed: " + e.getMessage());
        }
        return responseDto;
    }

    /**
     * 파일 다운로드 하기
     * 
     * @param seq
     * @return
     */
    public ResponseEntity<Resource> fileDownload(String seq) {
        /* 추가 되어야 할 구간 */
        /* Front 암호화 시퀀스 --> Back 복호화하여 조회 */
        /* 추가 되어야 할 구간 */

        // 파일 정보 가져오기
        FileInfoDto fileInfo = rpt.info(Integer.parseInt(seq)); // rpt.info now returns FileInfoDto
        // 파일이 없을 경우 예외처리
        if (fileInfo == null || fileInfo.getFilePath() == null) {
            // 파일 정보가 없는경우 404 에러 발생.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            // 가져온 파일 정보에 따라, 가져올 경로 설정
            Path path = Paths.get(fileInfo.getFilePath());

            // Response 설정.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(fileInfo.getFileName(), StandardCharsets.UTF_8).build()); // Use getFileName()
            headers.add(HttpHeaders.CONTENT_TYPE, fileInfo.getFileType()); // Use getFileType()
            headers.add(HttpHeaders.CONTENT_LENGTH, fileInfo.getFileSize()); // Use getFileSize()

            Resource resource = new InputStreamResource(Files.newInputStream(path));

            // 파일 존재하는 경우, 정상 응답
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            // 처리 중, 오류 발생 시 500 에러 발생.
            log.error("Error during file download for seq: {}", seq, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 파일 삭제
     * 
     * @param seq
     * @return
     */
    public ResponseEntity<Resource> fileDelete(int fileSeq) {
        try {
            rpt.deleteFile(fileSeq);
        } catch (Exception e) {
            // 처리 중, 오류 발생 시 500 에러 발생.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // 파일 존재하는 경우, 정상 응답
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
