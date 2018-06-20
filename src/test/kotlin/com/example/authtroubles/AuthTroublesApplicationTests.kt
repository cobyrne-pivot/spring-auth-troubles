package com.example.authtroubles

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import javax.servlet.Filter


@RunWith(SpringRunner::class)
@SpringBootTest
class AuthTroublesApplicationTests {
	@Autowired
	private lateinit var context: WebApplicationContext

	@Autowired
	private lateinit var springSecurityFilterChain: Filter

	private lateinit var mockMvc: MockMvc

	@Before
	fun setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilter<DefaultMockMvcBuilder>(springSecurityFilterChain)
				.build()
	}

	@Test
	fun `this test passes incorrectly`(){
		// these pass correctly
		mockMvc.perform(get("/api/foo").with(httpBasic("username", "password")))
				.andExpect(status().isOk)
		mockMvc.perform(get("/api/foo").with(httpBasic("guest", "password")))
				.andExpect(status().isForbidden)
		mockMvc.perform(get("/api/foo"))
				.andExpect(status().isUnauthorized)
		mockMvc.perform(get("/home"))
				.andExpect(status().is3xxRedirection)

		// these pass but in real life we don't see this behavior
		mockMvc.perform(get("/api/foo").with(httpBasic("username", "wrong")))
				.andExpect(status().isUnauthorized)
		mockMvc.perform(get("/api/foo").with(httpBasic("nobody", "password")))
				.andExpect(status().isUnauthorized)
	}
}
