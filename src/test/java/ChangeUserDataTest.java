import com.github.javafaker.Faker;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class ChangeUserDataTest {
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
    @Description("Проверить, что можно изменить поле Email с авторизацией")
    public void checkThatUserCanChangeEmail() {
        user.setEmail(faker.internet().emailAddress());
        response = userClient.changeUserDataAuth("{\"email\":\"" + user.getEmail() + "\"}", token);

        int statusCode = response.statusCode();
        String email = response.then().extract().path("user.email");

        assertEquals(SC_OK, statusCode);
        assertEquals(user.getEmail(), email);
    }

    @Test
    @Description("Проверить, что можно изменить поле Name с авторизацией")
    public void checkThatUserCanChangeName() {
        user.setName(faker.leagueOfLegends().champion());
        response = userClient.changeUserDataAuth("{\"name\":\"" + user.getName() + "\"}", token);

        int statusCode = response.statusCode();
        String name = response.then().extract().path("user.name");

        assertEquals(SC_OK, statusCode);
        assertEquals(user.getName(), name);
    }

    @Test
    @Description("Проверить, что можно изменить поле Password с авторизацией")
    public void checkThatUserCanChangePassword() {
        user.setPassword(faker.internet().password());
        userClient.changeUserDataAuth("{\"password\":\"" + user.getPassword() + "\"}", token);
        response = userClient.logUser(user, token); // Здесь проверка через логин, потому что в теле ответа не приходит поле с паролем

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_OK, statusCode);
        assertTrue(success);
    }

    @Test
    @Description("Проверить, что нельзя изменить поле Email без авторизации")
    public void checkThatCanNotChangeEmailNoAuth() {
        user.setEmail(faker.internet().emailAddress());
        response = userClient.changeUserDataNoAuth("{\"email\":\"" + user.getEmail() + "\"}");

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_UNAUTHORIZED, statusCode);
        assertFalse(success);
    }

    @Test
    @Description("Проверить, что нельзя изменить поле Name без авторизации")
    public void checkThatCanNotChangeNameNoAuth() {
        user.setName(faker.leagueOfLegends().champion());
        response = userClient.changeUserDataNoAuth("{\"name\":\"" + user.getName() + "\"}");

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_UNAUTHORIZED, statusCode);
        assertFalse(success);
    }

    @Test
    @Description("Проверить, что нельзя изменить поле Password без авторизации")
    public void checkThatCanNotChangePasswordNoAuth() {
        user.setPassword(faker.internet().password());
        response = userClient.changeUserDataNoAuth("{\"password\":\"" + user.getPassword() + "\"}");

        int statusCode = response.statusCode();
        Boolean success = response.then().extract().path("success");

        assertEquals(SC_UNAUTHORIZED, statusCode);
        assertFalse(success);
    }

    @After
    public void cleanUp(){
        userClient.deleteUser(token);
    }
}
