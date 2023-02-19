import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {
    private static final String POST_NEW_USER = "api/auth/register";
    private static final String POST_LOG_USER = "api/auth/login";
    private static final String PATCH_USER_DATA = "api/auth/user";
    private static final String DELETE_USER = "api/auth/user";
    private static final String GET_INGREDIENTS = "api/ingredients";
    private static final String POST_CREATE_ORDER = "api/orders";
    private static final String GET_USER_ORDERS = "/api/orders";

    public Response createNewUser(User user) {
        return given()
                .spec(getSpecs())
                .body(user)
                .post(POST_NEW_USER);
    }

    public Response logUser(User user, String token) {
        return given()
                .spec(getSpecs())
                .header("Authorization", token)
                .body(user)
                .post(POST_LOG_USER);
    }

    public Response changeUserDataAuth(String field, String token) {
        return given()
                .spec(getSpecs())
                .header("Authorization", token)
                .body(field)
                .patch(PATCH_USER_DATA);
    }

    public Response changeUserDataNoAuth(String field) {
        return given()
                .spec(getSpecs())
                .body(field)
                .patch(PATCH_USER_DATA);
    }

    public Response deleteUser(String token) {
        return given()
                .spec(getSpecs())
                .header("Authorization", token)
                .delete(DELETE_USER);
    }

    public Response getIngredientsList() {
        return given()
                .spec(getSpecs())
                .get(GET_INGREDIENTS);
    }

    public Response createOrder(Order order, String token) {
        return given()
                .spec(getSpecs())
                .header("Authorization", token)
                .body(order)
                .post(POST_CREATE_ORDER);
    }

    public Response getUserOrders(String token) {
        return given()
                .spec(getSpecs())
                .header("Authorization", token)
                .get(GET_USER_ORDERS);
    }

    public String getToken(Response response, User user) {
        String token = response.then().extract().path("accessToken");
        user.setToken(token);
        return user.getToken();
    }
}