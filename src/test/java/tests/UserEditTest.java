package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * IV. Создание фреймворка и запуск в Docker
 * <p>
 * 04. Редактирование пользователя
 * <p>
 * Ex17: Негативные тесты на PUT
 */
public class UserEditTest extends BaseTestCase {

    final String USER_LOGIN_REQUEST = "https://playground.learnqa.ru/api/user/login/";
    final String USER_REQUEST = "https://playground.learnqa.ru/api/user/";

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    Map<String, String> userData;
    String email;
    String password;
    String username;
    String firstName;
    String userId;
    String cookie;
    String header;

    @BeforeEach
    public void generateUser() {

        //GENERATE USER
        this.userData = DataGenerator.getRegistrationData();

        this.email = userData.get("email");
        this.password = userData.get("password");
        this.username = userData.get("username");
        this.firstName = userData.get("firstName");

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestAndGetJsonPath(USER_REQUEST, this.userData);

        this.userId = responseCreateAuth.getString("id");
    }

    public void loginUser(String email, String password) {

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        Response responseGetAuth = apiCoreRequests.makePostRequest(USER_LOGIN_REQUEST, authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
    }

    @Test
    public void testEditJustCreatedUser() {

        //LOGIN
        loginUser(email, password);

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        apiCoreRequests.makePutRequest(USER_REQUEST + this.userId, editData, this.header, this.cookie);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(USER_REQUEST + this.userId, this.header, this.cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testEditJustCreatedUserWithoutAuth() {

        //EDIT
        String newUsername = "Changed Username";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newUsername);

        Response responseEditUser = apiCoreRequests.makePutRequestWithoutTokenAndCookie(USER_REQUEST + this.userId, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(USER_REQUEST + this.userId);

        Assertions.assertJsonByName(responseUserData, "username", username);
    }

    @Test
    public void testEditJustCreatedUserByAnotherUserAuth() {

        //LOGIN
        loginUser("vinkotov@example.com", "1234");

        //EDIT
        String newUsername = "Changed Username";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newUsername);

        Response responseEditUser = apiCoreRequests.makePutRequest(USER_REQUEST + userId, editData, this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(USER_REQUEST + this.userId);

        Assertions.assertJsonByName(responseUserData, "username", username);
    }

    @Test
    public void testEditUserEmailToInvalid() {

        //LOGIN
        loginUser(email, password);

        //EDIT
        String invalidEmail = DataGenerator.getRandomEmail().replaceAll("@", "");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", invalidEmail);

        Response responseEditUser = apiCoreRequests.makePutRequest(USER_REQUEST + userId, editData, this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(USER_REQUEST + this.userId, this.header, this.cookie);

        Assertions.assertJsonByName(responseUserData, "email", email);
    }

    @Test
    public void testEditUserNameToShortName() {

        //LOGIN
        loginUser(email, password);

        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", getRandomString(1));

        Response responseEditUser = apiCoreRequests.makePutRequest(USER_REQUEST + this.userId, editData, this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Too short value for field firstName\"}");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(USER_REQUEST + this.userId, this.header, this.cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", firstName);
    }

    private String getRandomString(int count) {
        String characters =
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                        "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "abcdefghijklmnopqrstuvwxyz" +
                        "0123456789" +
                        "~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
        return RandomStringUtils.random(count, characters);
    }
}
