package com.prsdb.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prsdb.model.Vendor;

public interface VendorRepo extends JpaRepository<Vendor, Integer> {
	

}
