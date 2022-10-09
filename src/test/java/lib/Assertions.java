package lib;

import io.restassured.response.Response;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {

    public static void assertJsonByName(Response response, String name, int expectedValue) {

        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);

        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 01. Создание существующего пользователя
     */
    public static void assertResponseTextEquals(Response response, String expectedAnswer) {
        assertEquals(expectedAnswer,
                response.asString(),
                "Response text is not as expected"
        );
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 01. Создание существующего пользователя
     */
    public static void assertResponseCodeEquals(Response response, int expectedCodeStatus) {
        assertEquals(expectedCodeStatus,
                response.statusCode(),
                "Response status code is not as expected"
        );
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 02. Создание нового пользователя
     */
    public static void assertJsonHasField(Response response, String expectedFieldName) {
        response.then().assertThat().body("$", hasKey(expectedFieldName));
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 03. Просмотр пользователя
     */
    public static void assertJsonHasFields(Response response, String[] expectedFieldNames) {
        Arrays.stream(expectedFieldNames)
                .forEach(expectedFieldName -> response
                        .then()
                        .assertThat()
                        .body("$", hasKey(expectedFieldName)));
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 03. Просмотр пользователя
     */
    public static void assertJsonHasNotField(Response response, String unexpectedFieldName) {
        response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }


}
