package com.dk.api.dto;

import java.util.List;

public class FileSaveResultDto {

    private long fileMasterSeq;
    private List<UploadResponseDto> uploadResults;

    public FileSaveResultDto() {
    }

    public FileSaveResultDto(long fileMasterSeq, List<UploadResponseDto> uploadResults) {
        this.fileMasterSeq = fileMasterSeq;
        this.uploadResults = uploadResults;
    }

    public long getFileMasterSeq() {
        return fileMasterSeq;
    }

    public void setFileMasterSeq(long fileMasterSeq) {
        this.fileMasterSeq = fileMasterSeq;
    }

    public List<UploadResponseDto> getUploadResults() {
        return uploadResults;
    }

    public void setUploadResults(List<UploadResponseDto> uploadResults) {
        this.uploadResults = uploadResults;
    }

    @Override
    public String toString() {
        return "FileSaveResultDto{" +
                "fileMasterSeq=" + fileMasterSeq +
                ", uploadResults=" + uploadResults +
                '}';
    }
}
