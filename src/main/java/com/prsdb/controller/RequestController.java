package com.prsdb.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prsdb.db.ProductRepo;
import com.prsdb.db.RequestRepo;
import com.prsdb.model.Request;
import com.prsdb.model.RequestCreate;
import com.prsdb.model.User;

@CrossOrigin
@RestController
@RequestMapping("/api/requests")
public class RequestController {

	@Autowired
	private RequestRepo requestRepo;

	@GetMapping("/")
	public List<Request> getAll() {
		return requestRepo.findAll();
	}

	@GetMapping("/{id}")
	public Optional<Request> getById(@PathVariable int id) {
		Optional<Request> r = requestRepo.findById(id);
		if (r.isPresent()) {
			return r;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found for id " + id);
		}
	}
	
	@GetMapping("/list-review/{userId}")
	public List<Request> listReview(@PathVariable int userId) {
		return requestRepo.findByStatusAndUserIdNot("REVIEW", userId);
	}		
		

	@PostMapping("")
	public Request add(@RequestBody RequestCreate rc) {
		Request request = new Request();
		request.setUser(rc.getUser());
		request.setRequestNumber(getRequestNumber());
		request.setDescription(rc.getDescription());
		request.setJustification(rc.getJustification());
		request.setDateNeeded(rc.getDateNeeded());
		request.setDeliveryMode(rc.getDeliveryMode());
		request.setStatus("NEW");
		request.setTotal(0.0);
		request.setSubmittedDate(LocalDateTime.now());
		return requestRepo.save(request);
	}

	private String getRequestNumber() {
		String requestNbr = "R";
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
		requestNbr += today.format(formatter);
//		String requests = ("");
		Optional<Request> maxRequest = requestRepo.findAll().stream()
				.max((r1, r2) -> r1.getRequestNumber().compareTo(r2.getRequestNumber()));
		String reqNbr;
		if (maxRequest.isPresent()) {
			String maxReqNbr = maxRequest.get().getRequestNumber();
			String tempNbr = maxReqNbr.substring(7);
			try {
				int nbr = Integer.parseInt(tempNbr);
				nbr++;
				reqNbr = String.format("%04d", nbr);
			} catch (NumberFormatException e) {
				reqNbr = "0001";
			}
		} else {
			reqNbr = "0001";
		}
		requestNbr += reqNbr;
		return requestNbr;
	}

	@PutMapping("/{id}")
	public void putRequest(@PathVariable int id, @RequestBody Request request) {
		if (id != request.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request id mismatch vs URL");
		} else if (requestRepo.existsById(request.getId())) {
			requestRepo.save(request);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found for id " + id);
		}
	}

	@PutMapping("/submit-review/{id}")
	public void review(@PathVariable int id) {
		// get movie by id
		if (requestRepo.existsById(id)) {
			Request r = requestRepo.findById(id).get();
			// if total <= 50.00
			// return APPROVED
			// else return REVIEW
			if (r.getTotal() <= 50.00) {
				r.setStatus("APPROVED");
			} else {
				r.setStatus("REVIEW");
			}
			requestRepo.save(r);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found for id " + id);
		}


	}

	@PutMapping("/approve/{id}")
	public void approve(@PathVariable int id) {
		// get movie by id
		if (requestRepo.existsById(id)) {
			Request r = requestRepo.findById(id).get();
			// if status is "REVIEW"
			if (r.getStatus().equals("REVIEW")) {
				// update to APPROVED
				r.setStatus("APPROVED");
				// save changes
				requestRepo.save(r);
			}
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found for id " + id);
		}
	}
	
	
	@PutMapping("/reject/{id}")
	public void reject(@PathVariable int id) {
		if (requestRepo.existsById(id)) {
			Request r = requestRepo.findById(id).get();
			//if reasonForRejection != null
			if (r.getReasonForRejection() == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reason for Request cannot be null");
			}
			else {
			//update to REJECTED
			r.setStatus("REJECTED");
			//save changes
			requestRepo.save(r);
			}
		}
	
	}
	
	
	
	
	
	
	
	

	@DeleteMapping("/{id}")
	public void delete(@PathVariable int id) {
		if (requestRepo.existsById(id)) {
			requestRepo.deleteById(id);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found for id " + id);
		}
	}

}
