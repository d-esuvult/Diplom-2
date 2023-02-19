import com.github.javafaker.Faker;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateOrderTest {
    User user;
    UserClient client;
    Response response;
    String token;
    Order order;

    @Before
    public void setUp() {
        client = new UserClient();
        user = UserBuilder.createRandomUser();
        response = client.createNewUser(user);

        token = client.getToken(response, user);

        response = client.getIngredientsList();
        String list = response.then().extract().body().asString();
        String[] burger = AssembleIngredients.assembleIngredients(list);
        order = new Order(burger);
    }

    @Test
    @Description("Проверить, что можно создать заказ, если пользователь авторизован")
    public void checkThatCanCreateOrderWithAuth() {
        response = client.createOrder(order, token);

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_OK, statusCode);
        assertTrue(success);
    }

    @Test
    @Description("Проверить, что можно создать заказ без авторизации")
    public void checkThatCanCreateOrderNoAuth() {
        response = client.createOrder(order,"");

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_OK, statusCode);
        assertTrue(success);
    }

    @Test
    @Description("Проверить, что нельзя создать заказ без ингредиентов")
    public void checkThatCanNotCreateOrderNoIngredients() {
        Order noOrder = new Order(new String[0]);
        response = client.createOrder(noOrder, token);

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_BAD_REQUEST, statusCode);
        assertFalse(success);
    }

    @Test
    @Description("Проверить, что нельзя создать заказ, если указан неверный хеш ингредиента")
    public void checkThatCanNotCreateOrderWithWrongIdHash() {
        Faker faker = new Faker();
        String[] hashCode = new String[]{faker.crypto().md5()};
        order = new Order(hashCode);
        response = client.createOrder(order, token);

        int statusCode = response.statusCode();

        assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }

    @After
    public void cleanUp() {
        client.deleteUser(token);
    }
}