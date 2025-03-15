package com.prsdb.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prsdb.model.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {

}
