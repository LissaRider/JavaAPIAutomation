package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

/**
 * IV. Создание фреймворка и запуск в Docker
 * <p>
 * 05. Allure
 */
@Epic("Authorisation cases")
@Feature("Authorisation")
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

//        Response responseGetAuth = RestAssured
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData
                );

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Description("This test successfully authorize user by email and password")
    @DisplayName("Test positive auth user")
    public void testAuthUser() {

//        Response responseCheckAuth = RestAssured
//                .given()
//                .header("x-csrf-token", this.header)
//                .cookie("auth_sid", this.cookie)
//                .get("https://playground.learnqa.ru/api/user/auth")
//                .andReturn();
        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/auth",
                        this.header,
                        this.cookie
                );

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @ParameterizedTest
    @Description("This tests checks authorization w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {

//        RequestSpecification spec = RestAssured.given();
//        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        switch (condition) {
            case "cookie": {
                Response responseForCheck = apiCoreRequests
                        .makeGetRequestWithCookie(
                                "https://playground.learnqa.ru/api/user/auth",
                                this.cookie
                        );
                Assertions.assertJsonByName(responseForCheck, "user_id", 0);
//                spec.cookie("auth_sid", this.cookie);
                break;
            }
            case "headers": {
                Response responseForCheck = apiCoreRequests
                        .makeGetRequestWithToken(
                                "https://playground.learnqa.ru/api/user/auth",
                                this.header
                        );
                Assertions.assertJsonByName(responseForCheck, "user_id", 0);
//                spec.header("x-csrf-token", this.header);
                break;
            }
            default:
                throw new IllegalArgumentException(String.format("Condition value is unknown: %s", condition));
        }

//        Response responseForCheck = spec.get().andReturn();
//
//        Assertions.assertJsonByName(responseForCheck, "user_id", 0);
    }
}
