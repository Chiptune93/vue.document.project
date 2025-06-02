package com.dk.api.dto;

public class UploadResponseDto {

    private String status;
    private String message;
    private Long fileMasterSeq; // Changed to Long for consistency
    private FileInfoDto fileInfo;

    public UploadResponseDto() {
    }

    public UploadResponseDto(String status, String message, Long fileMasterSeq, FileInfoDto fileInfo) {
        this.status = status;
        this.message = message;
        this.fileMasterSeq = fileMasterSeq;
        this.fileInfo = fileInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getFileMasterSeq() {
        return fileMasterSeq;
    }

    public void setFileMasterSeq(Long fileMasterSeq) {
        this.fileMasterSeq = fileMasterSeq;
    }

    public FileInfoDto getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfoDto fileInfo) {
        this.fileInfo = fileInfo;
    }

    @Override
    public String toString() {
        return "UploadResponseDto{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", fileMasterSeq=" + fileMasterSeq +
                ", fileInfo=" + fileInfo +
                '}';
    }
}
