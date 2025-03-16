package com.prsdb.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.DoubleStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prsdb.db.LineItemRepo;
import com.prsdb.db.RequestRepo;
import com.prsdb.model.LineItem;
import com.prsdb.model.Product;
import com.prsdb.model.Request;

@CrossOrigin
@RestController
@RequestMapping("/api/lineitems")
public class LineItemController {

	@Autowired
	private LineItemRepo lineItemRepo;
	@Autowired
	private RequestRepo requestRepo;

	@GetMapping("/")
	public List<LineItem> getAll() {
		return lineItemRepo.findAll();
	}

	@GetMapping("/{id}")
	public Optional<LineItem> getById(@PathVariable int id) {
		Optional<LineItem> l = lineItemRepo.findById(id);
		if (l.isPresent()) {
			return l;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line item not found for id " + id);
		}
	}

	@GetMapping("/lines-for-req/{requestId}")
	public List<LineItem> lineItemsForRequest(@PathVariable int requestId) {
		return lineItemRepo.findByRequestId(requestId);
	}

	@PostMapping("")
	public LineItem add(@RequestBody LineItem lineItem) {
		LineItem li = lineItemRepo.save(lineItem);
		recalculateTotals(li.getRequest().getId());
		return li;
	}

	@PutMapping("/{id}")
	public void putLineItem(@PathVariable int id, @RequestBody LineItem lineItem) {
		if (id != lineItem.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line item id mismatch vs URL");
		} else if (lineItemRepo.existsById(lineItem.getId())) {
			LineItem li = lineItemRepo.save(lineItem);
			recalculateTotals(li.getRequest().getId());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line item not found for id " + id);
		}

	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable int id) {

		if (lineItemRepo.existsById(id)) {
			Optional<LineItem> li = lineItemRepo.findById(id);
			lineItemRepo.deleteById(id);
			recalculateTotals(li.get().getRequest().getId());

		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line item not found for id " + id);
		}
	}

	public void recalculateTotals(int requestId) {
		List<LineItem> total = lineItemRepo.findByRequestId(requestId);
		Optional<Request> requests = requestRepo.findById(requestId);
		double lineItemTotal = 0.0;
		Request request = requests.get();
//		lineItemTotal = request.getTotal();
		if(!request.equals(null)) {
		for (var li : total) {
			lineItemTotal += li.getProduct().getPrice()*li.getQuantity();			
		}
		requests.get().setTotal(lineItemTotal);
		requestRepo.save(requests.get());				
	}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line item not found for id ");
					}
	}
	
	
	

}
