package com.dk.api.repo;

import com.dk.api.dto.FileInfoDto;
import java.util.HashMap; // Keep for parameterMap in fileList if not changing yet
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository {
    List<FileInfoDto> fileList(FileInfoDto fileInfoDto);

    int getMasterSeq();

    int insertFile(FileInfoDto fileInfoDto);

    int deleteFile(int fileSeq);

    FileInfoDto info(int fileSeq);
}
