package com.example.demo.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.request.ProductSearchCriteriaDTO;
import com.example.demo.entity.Product;

@Repository
public class ProductRepositoryImpl {

	@Autowired
	private EntityManager entityManager;

	public List<Product> search(ProductSearchCriteriaDTO criteria) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> query = builder.createQuery(Product.class);
		Root<Product> root = query.from(Product.class);

		List<Predicate> predicates = new ArrayList<>();

		if (criteria.getName() != null) {
			predicates.add(builder.like(builder.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
		} else if (criteria.getPrice() != null) {
			predicates.add(builder.equal(root.get("price"), criteria.getPrice()));
		} else if (criteria.getWeight() != null) {
			predicates.add(builder.equal(root.get("weight"), criteria.getWeight()));
		}

		query.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Product> typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList();
	}

}
