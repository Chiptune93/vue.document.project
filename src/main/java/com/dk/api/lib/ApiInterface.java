package com.dk.api.lib;

import java.util.HashMap;
import java.util.List;

public interface ApiInterface {

    List<HashMap> list(HashMap paramMap);

    HashMap info(HashMap paramMap);

    int insert(HashMap paramMap);

    int delete(HashMap paramMap);

}
