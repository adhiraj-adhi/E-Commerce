package com.project.ecom.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
	@Override
	public String uploadImage(String path, MultipartFile productImage) {
		// File name of original/current file
		String originalFileName = productImage.getOriginalFilename();
		
//		String fileNameWithoutExtension = productImage.getName();
		
		// Generate a unique file name
		String randomId = UUID.randomUUID().toString(); // Generating a random Id
		String fileName = randomId
				.concat(originalFileName
						.substring(originalFileName.lastIndexOf('.')));  // Concating original file name with extension to randomId
			// say if original file name is mat.jpg and randomId is 1234 then fileName will be 1234.jpg
		String filePath = path + File.separator + fileName; 
			/* (Q) Why we are using File.separator, we could have simply done: path+"/"+fileName?
			 *     This is so because file separator may vary from machine to machine (i.e. separator 
			 *     may be different on server machine).
			 */
		
		// Check if the path where we are uploading image exists (if not then create)
		File folder = new File(path);
		if(!folder.exists())
			folder.mkdir();
		
		// Upload the image on server
		try {
			Files.copy(productImage.getInputStream(), Paths.get(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// return the file name
		return fileName;
	}
}
