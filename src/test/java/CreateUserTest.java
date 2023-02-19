import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class CreateUserTest {
    UserClient userClient;
    User user;
    Response response;

    @Before
    public void setUp() {
        user = UserBuilder.createRandomUser();
        userClient = new UserClient();
    }

    @Test
    @Description("Проверить, что можно создать уникального пользователя")
    public void checkThatUniqueUserIsCreated() {
        response = userClient.createNewUser(user);

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals("Статус должен быть ОК", SC_OK, statusCode);
        assertTrue(success);
    }

    @Test
    @Description("Проверить, что нельзя создать пользователя с дублирующими данными")
    public void checkThatCanNotDuplicateUser() {
        response = userClient.createNewUser(user);
        Response checkDuplicate = userClient.createNewUser(user);

        int statusCode = checkDuplicate.statusCode();
        Boolean success = checkDuplicate.then().extract().path("success");

        assertEquals(SC_FORBIDDEN, statusCode);
        assertFalse(success);
    }

    @Test
    @Description("Проверить, что нельзя создать пользователя, если обязательное поле Email не заполнено")
    public void checkThatCanNotCreateUserIfEmailFieldIsEmpty() {
        user.setEmail("");
        response = userClient.createNewUser(user);

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_FORBIDDEN, statusCode);
        assertFalse(success);
    }

    @Test
    @Description("Проверить, что нельзя создать пользователя, если обязательное поле Password не заполнено")
    public void checkThatCanNotCreateUserIfPasswordFieldIsEmpty() {
        user.setPassword("");
        response = userClient.createNewUser(user);

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_FORBIDDEN, statusCode);
        assertFalse(success);
    }

    @After
    public void cleanUp() throws IllegalArgumentException {
        try {
            userClient.deleteUser(userClient.getToken(response, user));
        } catch (IllegalArgumentException ignored) {}
    }
}