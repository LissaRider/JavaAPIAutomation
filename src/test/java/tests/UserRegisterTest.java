package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

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

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex15: Тесты на метод user
     * <p>
     * Создание пользователя с некорректным email - без символа @
     */
    @Test
    public void testCreateUserWithInvalidEmail() {

        String email = DataGenerator.getRandomEmail().replaceAll("@", "");

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser, "Invalid email format");
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex15: Тесты на метод user
     * <p>
     * Создание пользователя без указания одного из полей
     */
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithMissingField(String field) {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(field); // полностью не указывая поле

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser,
                String.format("The following required params are missed: %s", field));
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex15: Тесты на метод user
     * <p>
     * Создание пользователя если одно из полей равно null
     */
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithNullField(String field) {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put(field, null); // нулевое значение

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser,
                String.format("The following required params are missed: %s", field));
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex15: Тесты на метод user
     * <p>
     * Создание пользователя если значение одного из полей пустое
     */
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithEmptyField(String field) {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put(field, ""); // пустое значение

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser,
                String.format("The value of '%s' field is too short", field));
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex15: Тесты на метод user
     * <p>
     * Создание пользователя если значение одного из полей равно одному пробелу
     */
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithOneSpaceField(String field) {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put(field, " "); // один пробел

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser,
                String.format("The value of '%s' field is too short", field));
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex15: Тесты на метод user
     * <p>
     * Создание пользователя с очень коротким именем в один символ
     */
    @Test
    public void testCreateUserWithShortName() {

        Map<String, String> userData = new HashMap<>();
        String characters =
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789" +
                "~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
        String newFirstName = RandomStringUtils.random(1, characters);
        userData.put("firstName", newFirstName); // 1 символ

        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser,
                "The value of 'firstName' field is too short");
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex15: Тесты на метод user
     * <p>
     * Создание пользователя с очень длинным именем - длиннее 250 символов
     */
    @Test
    public void testCreateUserWithLongName() {

        Map<String, String> userData = new HashMap<>();
        String characters =
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                " " +
                "0123456789" +
                "~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
        String newFirstName = RandomStringUtils.random(251, characters);
        userData.put("firstName", newFirstName);

        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser,
                "The value of 'firstName' field is too long");
    }
}
