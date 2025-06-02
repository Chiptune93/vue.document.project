package com.dk.api.dto;

import java.util.List;

public class MenuNodeDto {

    private Long seq;
    private Long upSeq;
    private String title;
    private List<MenuNodeDto> children; // Representing depth2, depth3 etc.

    public MenuNodeDto() {
    }

    public MenuNodeDto(Long seq, Long upSeq, String title, List<MenuNodeDto> children) {
        this.seq = seq;
        this.upSeq = upSeq;
        this.title = title;
        this.children = children;
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

    public List<MenuNodeDto> getChildren() {
        return children;
    }

    public void setChildren(List<MenuNodeDto> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "MenuNodeDto{" +
                "seq=" + seq +
                ", upSeq=" + upSeq +
                ", title='" + title + '\'' +
                ", children=" + (children != null ? children.size() : 0) + // Avoid printing full children list for brevity
                '}';
    }
}
