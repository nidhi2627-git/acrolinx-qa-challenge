package com.example.userprofile;

import com.example.userprofile.utils.UserProfileUtils;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

class RegistrationTestV1 {
    private RequestSpecification requestSpec;
    private static String versionPath;

    @BeforeClass
    private void setUp() {
        requestSpec = UserProfileUtils.setUpRequestSpec();
        versionPath = "public/v1/";
    }

    @Test
    public void getUserDetailsSuccess() throws JSONException {
        String uuid = UUID.randomUUID().toString();
        //create a user
        String id = UserProfileUtils.createUser(uuid, requestSpec, versionPath).path("data.id").toString();

        Response response = UserProfileUtils.readUser(id, requestSpec, versionPath);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.path("data.id").toString(), id);
        Assert.assertEquals(response.path("data.name"), "testName" + uuid);
        Assert.assertEquals(response.path("data.email"), "testEmail" + uuid + "@gmail.com");

        // tear down test data
        UserProfileUtils.tearDownUser(id, requestSpec, versionPath);
    }

    @Test
    public void getUserDetailsNotFound() {
        String uuid = UUID.randomUUID().toString();
        //read user by non-existing id
        Response response = UserProfileUtils.readUser(uuid, requestSpec, versionPath);

        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertEquals(response.path("data.message"), "Resource not found");
    }

}
