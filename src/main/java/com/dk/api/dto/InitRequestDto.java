package com.dk.api.dto;

// Assuming init parameters might expand, creating a DTO is good practice.
// If it's only 'type', a Map or direct @RequestParam might also work in controller,
// but DTO is cleaner for @RequestBody.
public class InitRequestDto {
    private String type;
    // Potentially other fields from the original paramMap for init
    // For example, if 'seq' was sometimes passed for specific init types:
    private Long seq;

    public InitRequestDto() {
    }

    public InitRequestDto(String type) {
        this.type = type;
    }

    public InitRequestDto(String type, Long seq) {
        this.type = type;
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        return "InitRequestDto{" +
                "type='" + type + '\'' +
                ", seq=" + seq +
                '}';
    }
}
