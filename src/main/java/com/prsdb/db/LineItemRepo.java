package com.prsdb.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.prsdb.model.LineItem;

public interface LineItemRepo extends JpaRepository<LineItem, Integer> {
	List<LineItem> findByRequestId(int id);

}
