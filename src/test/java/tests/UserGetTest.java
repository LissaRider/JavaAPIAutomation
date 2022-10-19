package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * IV. Создание фреймворка и запуск в Docker
 * <p>
 * 03. Просмотр пользователя
 */
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        System.out.println(responseUserData.asString());

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

//        Response responseGetAuth = RestAssured
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

//        System.out.println(responseUserData.asString());

//        Assertions.assertJsonHasField(responseUserData, "username");
//        Assertions.assertJsonHasField(responseUserData, "firstName");
//        Assertions.assertJsonHasField(responseUserData, "lastName");
//        Assertions.assertJsonHasField(responseUserData, "email");

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex16: Запрос данных другого пользователя
     */
    @Test
    public void testGetUserDetailsAuthAsAnotherUser(){

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = responseGetAuth.getCookie("auth_sid");
        String header = responseGetAuth.getHeader("x-csrf-token");

        int userId = this.getIntFromJson(responseGetAuth, "user_id");

        Response responseCheckAuth = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/auth",
                header,
                cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", userId);

        Map<String, String> newUserData = DataGenerator.getRegistrationData();

        JsonPath responseCreateUser = apiCoreRequests.makePostRequestAndGetJsonPath(
                "https://playground.learnqa.ru/api/user",
                newUserData);

        String newUserId = responseCreateUser.getString("id");

        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(
                String.format("https://playground.learnqa.ru/api/user/%s", newUserId));

        String[] unexpectedFieldNames = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFieldNames);
    }
}
