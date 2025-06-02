package com.dk.api.lib;

import com.dk.api.dto.DocumentDto;
import com.dk.api.dto.MenuNodeDto;
import java.util.HashMap; // Kept for list and info as per ApiService
import java.util.List;

public interface ApiInterface {

    List<MenuNodeDto> list(HashMap paramMap);

    DocumentDto info(HashMap paramMap);

    int insert(DocumentDto documentDto); // Changed from HashMap to DocumentDto

    int delete(DocumentDto documentDto); // Changed from HashMap to DocumentDto

}
