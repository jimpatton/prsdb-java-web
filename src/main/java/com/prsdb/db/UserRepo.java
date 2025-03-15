package com.prsdb.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prsdb.model.User;



public interface UserRepo extends JpaRepository<User, Integer> {

	User findByUserNameAndPassword(String userName, String password);
	

}
