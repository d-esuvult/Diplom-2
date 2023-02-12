import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class GetUserOrderTest {
    User user;
    UserClient userClient;
    String token;
    Response response;

    @Before
    public void setUp() {
        user = UserBuilder.createRandomUser();
        userClient = new UserClient();

        response = userClient.createNewUser(user);
        token = userClient.getToken(response, user);
    }

    @Test
    @Description("Проверить, что можно получить заказы конкретного пользователя, если пользователь авторизован")
    public void checkThatCanGetUserOrdersAuth() {
        response = userClient.getUserOrders(token);

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_OK, statusCode);
        assertTrue(success);
    }

    @Test
    @Description("Проверить, что нельзя получить заказы конкретного пользователя без авторизации")
    public void checkThatCanNotGetUserOrdersNoAuth() {
        response = userClient.getUserOrders("");

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_UNAUTHORIZED, statusCode);
        assertFalse(success);
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(token);
    }
}