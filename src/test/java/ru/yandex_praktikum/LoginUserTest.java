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

public class LoginUserTest {
    private UserClient userClient = new UserClient();
    private ValidatableResponse response;
    private String token;
    private CreateUserRequest createUserRequest = UserProvider.getRandomUserRequest();
    private LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Авторизация существующего пользователя")
    public void loginUser() {
        response = userClient.create(createUserRequest);
        cod200();
        successTrue();
        response = userClient.login(loginUserRequest);
        cod200();
        successTrue();
        token = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка на авторизацию пользователя с неверным логином и паролем")
    public void checkLoginUserWithInvalidLoginPassword() {
        LoginUserRequest request = new LoginUserRequest(UserProvider.getRandomUserRequest().getEmail(), UserProvider.getRandomUserRequest().getPassword());
        response = userClient.login(request);
        cod401();
        successFalse();
        textIncorrectLoginPassword();
    }

    @After
    public void tearDown(){
        deleteUser();
    }

    @Step("Проверяем что код ответа 200")
    public void cod200(){
        response.statusCode(200);
    }

    @Step("Проверяем что код ответа 401")
    public void cod401(){
        response.statusCode(401);
    }

    @Step("Проверяем что ответ возвращает [success: true]")
    public void successTrue() {
        response.body("success", Matchers.equalTo(true));
    }

    @Step("Проверяем что ответ возвращает [success: false]")
    public void successFalse() {
        response.body("success", Matchers.equalTo(false));
    }

    @Step("Проверяем что ответ возвращает [email or password are incorrect]")
    public void textIncorrectLoginPassword(){
        response.body("message", Matchers.equalTo("email or password are incorrect"));
    }

    @Step("Удаление пользователя")
    public void deleteUser(){
        if (token != null){
            userClient.delete(token)
                    .statusCode(202);
        }
    }
}
