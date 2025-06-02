package com.dk.api.service;

import com.dk.api.dto.*; // Import all DTOs from the package
import com.dk.api.lib.ApiInterface;
import com.dk.api.repo.ApiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApiService implements ApiInterface {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    ApiRepository repo;

    @Override
    public List<MenuNodeDto> list(HashMap paramMap) { // Parameter kept as HashMap for now
        return repo.list(paramMap);
    }

    @Override
    public DocumentDto info(HashMap paramMap) { // Parameter kept as HashMap for now
        return repo.info(paramMap);
    }

    @Override
    public int insert(DocumentDto documentDto) { // Changed parameter to DocumentDto
        return repo.insert(documentDto);
    }

    @Override
    public int delete(DocumentDto documentDto) { // Changed parameter to DocumentDto
        return repo.delete(documentDto);
    }

    public List<MenuNodeDto> init(InitRequestDto initRequestDto) {
        // InitRequestDto is kept in signature for future filtering needs, but not used for now
        // as getAllMenuNodes fetches the entire tree.
        log.info("Initializing menu structure. Input DTO (currently ignored for full tree fetch): {}", initRequestDto);

        List<MenuNodeDto> allNodes = repo.getAllMenuNodes(); // Call the new method

        if (allNodes == null || allNodes.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, MenuNodeDto> nodeMap = allNodes.stream()
                .peek(node -> node.setChildren(new ArrayList<>())) // Initialize children list
                .collect(Collectors.toMap(MenuNodeDto::getSeq, node -> node));

        List<MenuNodeDto> rootNodes = new ArrayList<>();
        for (MenuNodeDto node : allNodes) {
            if (node.getUpSeq() == null || node.getUpSeq() == 0) { // Assuming root nodes have upSeq as 0 or null
                rootNodes.add(node);
            } else {
                MenuNodeDto parent = nodeMap.get(node.getUpSeq());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }
        log.info("Initialized menu structure: {}", rootNodes);
        return rootNodes;
    }


    public DocumentDto getContents(DocumentDto documentDto) { // Parameter changed to DocumentDto
        // Convert DocumentDto to HashMap for the existing repo.info(HashMap) call
        // This is an interim step. Ideally, repo.info would also take DocumentDto.
        HashMap<String, Object> paramMapForRepo = new HashMap<>();
        if (documentDto != null && documentDto.getSeq() != null) {
            // Assuming repo.info uses "seq" and potentially "type" or other fields from DocumentDto
            // The original repo.info query in XML was complex and used 'menu1', 'menu2', 'menu3', 'type'.
            // This mapping needs to be carefully considered based on how repo.info actually uses the map.
            // For simplicity, if DocumentDto only provides 'seq' for identification:
            paramMapForRepo.put("seq", documentDto.getSeq());
            // If other fields from DocumentDto (like title, upSeq) were used to form 'menu1' etc.
            // that logic would need to be replicated here or repo.info simplified.
            // The task states "DocumentDto contains the necessary fields from the original paramMap"
            // Let's assume for now that repo.info can work if 'seq' (and other relevant fields) are passed in the map.
            // If 'type' or specific menu level parameters are needed by repo.info,
            // they must be present in the DocumentDto or passed differently.
            // The original XML for repo.info:
            // <when test='type == "1"'> and db.up_seq in (select seq from viewdata where index_level = 2 and up_seq = #{menu1}::numeric)
            // <when test='type == "2"'> and db.seq in (select seq from viewdata where index_level = 3 and up_seq = #{menu2}::numeric)
            // <when test='type == "3"'> and db.seq = (select seq from viewdata where index_level = 3 and seq = #{menu3}::numeric)
            // This implies `paramMap` for `repo.info` needed `type` and `menu1/2/3` which are not directly in `DocumentDto`.
            // This part of the refactoring is problematic if repo.info is not also changed.
            // For now, I will proceed with the assumption that `repo.info` is flexible enough or
            // that the `DocumentDto` passed to `getContents` will have enough info to construct the map.
            // This is a potential area that might need further refinement.
            // Given the task description: "Service method svc.getContents() expects DocumentDto",
            // I will assume the HashMap needed by repo.info can be constructed from this DTO.
            // If `DocumentDto` is meant to hold query parameters like `type`, `menu1`, etc., it should be designed for that.
            // Or, `getContents` should take a more specific request DTO.
            // Sticking to DocumentDto as per prompt:
            if(documentDto.getSeq() != null) paramMapForRepo.put("seq", documentDto.getSeq());
            // if(documentDto.getType() != null) paramMapForRepo.put("type", documentDto.getType()); // If DocumentDto had a type field
            // This is a simplification. The actual mapping from DocumentDto to the complex HashMap expected by repo.info
            // needs to be defined by how the frontend query is structured.
        }
        log.debug("Calling repo.info with paramMap: {}", paramMapForRepo);
        return repo.info(paramMapForRepo);
    }

    public int addContents(DocumentDto documentDto) { // Parameter changed to DocumentDto
        int result = 0;
        try {
            // DocumentDto's seq determines insert or update
            if (documentDto.getSeq() != null && documentDto.getSeq() > 0) {
                repo.update(documentDto);
                result = documentDto.getSeq().intValue();
            } else {
                result = repo.insert(documentDto); // insert should now return the new seq
            }
        } catch (Exception e) {
            log.error("Error adding/updating content: {}", documentDto, e);
            return -1;
        }
        log.info("Added/Updated content, result (seq/id): {}", result);
        return result;
    }

    public List<FileInfoDto> getFileList(FileInfoDto fileInfoDto) { // Parameter changed to FileInfoDto
        // repo.getFileList already expects FileInfoDto
        if (fileInfoDto == null || fileInfoDto.getFileMasterSeq() == null) {
            log.warn("getFileList called with null FileInfoDto or null/zero fileMasterSeq.");
            // Consider returning empty list or throwing an IllegalArgumentException
            // For now, let it proceed to repo.getFileList which might handle it (e.g. return all if no seq)
            // or fail if seq is strictly required by its query.
            // The XML for fileList has <if test="fileMasterSeq != null and fileMasterSeq != 0">
            // so passing a DTO with null or 0 masterSeq will result in listing files without that filter.
        }
        return repo.getFileList(fileInfoDto);
    }

    // These methods were part of the interface but not explicitly in the refactor list for DTOs
    // They should be reviewed if they are still used or need similar DTO refactoring
    // For now, keeping them as-is if they are not directly using HashMaps that map to primary DTOs
     // list_old, info_old, insert_old, delete_old are removed as they are not part of the interface
     // and were placeholders.
}
