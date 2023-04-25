package com.example.demo.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.common.FileUploadService;
import com.example.demo.constants.CafeConstants;
import com.example.demo.dao.IUSerDao;
import com.example.demo.dto.request.ForgotPasswordRequestDTO;
import com.example.demo.dto.request.LoginFormDTO;
import com.example.demo.dto.request.SignUpFormDTO;
import com.example.demo.dto.request.UpdateInforUserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleName;
import com.example.demo.entity.User;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.jwt.UserDetailService;
import com.example.demo.service.IUserService;
import com.example.demo.utils.CafeUtils;
import com.example.demo.utils.EmailUtils;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * request Service business layer processing
 * 
 * @author VanLQ
 */

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	IUSerDao userDao;

	@Autowired
	private RoleServiceImpl roleServiceImpl;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	UserDetailService detailService;

	@Autowired
	JWTUtil jwtUtil;

	@Autowired
	JWTFilter jwtFilter;

	@Autowired
	EmailUtils emailUtils;

	/**
	 * Register a new user with the registration information provided.
	 *
	 * @param signUpFormDTO The SignUpFormDTO object contains registered user
	 *                      information.
	 *
	 * @return ResponseEntity<String> with success or error message.
	 *
	 * @throws Exception If there was an error during registration.
	 */
	@Override
	public ResponseEntity<String> signUp(SignUpFormDTO signUpFormDTO) {

		log.info("inside signup ", signUpFormDTO);
		try {
			if (validateMap(signUpFormDTO)) {
				User user = userDao.findByEmail(signUpFormDTO.getRequestMap().get("email"));
				if (Objects.isNull(user)) {
					userDao.save(getUserFromMap(signUpFormDTO));
					return CafeUtils.getResponse("Successfully Register", HttpStatus.OK);
				} else {
					return CafeUtils.getResponse("Email is already exists", HttpStatus.BAD_REQUEST);
				}
			} else {
				return CafeUtils.getResponse(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Check the validity of the SignUpFormDTO object.
	 *
	 * @param signUpFormDTO The SignUpFormDTO object is checked.
	 *
	 * @return true if required fields are provided, false otherwise.
	 */
	private static boolean validateMap(SignUpFormDTO signUpFormDTO) {
		if (signUpFormDTO.getRequestMap().containsKey("userName")
				&& signUpFormDTO.getRequestMap().containsKey("contactNumber")
				&& signUpFormDTO.getRequestMap().containsKey("fullName")
				&& signUpFormDTO.getRequestMap().containsKey("email")
				&& signUpFormDTO.getRequestMap().containsKey("password") && signUpFormDTO.getRoles() != null) {
			return true;
		}
		return false;
	}

	/**
	 * Create a user object from the provided SignUpFormDTO object.
	 *
	 * @param signUpFormDTO The SignUpFormDTO object contains registered user
	 *                      information.
	 *
	 * @return User generated from the provided SignUpFormDTO.
	 *
	 * @throws RuntimeException If the corresponding user permission is not found.
	 */
	private User getUserFromMap(SignUpFormDTO signUpFormDTO) {
		User user = new User();
		user.setUserName(signUpFormDTO.getRequestMap().get("userName"));
		user.setFullName(signUpFormDTO.getRequestMap().get("fullName"));
		user.setContactNumber(signUpFormDTO.getRequestMap().get("contactNumber"));
		user.setEmail(signUpFormDTO.getRequestMap().get("email"));
		String encodedPassword = new BCryptPasswordEncoder().encode(signUpFormDTO.getRequestMap().get("password"));
//		user.setPassword(passwordEncoder.encode(signUpFormDTO.getRequestMap().get("password")));
		user.setPassword(encodedPassword);
		Set<String> strRoles = signUpFormDTO.getRoles();
		Set<Role> setRoles = new HashSet<>();
		strRoles.forEach(role -> {
			switch (role) {
			case "ADMIN":
				Role roleAdmin = roleServiceImpl.findByRoleName(RoleName.ADMIN)
						.orElseThrow(() -> new RuntimeException("Role Not Found"));
				setRoles.add(roleAdmin);
				break;
			case "CUSTOMER":
				Role roleUser = roleServiceImpl.findByRoleName(RoleName.CUSTOMER)
						.orElseThrow(() -> new RuntimeException("Role not found"));
				setRoles.add(roleUser);
				break;
			default:
				Role roleManager = roleServiceImpl.findByRoleName(RoleName.MANAGER)
						.orElseThrow(() -> new RuntimeException("Role not found"));
				setRoles.add(roleManager);
				break;

			}
		});
		user.setRoles(setRoles);
		user.setStatus(true);

		return user;
	}

	/**
	 * 
	 * Authenticate user login credentials.
	 * 
	 * @param loginFormDTO The DTO containing the user's login credentials.
	 * 
	 * @return A ResponseEntity containing a JSON Web Token if the authentication is
	 *         successful and the user's status is active. If the authentication is
	 *         unsuccessful, or the user's status is inactive, a message is returned
	 *         indicating the reason.
	 */
	@Override
	public ResponseEntity<String> login(LoginFormDTO loginFormDTO) {
		log.info("Inside Login");
		try {
			Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginFormDTO.getRequestMap().get("userName"), loginFormDTO.getRequestMap().get("password")));
			if (auth.isAuthenticated()) {

				if (detailService.getUserDetail().getStatus() == true) {
					return new ResponseEntity<String>(
							"{\"token\":\"" + jwtUtil.generateToken(detailService.getUserDetail().getUserName(),
									detailService.getUserDetail().getRoles()) + "\"}",
							HttpStatus.OK);
				} else {
					return new ResponseEntity<String>("{\"message\":\"" + "Wait for admin approveal." + "\"}",
							HttpStatus.BAD_REQUEST);
				}
			}
		} catch (Exception e) {
			log.error("{}", e);
		}
		return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}", HttpStatus.BAD_REQUEST);
	}

	/**
	 * 
	 * Update user status with provided userId
	 * 
	 * @param requestMap a Map<String, String> containing userId and status value to
	 *                   update
	 * 
	 * @return a ResponseEntity<String> object with a success or error message
	 */
	@Override
	public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
		log.info("inside updateUser ", requestMap);
		try {
			if (!jwtFilter.isAdmin()) {
				return CafeUtils.getResponse(CafeConstants.UNAUTHORIZATE_ACESS, HttpStatus.UNAUTHORIZED);
			}

			Long userId = Long.valueOf(requestMap.get("userId"));
			Optional<User> findUser = userDao.findById(userId);
			User updateUser = findUser.get();

			if (!findUser.isEmpty()) {
				return CafeUtils.getResponse("UserId does not exists", HttpStatus.OK);
			}
			userDao.updateStatus(Boolean.valueOf(requestMap.get("status")), updateUser.getId());
			sendMailToAllAdmin(requestMap.get("status"), updateUser.getEmail(), userDao.getAllAdmin());
			return CafeUtils.getResponse("User's status update successfully", HttpStatus.OK);

		} catch (Exception e) {
			log.error("An error occurred: " + e.getMessage(), e);
			return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * Sends email to all admins except the current user.
	 * 
	 * @param status   the status of the user, either "true" for approved or "false"
	 *                 for disabled
	 * 
	 * @param user     the email of the user whose status is being updated
	 * 
	 * @param allAdmin a list of all admin emails
	 */
	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());

		if (status != null && Boolean.valueOf(status) == true) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "APPROVED ACCOUNT",
					"USER:- " + user + "\n is approved by \n ADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		} else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "DISABLE ACCOUNT",
					"USER:- " + user + "\n is disabled by \n ADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		}
	}

	@Override
	public ResponseEntity<String> checkToken() {

		return CafeUtils.getResponse("true", HttpStatus.OK);
	}

	/**
	 * 
	 * This method is used to change the password of a user.
	 * 
	 * @param requestMap a map containing request parameters including oldPassword
	 *                   and newPassword
	 * @return a ResponseEntity object with a message indicating whether the
	 *         password update was successful or not
	 */
	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {

		try {
			Optional<User> currentUser = userDao.findByUserName(jwtFilter.getCurrentUser());
			User user = userDao.findByEmail(currentUser.get().getEmail());
			if (!user.equals(null)) {
				if (passwordEncoder.matches(requestMap.get("oldPassword"), user.getPassword())) {
					user.setPassword(requestMap.get("newPassword"));
					userDao.save(user);
					return CafeUtils.getResponse("Updated password successfully", HttpStatus.OK);
				}
				return CafeUtils.getResponse("Incorect old password", HttpStatus.BAD_REQUEST);
			}
			return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("An error occurred while changing the password: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("The password cannot be changed. Please try again later.");
		}
	}

	/**
	 * 
	 * Handles forgot password request. Sends the new password to the user's email
	 * address if found in the database.
	 * 
	 * @param request the request containing the new password
	 * @return a response entity indicating the result of the operation
	 */
	@Override
	public ResponseEntity<String> forgotPassword(ForgotPasswordRequestDTO request) {
		try {
			Optional<User> currentUser = userDao.findByUserName(jwtFilter.getCurrentUser());
			User user = userDao.findByEmail(currentUser.get().getEmail());
			if (user != null && !Strings.isNullOrEmpty(user.getEmail())) {
				emailUtils.forgotPassword(user.getEmail(), "Credentials System ", request.getPassworrd());
				return CafeUtils.getResponse("Check your email Credentials", HttpStatus.OK);
			}
			return CafeUtils.getResponse("Check your email credentials", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Đã xảy ra lỗi: " + e.getMessage(), e);
		}
		return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 
	 * Updates the information of the current user in the system with the provided
	 * data.
	 * 
	 * @param request An object of type UpdateInforUserDTO containing the updated
	 *                user information.
	 * @return A ResponseEntity object containing a success message and a HttpStatus
	 *         code of OK if the user information was updated successfully.
	 *         Otherwise, an error message and an INTERNAL_SERVER_ERROR status code
	 *         is returned.
	 * @throws Exception if an error occurs while updating the user information.
	 */
	@Override
	public ResponseEntity<String> updateInFormUser(UpdateInforUserDTO request) {
		try {

			log.info("inside updateUser ", request);
			String authUser = jwtFilter.getCurrentUser();
			Optional<User> currentUser = userDao.findByUserName(authUser);
			if (!jwtFilter.isCustomer() || !jwtFilter.isAdmin()) {
				return CafeUtils.getResponse(CafeConstants.UNAUTHORIZATE_ACESS, HttpStatus.BAD_REQUEST);
			}

			User updateUser = currentUser.get();
			String newUserName = request.getUserName();
			String newEmail = request.getEmail();
			String newFullName = request.getFullName();
			String newPassword = request.getPassword();
			String newContactNumber = request.getContactNumber();
			String newAvatar = fileUploadService.uploadFile(request.getAvartar());

			if (request.getImages() != null && request.getImages().length > 0) {
				List<String> imageUrls = fileUploadService.uploadFiles(Arrays.asList(request.getImages()));
				updateUser.setImages(imageUrls);
			}

			updateUser.setUserName(StringUtils.isBlank(newUserName) || userDao.findByUserName(newUserName).isPresent()
					? updateUser.getUserName()
					: newUserName);
			updateUser.setEmail(StringUtils.isBlank(newEmail) || userDao.existsByEmail(newEmail) ? updateUser.getEmail()
					: newEmail);
			updateUser.setFullName(StringUtils.isBlank(newFullName) ? updateUser.getFullName() : newFullName);
			updateUser.setPassword(StringUtils.isBlank(newPassword) ? updateUser.getPassword() : newPassword);
			updateUser.setContactNumber(
					StringUtils.isBlank(newContactNumber) ? updateUser.getContactNumber() : newContactNumber);
			updateUser.setAvatar(StringUtils.isBlank(newAvatar) ? updateUser.getAvatar() : newAvatar);
			userDao.save(updateUser);
			return CafeUtils.getResponse("Update user successfully", HttpStatus.OK);
		} catch (Exception e) {
			log.error("An error occurred while updating user: " + e.getMessage(), e);
			return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
