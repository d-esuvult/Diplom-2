import com.github.javafaker.Faker;

public class UserBuilder {

    public static User createRandomUser(){
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.leagueOfLegends().champion();
        return new User(email, password, name);
    }
}
