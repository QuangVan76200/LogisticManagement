package com.example.demo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForgotPasswordRequestDTO {

	String email;

	String passworrd;

	public ForgotPasswordRequestDTO(String email, String passworrd) {
		this.email = email;
		this.passworrd = passworrd;
	}

}
