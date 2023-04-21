package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.example.demo.dto.request.ForgotPasswordRequestDTO;
import com.example.demo.dto.request.LoginFormDTO;
import com.example.demo.dto.request.SignUpFormDTO;
import com.example.demo.dto.request.UpdateInforUserDTO;
import com.example.demo.dto.response.UserDTO;

public interface IUserService {

	ResponseEntity<String> signUp(SignUpFormDTO signUpFormDTO);

	ResponseEntity<String> login(LoginFormDTO loginFormDTO);

	public ResponseEntity<String> updateUser(Map<String, String> requestMap);
	
	public ResponseEntity<String> updateInFormUser(UpdateInforUserDTO request);

	public ResponseEntity<String> checkToken();

	public ResponseEntity<String> changePassword(Map<String, String> requestMap);
	
	public ResponseEntity<String> forgotPassword(ForgotPasswordRequestDTO request);
	
	

}
