package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constants.CafeConstants;
import com.example.demo.dao.IUSerDao;
import com.example.demo.dto.request.ForgotPasswordRequestDTO;
import com.example.demo.dto.request.LoginFormDTO;
import com.example.demo.dto.request.SignUpFormDTO;
import com.example.demo.dto.request.UpdateInforUserDTO;
import com.example.demo.dto.response.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IUserService;
import com.example.demo.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * request Controller
 * 
 * @author VanLQ
 */

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserController {

	@Autowired
	IUserService userService;

	@Autowired
	IUSerDao userDao;

	@Autowired
	JWTFilter jwtFilter;

	/**
	 * 
	 * Register a new user.
	 * 
	 * @param requestMap a {@code SignUpFormDTO} object containing the user's
	 *                   registration information.
	 * @return a {@code ResponseEntity} object with a message indicating the status
	 *         of the user's registration.
	 * @throws Exception if an error occurs during registration.
	 */
	@PostMapping(path = "/signup")
	public ResponseEntity<String> signUp(@RequestBody(required = true) SignUpFormDTO requestMap) {
		try {
			return userService.signUp(requestMap);
		} catch (Exception e) {
			log.error("An error occurred while signing up user: " + e.getMessage(), e);

		}
		return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 
	 * Process user login using provided credentials
	 * 
	 * @param requestMap The login credentials of the user as a {@link LoginFormDTO}
	 *                   object
	 * @return A {@link ResponseEntity} containing the login response message and
	 *         {@link HttpStatus} code
	 * @throws Exception If an error occurs during the login process
	 */
	@PostMapping(path = "/login")
	public ResponseEntity<String> login(@RequestBody(required = true) LoginFormDTO requestMap) {
		try {
			return userService.login(requestMap);
		} catch (Exception e) {
			log.error("An error occurred while processing user login: " + e.getMessage(), e);
		}
		return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 
	 * Retrieves a list of all users if the requester is an admin
	 * 
	 * @return ResponseEntity containing a List of UserDTO if successful or an error
	 *         response otherwise
	 */
	@GetMapping(path = "/listAll")
	public ResponseEntity<List<UserDTO>> listAllUser() {
		try {
			if (jwtFilter.isAdmin()) {
				List<User> users = userDao.listAllUsers();
				List<UserDTO> userDTO = users.stream().map(user -> new UserDTO(user)).collect(Collectors.toList());
				return ResponseEntity.ok(userDTO);
			} else {
				return new ResponseEntity<List<UserDTO>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			log.error("An error occurred while getting the list of users: " + e.getMessage(), e);
		}

		return new ResponseEntity<List<UserDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

	}

	/**
	 * 
	 * Updates the status of a user account based on the given request body.
	 * 
	 * @param requestMap A map containing the request parameters. The keys in the
	 *                   map should be "userId" and "status".
	 * @return A ResponseEntity with the result of the operation and an HTTP status
	 *         code.
	 */
	@PostMapping(path = "/updateStatusUser")
	public ResponseEntity<String> updateProfile(@RequestBody(required = true) Map<String, String> requestMap) {

		try {
			return userService.updateUser(requestMap);
		} catch (Exception e) {
			log.error("An error occurred while changing the account status: " + e.getMessage(), e);
			return CafeUtils.getResponse("An error occurred while changing the account status",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * 
	 * Check if the current user's token is still valid. If token is valid, return
	 * HTTP code 200 OK and message "Token is valid". If not, return HTTP code 401
	 * Unauthorized and message "Token is invalid or expired".
	 * 
	 * @return HTTP code and corresponding message
	 */
	@GetMapping(path = "/checkToken")
	public ResponseEntity<String> checkToken() {
		try {
			return userService.checkToken();
		} catch (Exception e) {
			log.error("Error! An error occurred. Please try again later: " + e.getMessage(), e);
			return CafeUtils.getResponse("Error! An error occurred. Please try again later",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * 
	 * Endpoint for changing password of a user
	 * 
	 * @param requestMap a Map containing oldPassword and newPassword of the user
	 * @return ResponseEntity with a success or error message
	 */
	@PostMapping(path = "/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody(required = true) Map<String, String> requestMap) {
		try {
			return userService.changePassword(requestMap);
		} catch (Exception e) {
			log.error("An error occurred while changing the password: " + e.getMessage(), e);

		}
		return CafeUtils.getResponse("An error occurred while changing the password:",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 
	 * Handles the forgot password request by sending an email to the user's
	 * registered email address with instructions to reset their password.
	 * 
	 * @param request the request containing the email address of the user
	 * @return the response entity indicating the status of the request
	 */
	@PostMapping(path = "/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestBody(required = true) ForgotPasswordRequestDTO request) {
		try {
			userService.forgotPassword(request);
		} catch (Exception e) {
			log.error("An error occurred while changing the password: " + e.getMessage(), e);
		}
		return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping(path = "/updateInformUser")
	public ResponseEntity<String> updateInFormUser(@RequestBody(required = true) UpdateInforUserDTO request) {
		try {

			return userService.updateInFormUser(request);
		} catch (Exception e) {
			log.error("An error occurred while updating user: " + e.getMessage(), e);
			return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
