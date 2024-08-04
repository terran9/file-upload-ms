package com.terrancode.fileupload_ms.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.terrancode.fileupload_ms.dtos.FileResponse;
import com.terrancode.fileupload_ms.services.StorageService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
public class FileUploadController {

	@Autowired
	private StorageService storageService;

	@PostMapping("/upload")
	public ResponseEntity<FileResponse> fileUpload(@RequestParam("image") MultipartFile image) {
		String fileName = storageService.uploadImage(image);
		FileResponse f = new FileResponse(fileName, "File is successfully uploaded");
		return new ResponseEntity<>(f, HttpStatus.OK);
	}

	@GetMapping("/download/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) {
		Resource resource = storageService.getResource(fileName);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFile(@PathVariable("fileName") String fileName, HttpServletRequest request) {
		Resource resource = storageService.getResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			// Fallback to the default content type if type could not be determined
			contentType = "application/octet-stream";
		}

		// Set Content-Type and Content-Disposition headers
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
