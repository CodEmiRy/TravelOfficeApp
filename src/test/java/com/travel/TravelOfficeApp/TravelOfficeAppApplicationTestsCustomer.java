package com.travel.TravelOfficeApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TravelOfficeAppApplicationTestsCustomer {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomerRepository customerRepository;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		customerRepository.deleteAll();
	}

	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.customer").exists());
	}

	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post("/customer").content(
				"{\"firstName\": \"Emilia\", \"lastName\":\"Baggins\", \"address\":\"adresik\"}")).andExpect(
				status().isCreated()).andExpect(
				header().string("Location", containsString("customer/")));
	}

	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/customer").content(
				"{\"firstName\": \"Emilia\", \"lastName\":\"Baggins\", \"address\":\"adresik\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Emilia")).andExpect(
				jsonPath("$.lastName").value("Baggins")).andExpect(
				jsonPath("$.address").value("adresik"));
	}

	@Test
	public void shouldQueryEntity() throws Exception {

		mockMvc.perform(post("/customer").content(
				"{ \"firstName\": \"Emilia\", \"lastName\":\"Baggins\", \"address\":\"adresik\"}")).andExpect(
				status().isCreated());

		mockMvc.perform(
				get("/customer/search/findByLastName?name={name}", "Baggins")).andExpect(
				status().isOk()).andExpect(
				jsonPath("$._embedded.customer[0].firstName").value(
						"Emilia"));
	}

	@Test
	public void shouldUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/customer").content(
				"{\"firstName\": \"Emilia\", \"lastName\":\"Baggins\", \"address\":\"adresik\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put(location).content(
				"{\"firstName\": \"Emilia\", \"lastName\":\"Baggins\", \"address\":\"adresik\"}")).andExpect(
				status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Emilia")).andExpect(
				jsonPath("$.lastName").value("Baggins")).andExpect(
				jsonPath("$.address").value("adresik"));
	}

	@Test
	public void shouldPartiallyUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/customer").content(
				"{\"firstName\": \"Emilia\", \"lastName\":\"Baggins\", \"address\":\"adresik\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(
				patch(location).content("{\"firstName\": \"Emilia\", \"address\":\"adresik\"}")).andExpect(
				status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Emilia")).andExpect(
				jsonPath("$.lastName").value("Baggins")).andExpect(
				jsonPath("$.address").value("adresik"));
	}

	@Test
	public void shouldDeleteEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/customer").content(
				"{ \"firstName\": \"Bilbo\", \"lastName\":\"Baggins\", \"address\":\"adresik\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}
}

