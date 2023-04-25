package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
    @JoinColumn(name = "user_id",  nullable = false, referencedColumnName = "userId")
    private User user;

    @Column(name = "TransactionDate", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "TransactionType", nullable = false, length = 3)
    private String transactionType;

    @Column(name = "Note")
    private String note;


}
