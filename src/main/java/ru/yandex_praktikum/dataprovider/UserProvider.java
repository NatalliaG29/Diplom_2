package ru.yandex_praktikum.dataprovider;

import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex_praktikum.pojo.CreateUserRequest;

public class UserProvider {
    public static CreateUserRequest getRandomUserRequest(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(RandomStringUtils.randomAlphabetic(4) + "@yandex.ru");
        createUserRequest.setPassword(RandomStringUtils.randomAlphabetic(3));
        createUserRequest.setName(RandomStringUtils.randomAlphabetic(5));
        return createUserRequest;
    }
}
