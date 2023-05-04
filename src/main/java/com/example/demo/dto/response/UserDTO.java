package com.example.demo.dto.response;

import com.example.demo.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

	private String fullName;
	private String userName;
	private String contactNumber;
	private String email;
	private String avatar;

	public UserDTO(User user) {
		this.fullName = user.getFullName();
		this.userName = user.getUserName();
		this.contactNumber = user.getContactNumber();
		this.email = user.getEmail();
		this.avatar = user.getAvatar();
	}

}
