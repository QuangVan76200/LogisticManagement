package com.example.demo.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

	@Value("${file.upload-dir}")
	private String uploadDir;

	public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
		List<String> uploadedFiles = new ArrayList<>();

		for (MultipartFile file : files) {
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			Path filePath = Paths.get(uploadDir + fileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			uploadedFiles.add(fileName);
		}

		return uploadedFiles;
	}

	public String uploadFile(MultipartFile file) throws IOException {

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Path filePath = Paths.get(uploadDir + fileName);
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		return fileName;
	}
}
