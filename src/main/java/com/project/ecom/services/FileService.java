package com.project.ecom.services;

import org.springframework.web.multipart.MultipartFile;

// We will use this for all File related services
public interface FileService {
	String uploadImage(String path, MultipartFile productImage);
}
