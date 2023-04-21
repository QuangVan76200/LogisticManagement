package com.example.demo.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@DynamicUpdate
@DynamicInsert
@Data
@Table(name = "Users", uniqueConstraints = { @UniqueConstraint(columnNames = { "userName" }),

		@UniqueConstraint(columnNames = { "email" }) })
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userId")
	private Long id;

	@NotBlank
	@Size(min = 5, max = 100)
	@Column(name = "userName")
	private String userName;

	@NotBlank
	@Size(min = 5, max = 100)
	@Column(name = "fullName")
	private String fullName;

	@Size(max = 10)
	@Column(name = "contactNumber")
	private String contactNumber;

	@NotBlank
	@Size(max = 50)
	@Email
	@Column(unique = true, name = "email")
	private String email;

	@NotBlank
	@JsonIgnore
	@Size(max = 100)
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "status")
	private Boolean status;

	@Lob
	@Column(name = "avatar")
	private String avatar;

	@Lob
	@Column(name = "images")
	private List<String> images;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "userRole", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "rolesId"))
	Set<Role> roles;

}
