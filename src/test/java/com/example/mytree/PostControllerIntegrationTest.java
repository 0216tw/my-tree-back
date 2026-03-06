package com.example.mytree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PostControllerIntegrationTest {

	private MockMvc mockMvc;

	@org.springframework.beans.factory.annotation.Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void createAndReadPosts() throws Exception {
		signupUser("writer01", "pw1234", "Writer One", "127.0.0.1");

		mockMvc.perform(jsonRequest(
				post("/api/posts"),
				"""
					{
					  "userId": "writer01",
					  "title": "First note",
					  "content": "This is the first post."
					}
					"""
			))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.postNo").isNumber())
			.andExpect(jsonPath("$.userId").value("writer01"))
			.andExpect(jsonPath("$.title").value("First note"))
			.andExpect(jsonPath("$.content").value("This is the first post."));

		mockMvc.perform(get("/api/posts/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.postNo").value(1))
			.andExpect(jsonPath("$.title").value("First note"));

		mockMvc.perform(get("/api/posts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].postNo").value(1))
			.andExpect(jsonPath("$[0].userId").value("writer01"));
	}

	@Test
	void createPostFailsWhenUserDoesNotExist() throws Exception {
		mockMvc.perform(jsonRequest(
				post("/api/posts"),
				"""
					{
					  "userId": "missing-user",
					  "title": "Invalid post",
					  "content": "Should fail."
					}
					"""
			))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("User not found: missing-user"));
	}

	private void signupUser(String userId, String password, String name, String ipAddress) throws Exception {
		String payload = """
			{
			  "userId": "%s",
			  "password": "%s",
			  "name": "%s"
			}
			""".formatted(userId, password, name);

		mockMvc.perform(jsonRequest(post("/api/users/signup"), payload).with(request -> {
			request.setRemoteAddr(ipAddress);
			return request;
		}))
			.andExpect(status().isCreated());
	}

	private MockHttpServletRequestBuilder jsonRequest(MockHttpServletRequestBuilder builder, String body) {
		MockHttpServletRequestBuilder requestBuilder = builder.contentType(MediaType.APPLICATION_JSON);
		if (body != null) {
			requestBuilder = requestBuilder.content(body);
		}

		return requestBuilder;
	}
}
