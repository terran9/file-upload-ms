package com.terrancode.fileupload_ms.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    public String uploadImage( MultipartFile file);

    public Resource getResource(String fileName) ;
}
