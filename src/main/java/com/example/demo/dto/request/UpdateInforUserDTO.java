package com.example.demo.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateInforUserDTO {

	private String userName;
	private String email;
	private String fullName;
	private String password;
	private String contactNumber;
	private MultipartFile avartar;

	private MultipartFile[] images;

	public UpdateInforUserDTO(String userName, String email, String fullName, String password, String contactNumber,
			MultipartFile avartar, MultipartFile[] images) {
		this.userName = userName;
		this.email = email;
		this.fullName = fullName;
		this.password = password;
		this.contactNumber = contactNumber;
		this.avartar = avartar;
		this.images = images;
	}

}
