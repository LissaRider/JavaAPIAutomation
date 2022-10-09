package lib;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 02. Создание нового пользователя
     */
    public static String getRandomEmail() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return String.format("learnqa%s@example.com", timestamp);
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 04. Редактирование пользователя
     */
    public static Map<String, String> getRegistrationData() {

        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password", "123");
        data.put("username", "learnqa");
        data.put("firstName", "learnqa");
        data.put("lastName", "learnqa");

        return data;
    }

    /**
     * IV. Создание фреймворка и запуск в Docker
     * <p>
     * 04. Редактирование пользователя
     */
    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {

        Map<String, String> defaultValues = DataGenerator.getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        Arrays.stream(keys)
                .forEach(key -> userData.put(
                        key,
                        nonDefaultValues.containsKey(key)
                                ? nonDefaultValues.get(key)
                                : defaultValues.get(key)
                ));
        return userData;
    }
}
