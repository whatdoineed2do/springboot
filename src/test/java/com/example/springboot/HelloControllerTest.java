package com.example.springboot.controller;

import com.example.springboot.controller.exception.BarException;
import com.example.springboot.service.HelloService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
public class HelloControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HelloService helloService;

	@BeforeEach
	public void setup() {
		Mockito.reset(helloService);
	}

	@Test
	public void testPing() throws Exception {
		mockMvc.perform(get("/api/v1/ping"))
				.andExpect(status().isOk())
				.andExpect(content().json("{ \"status\": 200, \"message\": \"pong\" }"));
	}

	@Test
	public void testRollDiceEven() throws Exception {
		Mockito.doThrow(new RuntimeException("FooException")).when(helloService).rollDice();

		mockMvc.perform(get("/api/v1/roll/dice"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testPostObject() throws Exception {
		Mockito.when(helloService.addObject(eq(1234L), any(String.class))).thenReturn(0);

		mockMvc.perform(post("/api/v1/objects/1234")
						.content("{ \"what\": \"onetwofreetfour\" }")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetObjects() throws Exception {
		mockMvc.perform(get("/api/v1/objects"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetObjectNotFound() throws Exception {
		Mockito.when(helloService.getById(1234L)).thenReturn(null);

		mockMvc.perform(get("/api/v1/objects/1234"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testExceptionNotImplemented() throws Exception {
		mockMvc.perform(get("/api/v1/exception/notimplemented"))
				.andExpect(status().isNotImplemented());
	}

	@Test
	public void testPingThrowsExceptionOnThirdCall() throws Exception {
		mockMvc.perform(get("/api/v1/ping"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/v1/ping"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/v1/ping"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testRollDiceOdd() throws Exception {
		Mockito.doThrow(new BarException(1)).when(helloService).rollDice();

		mockMvc.perform(get("/api/v1/roll/dice"))
				.andExpect(status().isIAmATeapot());
	}

	@Test
	public void testPostObjectNotImplemented() throws Exception {
		Mockito.when(helloService.addObject(eq(666L), any(String.class))).thenReturn(-1);

		mockMvc.perform(post("/api/v1/objects/666")
						.content("{ \"what\": \"test\" }")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotImplemented());
	}

	@Test
	public void testPostObjectBadRequest() throws Exception {
		Mockito.when(helloService.addObject(eq(1234L), any(String.class))).thenReturn(1);

		mockMvc.perform(post("/api/v1/objects/1234")
						.content("{ \"what\": \"test\" }")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetObjectsInternalServerError() throws Exception {
		Mockito.doThrow(new RuntimeException("Database error")).when(helloService).getAll();

		mockMvc.perform(get("/api/v1/objects"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetObjectThrowsException() throws Exception {
		Mockito.doThrow(new RuntimeException("Unexpected error")).when(helloService).getById(1234L);

		mockMvc.perform(get("/api/v1/objects/1234"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetObject404WithFail404Param() throws Exception {
		Mockito.when(helloService.getById(1234L)).thenReturn(null);

		mockMvc.perform(get("/api/v1/objects/1234?fail_404=yes"))
				.andExpect(status().isInternalServerError());
	}
}