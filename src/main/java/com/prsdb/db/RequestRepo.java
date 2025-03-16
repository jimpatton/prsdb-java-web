package com.prsdb.db;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prsdb.model.Request;

public interface RequestRepo extends JpaRepository<Request, Integer> {

	default Optional<String> findTopRequestNumberCustomMethod() {
        return findAll().stream()
          .map(Request::getRequestNumber)
          .max(Comparator.naturalOrder());
}

	List<Request> findByStatusAndUserIdNot(String status, int userId);

	

}
