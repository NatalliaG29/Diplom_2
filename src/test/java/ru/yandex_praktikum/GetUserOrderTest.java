package ru.yandex_praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex_praktikum.clients.UserClient;
import ru.yandex_praktikum.dataprovider.UserProvider;
import ru.yandex_praktikum.pojo.CreateUserRequest;
import ru.yandex_praktikum.pojo.LoginUserRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetUserOrderTest {
    private UserClient userClient = new UserClient();
    private ValidatableResponse response;
    private List<String> randomIds = new ArrayList<>();
    private String token;
    private CreateUserRequest createUserRequest = UserProvider.getRandomUserRequest();
    private LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    public void getOrderWithLoginUser() {
        response = userClient.create(createUserRequest);
        cod200();
        successTrue();
        response = userClient.login(loginUserRequest);
        cod200();
        successTrue();
        token = response.extract().path("accessToken");
        response = userClient.getIngredients();
        cod200();
        successTrue();
        listIngredients();
        response = userClient.createOrderWithIngredientsAndWithLoginUser(token,randomIds);
        cod200();
        successTrue();
        response = userClient.getOrderUserWithLogin(token);
        cod200();
        successTrue();
    }

    @Test
    @DisplayName("Получение заказов без авторизации пользователя")
    public void getOrderWithoutLoginUser() {
        response = userClient.getOrderUserWithoutLogin();
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

    @Step("Получаем список из 3х рандомных id ингридиентов")
    public void listIngredients(){
        String responseBody = response.extract().response().getBody().asString();
        JsonPath jsonPath = new JsonPath(responseBody);
        List<String> allId = jsonPath.getList("data._id");
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int randomIndex = random.nextInt(allId.size());
            String randomId = allId.get(randomIndex);
            randomIds.add(randomId);
        }
    }

    @Step("Удаление пользователя")
    public void deleteUser(){
        if (token != null) {
            userClient.delete(token)
                    .statusCode(202);
        }
    }
}
