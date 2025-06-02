package com.dk.api.dto;

import java.time.LocalDateTime;

public class DocumentDto {

    private Long seq;
    private Long upSeq;
    private String title;
    private String contents;
    private LocalDateTime regDttm;
    private LocalDateTime modDttm;
    private Long fileMasterSeq;

    public DocumentDto() {
    }

    public DocumentDto(Long seq, Long upSeq, String title, String contents, LocalDateTime regDttm, LocalDateTime modDttm, Long fileMasterSeq) {
        this.seq = seq;
        this.upSeq = upSeq;
        this.title = title;
        this.contents = contents;
        this.regDttm = regDttm;
        this.modDttm = modDttm;
        this.fileMasterSeq = fileMasterSeq;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public Long getUpSeq() {
        return upSeq;
    }

    public void setUpSeq(Long upSeq) {
        this.upSeq = upSeq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public LocalDateTime getRegDttm() {
        return regDttm;
    }

    public void setRegDttm(LocalDateTime regDttm) {
        this.regDttm = regDttm;
    }

    public LocalDateTime getModDttm() {
        return modDttm;
    }

    public void setModDttm(LocalDateTime modDttm) {
        this.modDttm = modDttm;
    }

    public Long getFileMasterSeq() {
        return fileMasterSeq;
    }

    public void setFileMasterSeq(Long fileMasterSeq) {
        this.fileMasterSeq = fileMasterSeq;
    }

    @Override
    public String toString() {
        return "DocumentDto{" +
                "seq=" + seq +
                ", upSeq=" + upSeq +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", regDttm=" + regDttm +
                ", modDttm=" + modDttm +
                ", fileMasterSeq=" + fileMasterSeq +
                '}';
    }
}
