package com.example.springboot.controller;

import com.example.springboot.controller.exception.BarException;
import com.example.springboot.controller.exception.FooException;
import com.example.springboot.model.Meta;
import com.example.springboot.service.HelloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class HelloController {
	private long  roll = 0;

	@Autowired
	private HelloService service;

	@Operation(summary = "dummy endpoint")
	@ApiResponse(responseCode = "200", description = "no content")
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping()
	{
		log.info("ping");
		return "pong";
	}

	@Operation(summary = "tests error response from server")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "even invocations"),
			@ApiResponse(responseCode = "418", description = "odd invocations")
	} )
	@RequestMapping(path = "/roll/dice", method = RequestMethod.GET)
	public void  rollDice() throws FooException, BarException
	{
		log.info("GET  rolling dice...");
		if (roll++%2 == 0)
			throw new FooException();
		throw new BarException();
	}


	@Operation(summary = "add new object")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "add blob"),
			@ApiResponse(responseCode = "400", description = "duplicate"),
			@ApiResponse(responseCode = "500", description = "internal error"),
			@ApiResponse(responseCode = "501", description = "when objectId == 666")
	} )
	@RequestMapping(path = "/objects/{objectId}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Object> postObject(
			@PathVariable("objectId")                    Long objectId,
			@Valid @RequestBody                          String blob,
			@Valid @RequestHeader(name = "Content-Type", required = false) String content_type)
	{
		try {
			log.info("POST /api/objects -> " + objectId + "  content-type=" + content_type);
			final int  ret = service.addObject(objectId, blob);
			if (ret < 0) {
				log.warn("not impl -> " + objectId);
				return new ResponseEntity<Object>("rejecting " + objectId, HttpStatus.NOT_IMPLEMENTED);
			}
			else if (ret > 0) {
				log.info("bad request -> " + objectId);
				// return ResponseEntity.badRequest().build();
				return new ResponseEntity<Object>("duplicate", HttpStatus.NOT_IMPLEMENTED);
			}

			log.info("ok -> " + objectId);
			return ResponseEntity.ok().build();
		}
		catch (Exception ex) {
			log.error("internal error -> " + objectId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/objects", method = RequestMethod.GET)
	public ResponseEntity<Object> getObjects()
	{
		log.info("GET  /objects");
		List<Meta>  all = null;
		try {
			all = service.getAll();
			log.debug("-> items: {}", all.size());
		}
		catch (Exception ex)
		{
			// can throw or return - the ResponseExceptionHandler can map
			return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(all, HttpStatus.OK);
	}

	@RequestMapping(value = "/objects/{objectId}", method = RequestMethod.GET)
	public ResponseEntity<Object>  getObject(
			@RequestParam(name="fail_404", defaultValue ="no", required = false) String fail_404,
			@PathVariable Long objectId)
	{
		log.info("GET  /api/objects -> " + objectId);

		Meta  m = null;
		try {
			m = service.getById(objectId);
			if (m != null) {
				log.debug("Meta found with objectId={} blob={}", + objectId, m.getBlob());
				return new ResponseEntity<Object>(m, HttpStatus.OK);
			}
		}
		catch (Exception ex) {
			log.error("Error search for " + objectId, ex, ex.getMessage());
			return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		String  err404 = "{ \"response\": \"Meta + " + objectId + " not found\" }";
		if (fail_404.equals("no")) {
			return new ResponseEntity<Object>(err404, HttpStatus.NOT_FOUND);
		}

		// use the responseException handler that has annotations afor this class to do specific error response
		// the exception handler can be used to consolidate all handling based on thrown exception types
		throw new IndexOutOfBoundsException("Meta not found");
	}
}
