package com.prsdb.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prsdb.model.LineItem;

public interface LineItemRepo extends JpaRepository<LineItem, Integer> {

}
