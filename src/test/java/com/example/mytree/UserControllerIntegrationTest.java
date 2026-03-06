package com.example.mytree;

import com.example.mytree.domain.User;
import com.example.mytree.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void signupStoresUserWithPlainPasswordAndIpv4Address() throws Exception {
		mockMvc.perform(jsonRequest(
				post("/api/users/signup"),
				"""
					{
					  "userId": "hong01",
					  "password": "pw1234",
					  "name": "홍길동"
					}
					"""
			).with(request -> {
				request.setRemoteAddr("127.0.0.1");
				return request;
			}))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userId").value("hong01"))
			.andExpect(jsonPath("$.name").value("홍길동"))
			.andExpect(jsonPath("$.ipAddress").value("127.0.0.1"));

		User storedUser = userRepository.findByUserId("hong01");
		assertThat(storedUser).isNotNull();
		assertThat(storedUser.getPassword()).isEqualTo("pw1234");
		assertThat(storedUser.getCreatedAt()).isNotNull();
		assertThat(storedUser.getIpAddress()).isEqualTo("127.0.0.1");
	}

	@Test
	void loginSucceedsWithRegisteredCredentials() throws Exception {
		signupUser("kim01", "pw1234", "김철수", "192.168.0.10");

		mockMvc.perform(jsonRequest(
				post("/api/users/login"),
				"""
					{
					  "userId": "kim01",
					  "password": "pw1234"
					}
					"""
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value("kim01"))
			.andExpect(jsonPath("$.name").value("김철수"))
			.andExpect(jsonPath("$.authenticated").value(true));
	}

	@Test
	void userCrudFlowSupportsReadUpdateListAndDelete() throws Exception {
		signupUser("park01", "pw1234", "박영희", "10.0.0.20");

		mockMvc.perform(get("/api/users/park01"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value("park01"))
			.andExpect(jsonPath("$.name").value("박영희"));

		mockMvc.perform(jsonRequest(
				get("/api/users"),
				null
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].userId").exists());

		mockMvc.perform(jsonRequest(
				put("/api/users/park01"),
				"""
					{
					  "password": "newPass123",
					  "name": "박수정"
					}
					"""
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value("park01"))
			.andExpect(jsonPath("$.name").value("박수정"));

		mockMvc.perform(jsonRequest(
				post("/api/users/login"),
				"""
					{
					  "userId": "park01",
					  "password": "newPass123"
					}
					"""
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authenticated").value(true));

		mockMvc.perform(delete("/api/users/park01"))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/users/park01"))
			.andExpect(status().isNotFound());
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
