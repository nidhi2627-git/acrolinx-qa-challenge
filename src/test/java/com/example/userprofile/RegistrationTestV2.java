package com.example.userprofile;

import com.example.userprofile.utils.UserProfileUtils;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

class RegistrationTestV2 {

	private static RequestSpecification requestSpec;

	@BeforeClass
	public static void setUp() throws IOException {
		UserProfileUtils.setUpRequestSpec();
	}

	@Test
	public void createUserSuccess() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		Response response = UserProfileUtils.createUser(uuid, "public/v2/");
		String id = response.path("id").toString();

		Assert.assertEquals(response.getStatusCode(), 201);
		Assert.assertEquals(response.path("name"), "testName" + uuid);
		Assert.assertEquals(response.path("email"), "testEmail" + uuid + "@gmail.com");

		// tear down test data
		UserProfileUtils.tearDown(id, "public/v2/");
	}

}
