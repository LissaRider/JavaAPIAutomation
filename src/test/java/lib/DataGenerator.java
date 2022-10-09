package lib;

import java.text.SimpleDateFormat;

/**
 * IV. Создание фреймворка и запуск в Docker
 * <p>
 * 02. Создание нового пользователя
 */
public class DataGenerator {

    public static String getRandomEmail() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return String.format("learnqa%s@example.com", timestamp);
    }
}
