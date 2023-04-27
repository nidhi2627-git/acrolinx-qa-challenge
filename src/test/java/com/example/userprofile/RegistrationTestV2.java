package com.example.userprofile;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;

import static io.restassured.RestAssured.given;

class RegistrationTestV2 {

	private static RequestSpecification requestSpec;
	String uuid = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUp() throws IOException {
		String authToken = null;
		try (InputStream inputStream = new FileInputStream("./src/test/resources/authToken.properties")) {
			Properties authTokenProp = new Properties();
			authTokenProp.load(inputStream);
			authToken = authTokenProp.getProperty("authToken");
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Accept", "application/json");
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Authorization", "Bearer " + authToken);

		requestSpec = new RequestSpecBuilder().
				setBaseUri("https://gorest.co.in/").
				addHeaders(headerMap).
				build();
	}

	public Response createUser() throws JSONException {
		String email = "testEmail" + uuid + "@gmail.com";
		String name = "testName" + uuid;

		JSONObject requestParams = new JSONObject();
		requestParams.put("name", name);
		requestParams.put("gender", "male");
		requestParams.put("email", email);
		requestParams.put("status", "active");

		Response response = given().
				spec(requestSpec).body(requestParams.toString()).
				when().
				post("public/v2/users").
				then().extract().response();
		return response;
	}

	public void tearDown(String userId) {
		given().
				spec(requestSpec).
				when().
				delete("public/v2/users/" + userId);
	}

	@Test
	public void createUserSuccess() throws JSONException {
		Response response = createUser();
		System.out.println(response.jsonPath().toString());
		String id = response.path("id").toString();

		Assert.assertEquals(response.getStatusCode(), 201);
		Assert.assertEquals(response.path("name"), "testName" + uuid);
		Assert.assertEquals(response.path("email"), "testEmail" + uuid + "@gmail.com");

		tearDown(id);
	}

}
