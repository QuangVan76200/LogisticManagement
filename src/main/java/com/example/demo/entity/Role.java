package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import lombok.Data;

@Data
@Entity
@Table(name="roles")
public class Role {
	
	@Id
	@GeneratedValue(strategy  = GenerationType.AUTO)
	@Column(name="rolesID")
	private int rolesID;
	
	@Enumerated(EnumType.STRING)
	@NaturalId
	@Column(name = "roleName", length = 60)
	private RoleName roleName;
	
}
