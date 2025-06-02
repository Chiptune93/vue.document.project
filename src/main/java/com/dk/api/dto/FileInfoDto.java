package com.dk.api.dto;

import java.time.LocalDateTime;

public class FileInfoDto {

    private Long fileMasterSeq;
    private Long fileSeq;
    private String fileName;
    private String filePath;
    private String fileType;
    private String fileSize; // Assuming String as per existing HashMap structure
    private String fileExt;
    private LocalDateTime regDttm;
    private Long documentSeq; // Or could be seq from document_base if it's a direct foreign key

    public FileInfoDto() {
    }

    public FileInfoDto(Long fileMasterSeq, Long fileSeq, String fileName, String filePath, String fileType, String fileSize, String fileExt, LocalDateTime regDttm, Long documentSeq) {
        this.fileMasterSeq = fileMasterSeq;
        this.fileSeq = fileSeq;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileExt = fileExt;
        this.regDttm = regDttm;
        this.documentSeq = documentSeq;
    }

    public Long getFileMasterSeq() {
        return fileMasterSeq;
    }

    public void setFileMasterSeq(Long fileMasterSeq) {
        this.fileMasterSeq = fileMasterSeq;
    }

    public Long getFileSeq() {
        return fileSeq;
    }

    public void setFileSeq(Long fileSeq) {
        this.fileSeq = fileSeq;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public LocalDateTime getRegDttm() {
        return regDttm;
    }

    public void setRegDttm(LocalDateTime regDttm) {
        this.regDttm = regDttm;
    }

    public Long getDocumentSeq() {
        return documentSeq;
    }

    public void setDocumentSeq(Long documentSeq) {
        this.documentSeq = documentSeq;
    }

    @Override
    public String toString() {
        return "FileInfoDto{" +
                "fileMasterSeq=" + fileMasterSeq +
                ", fileSeq=" + fileSeq +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", fileExt='" + fileExt + '\'' +
                ", regDttm=" + regDttm +
                ", documentSeq=" + documentSeq +
                '}';
    }
}
