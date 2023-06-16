package ru.yandex_praktikum.clients;

import io.restassured.response.ValidatableResponse;
import ru.yandex_praktikum.pojo.CreateUserRequest;
import ru.yandex_praktikum.pojo.LoginUserRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {

    public ValidatableResponse create(CreateUserRequest createUserRequest) {
        return given()
                .spec(getSpec())
                .body(createUserRequest)
                .when()
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.USER_REGISTER)
                .then();
    }
    public ValidatableResponse login(LoginUserRequest loginUserRequest) {
        return given()
                .spec(getSpec())
                .body(loginUserRequest)
                .when()
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.USER_LOGIN)
                .then();
    }
    public ValidatableResponse delete(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .when()
                .delete(EndpointConstants.API_BASE_URI + EndpointConstants.USER_USER)
                .then();
    }
    public ValidatableResponse changeEmail(String token, String email) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .patch(EndpointConstants.API_BASE_URI + EndpointConstants.USER_USER)
                .then();
    }

    public ValidatableResponse changePassword(String token, String password) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("password", password);
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .patch(EndpointConstants.API_BASE_URI + EndpointConstants.USER_USER)
                .then();
    }

    public ValidatableResponse changeName(String token, String name) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .patch(EndpointConstants.API_BASE_URI + EndpointConstants.USER_USER)
                .then();
    }

    public ValidatableResponse notChangeEmail(String email) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        return given()
                .spec(getSpec())
                .body(requestBody)
                .when()
                .patch(EndpointConstants.API_BASE_URI + EndpointConstants.USER_USER)
                .then();
    }

    public ValidatableResponse notChangePassword(String password) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("password", password);
        return given()
                .spec(getSpec())
                .body(requestBody)
                .when()
                .patch(EndpointConstants.API_BASE_URI + EndpointConstants.USER_USER)
                .then();
    }

    public ValidatableResponse notChangeName(String name) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        return given()
                .spec(getSpec())
                .body(requestBody)
                .when()
                .patch(EndpointConstants.API_BASE_URI + EndpointConstants.USER_USER)
                .then();
    }

    public ValidatableResponse getIngredients() {
        return given()
                .spec(getSpec())
                .when()
                .get(EndpointConstants.API_BASE_URI + EndpointConstants.USER_INGREDIENTS)
                .then();
    }

    public ValidatableResponse createOrderWithIngredientsAndWithLoginUser(String token, List<String> ingredients) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", ingredients);
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.USER_ORDERS)
                .then();
    }

    public ValidatableResponse createOrderWithoutIngredientsAndWithLoginUser(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .when()
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.USER_ORDERS)
                .then();
    }

    public ValidatableResponse createOrderWithIngredientsAndWithoutLoginUser(List<String> ingredients) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", ingredients);
        return given()
                .spec(getSpec())
                .body(requestBody)
                .when()
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.USER_ORDERS)
                .then();
    }

    public ValidatableResponse createOrderWithoutIngredientsAndWithoutLoginUser() {
        return given()
                .spec(getSpec())
                .when()
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.USER_ORDERS)
                .then();
    }

    public ValidatableResponse getOrderUserWithLogin(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .when()
                .get(EndpointConstants.API_BASE_URI + EndpointConstants.USER_ORDERS)
                .then();
    }

    public ValidatableResponse getOrderUserWithoutLogin() {
        return given()
                .spec(getSpec())
                .when()
                .get(EndpointConstants.API_BASE_URI + EndpointConstants.USER_ORDERS)
                .then();
    }
}
