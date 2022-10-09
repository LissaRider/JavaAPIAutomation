package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 01. Создание существующего пользователя
     * 04. Редактирование пользователя
     */
    @Test
    public void testCreateUserWithExistingEmail() {

        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);
//        userData.put("password", "123");
//        userData.put("username", "learnqa");
//        userData.put("firstName", "learnqa");
//        userData.put("lastName", "learnqa");

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, String.format("Users with email '%s' already exists", email));
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 02. Создание нового пользователя
     * 04. Редактирование пользователя
     */
    @Test
    public void testCreateUserSuccessfully() {

//        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();
//        Map<String, String> userData = new HashMap<>();
//        userData.put("email", email);
//        userData.put("password", "123");
//        userData.put("username", "learnqa");
//        userData.put("firstName", "learnqa");
//        userData.put("lastName", "learnqa");

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
//        System.out.println(responseCreateAuth.asString());
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }
}
