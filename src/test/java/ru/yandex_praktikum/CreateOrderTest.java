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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CreateOrderTest {
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
    @DisplayName("Создание заказа с ингридиентами и с авторизацией пользователя")
    public void createOrderWithIngredientsAndWithLoginUser() {
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
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов и с авторизацией пользователя")
    public void createOrderWithoutIngredientsAndWithLoginUser() {
        response = userClient.create(createUserRequest);
        cod200();
        successTrue();
        response = userClient.login(loginUserRequest);
        cod200();
        successTrue();
        token = response.extract().path("accessToken");
        response = userClient.createOrderWithoutIngredientsAndWithLoginUser(token);
        cod400();
        successFalse();
        textNotIngredient();
    }

    @Test
    @DisplayName("Создание заказа с ингридиентами и без авторизации пользователя")
    public void createOrderWithIngredientsAndWithoutLoginUser() {
        response = userClient.getIngredients();
        cod200();
        successTrue();
        listIngredients();
        response = userClient.createOrderWithIngredientsAndWithoutLoginUser(randomIds);
        //по требованиям должна быть ошибка 401 у неавторизованного пользователя, но в Postman приходит ответ 200
        cod401();
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов и без авторизации пользователя")
    public void createOrderWithoutIngredientsAndWithoutLoginUser() {
        response = userClient.createOrderWithoutIngredientsAndWithoutLoginUser();
        cod400();
        successFalse();
        textNotIngredient();
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов и без авторизации пользователя")
    public void createOrderWithInvalidIdIngredientsAndWithoutLoginUser() {
        List<String> listInvalidId = new ArrayList<>(Arrays.asList("61c111111d1f8200aa00", "61c0c111111bdaaa01", "61c0c5a7111aaa02"));
        response = userClient.createOrderWithIngredientsAndWithoutLoginUser(listInvalidId);
        cod500();
    }

    @After
    public void tearDown(){
        deleteUser();
    }

    @Step("Проверяем что код ответа 200")
    public void cod200(){
        response.statusCode(200);
    }

    @Step("Проверяем что код ответа 400")
    public void cod400(){
        response.statusCode(400);
    }

    @Step("Проверяем что код ответа 401")
    public void cod401(){
        response.statusCode(401);
    }

    @Step("Проверяем что код ответа 500")
    public void cod500(){
        response.statusCode(500);
    }

    @Step("Проверяем что ответ возвращает [success: true]")
    public void successTrue() {
        response.body("success", Matchers.equalTo(true));
    }

    @Step("Проверяем что ответ возвращает [success: false]")
    public void successFalse() {
        response.body("success", Matchers.equalTo(false));
    }

    @Step("Проверяем что ответ возвращает [Ingredient ids must be provided]")
    public void textNotIngredient(){
        response.body("message", Matchers.equalTo("Ingredient ids must be provided"));
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
        String contentType = response.extract().header("Content-Type");
        if (token != null && contentType.contains("application/json")) {
            userClient.delete(token)
                    .statusCode(202);
        }
    }
}
