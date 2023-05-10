package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.demo.enums.WareTransactionType;

import lombok.Data;

@Entity
@DynamicUpdate
@DynamicInsert
@Data
@Table(name = "WareTransaction")
public class WareTransaction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TransactionID")
	private Long transactionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, referencedColumnName = "userId")
	private User user;

	@Column(name = "TransactionDate", nullable = false)
	private LocalDateTime transactionDate;

	@Column(name = "TransactionType", nullable = false)
	private WareTransactionType transactionType;

	@Column(name = "Note")
	private String note;

	@OneToMany(mappedBy = "wareTransaction", cascade = CascadeType.ALL)
	private List<WareTransactionDetail> transactionDetails;

	public void add(WareTransactionDetail detail) {
		transactionDetails.add(detail);
	}

}
