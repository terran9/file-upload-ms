package com.terrancode.fileupload_ms.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.terrancode.fileupload_ms.exceptions.StorageException;
import com.terrancode.fileupload_ms.services.StorageService;

@Service
public class FileServiceImpl implements StorageService {

	@Value("${shared.drive.path}")
	private String path;

	@Override
	public String uploadImage(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			String originalFileName = file.getOriginalFilename();
			String fileExtension = "";
			String baseName = "";

			if (originalFileName != null && originalFileName.contains(".")) {
				int dotIndex = originalFileName.lastIndexOf(".");
				fileExtension = originalFileName.substring(dotIndex);
				baseName = originalFileName.substring(0, dotIndex);
			} else {
				baseName = originalFileName;
			}

			String unqFileName = generateUniqueFileName(baseName, fileExtension);

			String filepath = path + File.separator + unqFileName;
			File f = new File(path);
			if (!f.exists()) {
				f.mkdir();
			}

			Files.copy(file.getInputStream(), Paths.get(filepath));
			return unqFileName;

		} catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public Resource getResource(String fileName) {
		Path filePath = Paths.get(path).resolve(fileName).normalize();
		Resource resource = null;
		try {
			resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new StorageException("File not found: " + fileName);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to read file: " + fileName, e);
		}

	}

	private String generateUniqueFileName(String baseName, String fileExtension) {

		String unqFileName = "";
		File file;
		do {
			String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			String uuid = UUID.randomUUID().toString().substring(0, 8);
			unqFileName = baseName + '_' + timeStamp + '_' + uuid + fileExtension;
			file = new File(path + File.separator + unqFileName);
		} while (file.exists());
		return unqFileName;
	}
}
