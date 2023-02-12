import com.github.javafaker.Faker;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LogUserTest {
    User user;
    UserClient userClient;
    Response response;
    String token;
    Faker faker;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserBuilder.createRandomUser();
        response = userClient.createNewUser(user);
        token = userClient.getToken(response, user);
        faker = new Faker();
    }

    @Test
    @Description("Проверить, что существующий пользователь может авторизоваться")
    public void checkThatLogSuccessfulWithValidData() {
        response = userClient.logUser(user, token);

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_OK, statusCode);
        assertEquals(true, success);
    }

    @Test
    @Description("Проверить, что мользователь не может авторизоваться с неправильным логином")
    public void checkThatLogUnsuccessfulWithInvalidEmail() {
        user.setEmail(faker.internet().emailAddress());
        response = userClient.logUser(user, token);

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_UNAUTHORIZED, statusCode);
        assertFalse(success);
    }

    @Test
    @Description("Проверить, что пользователь не может авторизоваться с неправильным паролем")
    public void checkThatLogUnsuccessfulWithInvalidPassword() {
        user.setPassword(faker.internet().password(6, 7));
        user.setEmail(faker.internet().emailAddress());
        response = userClient.logUser(user, token);

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