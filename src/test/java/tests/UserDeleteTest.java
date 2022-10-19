package tests;

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
 * Ex18: Тесты на DELETE
 */
public class UserDeleteTest extends BaseTestCase {
    final String USER_REQUEST = "https://playground.learnqa.ru/api/user/";
    final String USER_LOGIN_REQUEST = "https://playground.learnqa.ru/api/user/login";
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    Map<String, String> userData;
    String email;
    String password;
    String username;
    String firstName;
    String userId;
    String cookie;
    String header;

    public void generateUser() {

        //GENERATE USER
        this.userData = DataGenerator.getRegistrationData();

        this.email = this.userData.get("email");
        this.password = this.userData.get("password");
        this.username = this.userData.get("username");
        this.firstName = this.userData.get("firstName");

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
    public void testNotDeleteUserWithIdEqualsTwo() {

        //LOGIN
        loginUser("vinkotov@example.com", "1234");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(USER_REQUEST + "2", this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(USER_REQUEST + "2", this.header, this.cookie);

        String[] expectedFieldNames = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFieldNames);
    }

    @Test
    public void testDeleteJustCreatedUser() {

        //GENERATE USER
        generateUser();

        //LOGIN
        loginUser(this.email, this.password);

        //GET USER 2 DATA BEFORE
        Response responseUserDataBefore = apiCoreRequests.makeGetRequest(USER_REQUEST + "2", this.header, this.cookie);

        String expectedUsername = this.getStringFromJson(responseUserDataBefore, "username");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(USER_REQUEST + this.userId, this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //GET USER 2 DATA AFTER
        Response responseUserDataAfter = apiCoreRequests.makeGetRequest(USER_REQUEST + "2", this.header, this.cookie);

        String[] unexpectedFieldNames = {"firstName", "lastName", "email"};
        Assertions.assertJsonByName(responseUserDataAfter, "username", expectedUsername);
        Assertions.assertJsonHasNotFields(responseUserDataAfter, unexpectedFieldNames);
    }

    @Test
    public void testDeleteJustCreatedUserByAnotherUser() {

        //GENERATE USER
        generateUser();

        //LOGIN
        loginUser("vinkotov@example.com", "1234");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(USER_REQUEST + this.userId, this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(USER_REQUEST + this.userId);

        Assertions.assertJsonByName(responseUserData, "username", this.username);
    }
}
