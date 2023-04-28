package com.example.userprofile;

import com.example.userprofile.utils.UserProfileUtils;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

class RegistrationTestV2 {

	private RequestSpecification requestSpec;
	private static String versionPath;

	@BeforeClass
	public void setUp() {
		requestSpec = UserProfileUtils.setUpRequestSpec();
		versionPath = "public/v2/";
	}

	@Test
	public void createUserSuccess() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		Response response = UserProfileUtils.createUser(uuid, requestSpec, versionPath);
		String id = response.path("id").toString();

		Assert.assertEquals(response.getStatusCode(), 201);
		Assert.assertEquals(response.path("name"), "testName" + uuid);
		Assert.assertEquals(response.path("email"), "testEmail" + uuid + "@gmail.com");

		// tear down test data
		UserProfileUtils.tearDown(id, requestSpec, versionPath);
	}

	@Test
	public void createUserDuplicateFailure() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		String id = UserProfileUtils.createUser(uuid, requestSpec, versionPath).path("id").toString();

		Response response = UserProfileUtils.createUser(uuid, requestSpec, versionPath);

		JSONArray jsonResponseArray = new JSONArray(response.body().asString());
		Assert.assertEquals(response.getStatusCode(), 422);
		Assert.assertEquals(jsonResponseArray.getJSONObject(0).getString("field"), "email");
		Assert.assertEquals(jsonResponseArray.getJSONObject(0).getString("message"), "has already been taken");

		// tear down test data
		UserProfileUtils.tearDown(id, requestSpec, versionPath);
	}

	@Test
	public void getUserDetailsSuccess() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		String id = UserProfileUtils.createUser(uuid, requestSpec, versionPath).path("id").toString();

		Response response = UserProfileUtils.readUser(id, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.path("id").toString(), id);
		Assert.assertEquals(response.path("name"), "testName" + uuid);
		Assert.assertEquals(response.path("email"), "testEmail" + uuid + "@gmail.com");

		// tear down test data
		UserProfileUtils.tearDown(id, requestSpec, versionPath);
	}

	@Test
	public void getUserDetailsNotFound() {
		String uuid = UUID.randomUUID().toString();
		Response response = UserProfileUtils.readUser(uuid, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertEquals(response.path("message"), "Resource not found");
	}

	@Test
	public void deleteUserSuccess() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		String id = UserProfileUtils.createUser(uuid, requestSpec, versionPath).path("id").toString();

		Response response = UserProfileUtils.tearDown(id, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 204);
	}

	@Test
	public void deleteNonExistingUser() {
		String uuid = UUID.randomUUID().toString();
		Response response = UserProfileUtils.tearDown(uuid, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertEquals(response.path("message"), "Resource not found");
	}

	@Test
	public void createPostSuccess() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		//create a new user
		String userId = UserProfileUtils.createUser(uuid, requestSpec, versionPath).path("id").toString();
		//create a new user's post
		Response response = UserProfileUtils.createPost(userId, uuid, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 201);
		Assert.assertEquals(response.path("user_id").toString(), userId);
		Assert.assertEquals(response.path("title"), "title" + uuid);
		Assert.assertEquals(response.path("body"), "body" + uuid);

		String postId = response.path("id").toString();

		// tear down user's test data
		UserProfileUtils.tearDown(userId, requestSpec, versionPath);
		// tear down user's post test data
		UserProfileUtils.tearDownPost(postId, requestSpec, versionPath);
	}

	@Test
	public void createPostWithNonExistingUser() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		//create a user's post with non-existing user
		Response response = UserProfileUtils.createPost(uuid, uuid, requestSpec, versionPath);
		JSONArray jsonResponseArray = new JSONArray(response.body().asString());

		Assert.assertEquals(response.getStatusCode(), 422);
		Assert.assertEquals(jsonResponseArray.getJSONObject(0).getString("field"), "user");
		Assert.assertEquals(jsonResponseArray.getJSONObject(0).getString("message"), "must exist");
	}

	@Test
	public void deletePostSuccess() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		//create a new user
		String userId = UserProfileUtils.createUser(uuid, requestSpec, versionPath).path("id").toString();
		//create a new user's post
		String postId = UserProfileUtils.createPost(userId, uuid, requestSpec, versionPath).path("id").toString();

		//delete user's post & fetch the response
		Response response = UserProfileUtils.tearDownPost(postId, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 204);

		// tear down user's test data
		UserProfileUtils.tearDown(userId, requestSpec, versionPath);
	}

	@Test
	public void deleteNonExistingPost() {
		String uuid = UUID.randomUUID().toString();
		//delete non-existing user's post and fetch response
		Response response = UserProfileUtils.tearDownPost(uuid, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertEquals(response.path("message"), "Resource not found");
	}

	@Test
	public void getPostDetailsSuccess() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		//create a new user
		String userId = UserProfileUtils.createUser(uuid, requestSpec, versionPath).path("id").toString();
		//create a new user's post
		String postId = UserProfileUtils.createPost(userId, uuid, requestSpec, versionPath).path("id").toString();

		//get user's post and fetch response
		Response response = UserProfileUtils.readPost(postId, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.path("id").toString(), postId);
		Assert.assertEquals(response.path("user_id").toString(), userId);

		// tear down user's test data
		UserProfileUtils.tearDown(userId, requestSpec, versionPath);
		// tear down user's post test data
		UserProfileUtils.tearDownPost(postId, requestSpec, versionPath);
	}

	@Test
	public void getPostDetailsNotFound() {
		String uuid = UUID.randomUUID().toString();
		//read non-existing post & fetch response
		Response response = UserProfileUtils.readPost(uuid, requestSpec, versionPath);

		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertEquals(response.path("message"), "Resource not found");
	}

}
