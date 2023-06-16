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

public class ChangeUserTest {
    private UserClient userClient = new UserClient();
    private ValidatableResponse response;
    private String token;
    private CreateUserRequest createUserRequest = UserProvider.getRandomUserRequest();
    private String email = UserProvider.getRandomUserRequest().getEmail();
    private String password = UserProvider.getRandomUserRequest().getPassword();
    private String name = UserProvider.getRandomUserRequest().getName();
    private LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Изменение почты пользователя с авторизацией")
    public void changeEmailUser() {
        response = userClient.create(createUserRequest);
        cod200();
        successTrue();
        response = userClient.login(loginUserRequest);
        cod200();
        successTrue();
        token = response.extract().path("accessToken");
        response = userClient.changeEmail(token, email);
        cod200();
        successTrue();
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    public void changePasswordUser() {
        response = userClient.create(createUserRequest);
        cod200();
        successTrue();
        response = userClient.login(loginUserRequest);
        cod200();
        successTrue();
        token = response.extract().path("accessToken");
        response = userClient.changePassword(token, password);
        cod200();
        successTrue();
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    public void changeNameUser() {
        response = userClient.create(createUserRequest);
        cod200();
        successTrue();
        response = userClient.login(loginUserRequest);
        cod200();
        successTrue();
        token = response.extract().path("accessToken");
        response = userClient.changeName(token, name);
        cod200();
        successTrue();
    }

    @Test
    @DisplayName("Проверка на изменение почты пользователя без авторизации")
    public void checkChangeEmailUserWithoutLogin() {
        response = userClient.notChangeEmail(email);
        cod401();
        successFalse();
        textNotLoginUser();
    }

    @Test
    @DisplayName("Проверка на изменение почты пользователя без авторизации")
    public void checkChangePasswordUserWithoutLogin() {
        response = userClient.notChangePassword(password);
        cod401();
        successFalse();
        textNotLoginUser();
    }

    @Test
    @DisplayName("Проверка на изменение имени пользователя без авторизации")
    public void checkChangeNameUserWithoutLogin() {
        response = userClient.notChangeName(name);
        cod401();
        successFalse();
        textNotLoginUser();
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

    @Step("Проверяем что ответ возвращает [You should be authorised]")
    public void textNotLoginUser(){
        response.body("message", Matchers.equalTo("You should be authorised"));
    }

    @Step("Удаление пользователя")
    public void deleteUser(){
        if (token != null){
            userClient.delete(token)
                    .statusCode(202);
        }
    }
}
