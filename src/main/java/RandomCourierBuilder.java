import com.github.javafaker.Faker;

public class RandomCourierBuilder {

    public static Courier createRandomCourier() {
        Faker faker = new Faker();
        String name = faker.name().username();
        String password = faker.number().digits(4);
        String firstName = faker.overwatch().hero();
        return new Courier(name, password, firstName);
    }

    public static Courier getRandomName() {
        Faker faker = new Faker();
        String name = faker.overwatch().hero();
        return new Courier(name);
    }
}
