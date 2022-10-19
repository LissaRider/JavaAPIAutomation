package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 05. Allure
     */
    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 05. Allure
     */
    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 05. Allure
     */
    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex16: Запрос данных другого пользователя
     */
    @Step("Make a GET-request without token and auth cookie")
    public Response makeGetRequestWithoutTokenAndCookie(String url){
        return given()
                .filter(new AllureRestAssured())
                .get(url)
                .andReturn();
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 05. Allure
     */
    @Step("Make a POST-request")
    public Response makePostRequest(String url, Map<String, String> data) {
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .post(url)
                .andReturn();
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * Ex16: Запрос данных другого пользователя
     */
    @Step("Make a POST-request and get a JsonPath object")
    public JsonPath makePostRequestAndGetJsonPath(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .jsonPath();
    }
}
