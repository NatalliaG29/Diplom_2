package ru.yandex_praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex_praktikum.clients.UserClient;
import ru.yandex_praktikum.dataprovider.UserProvider;
import ru.yandex_praktikum.pojo.CreateUserRequest;
import ru.yandex_praktikum.pojo.LoginUserRequest;

public class CreateUserTest {
    private UserClient userClient = new UserClient();
    private ValidatableResponse response;
    private String token;
    private CreateUserRequest createUserRequest = UserProvider.getRandomUserRequest();
    private LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
    private CreateUserRequest request = new CreateUserRequest();

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUser() {
        response = userClient.create(createUserRequest);
        cod200();
        successTrue();
        response = userClient.login(loginUserRequest);
        cod200();
        successTrue();
        token = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка на создание пользователя, который уже зарегистрирован")
        public void checkCreateDuplicateUser() {
        response = userClient.create(createUserRequest);
        cod200();
        successTrue();
        response = userClient.login(loginUserRequest);
        cod200();
        successTrue();
        token = response.extract().path("accessToken");
        response = userClient.create(createUserRequest);
        cod403();
        successFalse();
        textDuplicateUser();
    }

    @Test
    @DisplayName("Проверка на создание пользователя без заполненного обязательного поля - почта")
        public void checkCreateUserWithoutEmail() {
        request.setEmail("");
        request.setPassword("1234");
        request.setName("Xi");

        response = userClient.create(request);
        cod403();
        successFalse();
        textInsufficientDataLogin();
    }

    @After
    public void tearDown(){
        deleteUser();
    }

    @Step("Проверяем что код ответа 200")
    public void cod200(){
        response.statusCode(200);
    }

    @Step("Проверяем что код ответа 403")
    public void cod403(){
        response.statusCode(403);
    }

    @Step("Проверяем что ответ возвращает [success: true]")
    public void successTrue() {
        response.body("success", Matchers.equalTo(true));
    }

    @Step("Проверяем что ответ возвращает [success: false]")
    public void successFalse() {
        response.body("success", Matchers.equalTo(false));
    }

    @Step("Проверяем что ответ возвращает [User already exists]")
    public void textDuplicateUser(){
        response.body("message", Matchers.equalTo("User already exists"));
    }

    @Step("Проверяем что ответ возвращает [Email, password and name are required fields]")
    public void textInsufficientDataLogin(){
        response.body("message", Matchers.equalTo("Email, password and name are required fields"));
    }

    @Step("Удаление пользователя")
    public void deleteUser(){
        if (token != null){
            userClient.delete(token)
                    .statusCode(202);
        }
    }
}
