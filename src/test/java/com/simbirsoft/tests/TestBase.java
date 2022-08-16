package com.simbirsoft.tests;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public class TestBase {
    private static String baseUrl = "http://demowebshop.tricentis.com";
    public static String cookie;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = baseUrl;
    }

    private static String getCookieWithApi() {
        Response response1 = given()
                .when()
                .post(baseUrl)
                .then()
                .log().body()
                .statusCode(200)
                .extract()
                .response();
        return "Nop.customer=" + response1.cookies().get("Nop.customer") + ";";
    }

    @BeforeAll
    private static void writesAToken() {
        cookie = getCookieWithApi();
    }
}
