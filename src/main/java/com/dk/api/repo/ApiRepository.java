package com.dk.api.repo;

import com.dk.api.dto.DocumentDto;
import com.dk.api.dto.FileInfoDto;
import com.dk.api.dto.MenuNodeDto;
import java.util.HashMap; // Keep for list and info parameters for now
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRepository {

    public List<MenuNodeDto> list(HashMap paramMap); // paramMap kept for now

    public List<FileInfoDto> getFileList(FileInfoDto fileInfoDto); // Changed paramMap to FileInfoDto

    public DocumentDto info(HashMap paramMap); // paramMap kept for now

    public int insert(DocumentDto documentDto);

    public int update(DocumentDto documentDto);

    public int delete(DocumentDto documentDto);

    public List<MenuNodeDto> getAllMenuNodes(); // New method for fetching all nodes
}
