package com.example.userprofile.utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class UserProfileUtils {

    public static RequestSpecification setUpRequestSpec() {
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

        return new RequestSpecBuilder().
                setBaseUri("https://gorest.co.in/").
                addHeaders(headerMap).
                build();
    }

    public static Response createUser(String randomString, RequestSpecification requestSpec, String versionPath) throws JSONException {
        String email = "testEmail" + randomString + "@gmail.com";
        String name = "testName" + randomString;

        JSONObject requestParams = new JSONObject();
        requestParams.put("name", name);
        requestParams.put("gender", "male");
        requestParams.put("email", email);
        requestParams.put("status", "active");

        return given().
                spec(requestSpec).body(requestParams.toString()).
                when().
                post(versionPath + "users").
                then().extract().response();
    }

    public static Response readUser(String userId, RequestSpecification requestSpec, String versionPath) {
        return given().
                spec(requestSpec).
                when().
                get(versionPath + "users/" + userId).
                then().extract().response();
    }

    public static Response tearDown(String userId, RequestSpecification requestSpec, String versionPath) {
        return given().
                spec(requestSpec).
                when().
                delete(versionPath + "users/" + userId).
                then().extract().response();
    }

    public static Response createPost(String userId, String randomString, RequestSpecification requestSpec, String versionPath) throws JSONException {
        String title = "title" + randomString;
        String body = "body" + randomString;

        JSONObject requestParams = new JSONObject();
        requestParams.put("user_id", userId);
        requestParams.put("title", title);
        requestParams.put("body", body);

        return given().
                spec(requestSpec).body(requestParams.toString()).
                when().
                post(versionPath + "posts").
                then().extract().response();
    }

    public static Response tearDownPost(String postId, RequestSpecification requestSpec, String versionPath) {
        return given().
                spec(requestSpec).
                when().
                delete(versionPath + "posts/" + postId).
                then().extract().response();
    }

    public static Response readPost(String postId, RequestSpecification requestSpec, String versionPath) {
        return given().
                spec(requestSpec).
                when().
                get(versionPath + "posts/" + postId).
                then().extract().response();
    }

}
