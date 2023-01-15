import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class LogCourierTests {

    private Courier courier;
    private CourierClient courierClient;
    private Courier wrongCourier;
    private Response response;

    @Before
    public void setUp() {
        courier = RandomCourierBuilder.createRandomCourier();
        courierClient = new CourierClient();
        wrongCourier = RandomCourierBuilder.getRandomName();
    }

    @Step("Создает курьера")
    public Response postCreateCourier(Courier courier) {
        return courierClient.postCreateCourier(courier);
    }

    @Step("Логин курьера")
    public Response postLogCourier(Courier courier) {
        return courierClient.postLogCourier(CourierCredentials.from(courier));
    }

    @Test
    @Description("Проверяет, что курьер может залогиниться")
    public void courierCanLog() {
        postCreateCourier(courier);
        response = postLogCourier(courier);
        assertEquals(SC_OK, response.getStatusCode());
    }

    @Test
    @Description("Проверяет, что в теле ответа есть значение id")
    public void courierReturnsId() {
        postCreateCourier(courier);
        response = postLogCourier(courier);
        int id = response.jsonPath().getInt("id");
        response.then().assertThat().body("id", equalTo(id));
    }

    @Test
    @Description("Проверяет, что курьер не может залогиниться с существующим логином и неправильным паролем")
    public void courierCanNotLogWithWrongPassword() {
        postCreateCourier(courier);
        Random random = new Random();
        courier.setPassword(String.valueOf(random.nextInt()));
        response = postLogCourier(courier);
        assertEquals(SC_NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Description("Проверяет, что курьер не может залогиниться с существующим паролем и неправильным логином")
    public void courierCanNotLogWithWrongUserName() {
        postCreateCourier(courier);
        Faker faker = new Faker();
        courier.setLogin(faker.name().username());
        response = postLogCourier(courier);
        assertEquals(SC_NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Description("Проверяет, что курьер не может залогиниться, если в теле запроса удалено поле пароля")
    public void courierCanNotLogWOField() {
        postCreateCourier(courier);
        response = postLogCourier(wrongCourier);
        assertEquals(SC_BAD_REQUEST, response.getStatusCode()); // Тут он зависает, потому что по факту там код 504
    }

    @Test
    @Description("Проверяет, что курьер не может залогиниться, если в теле запроса оставить пустым поле пароля")
    public void courierCanNotLogWEmptyField() {
        postCreateCourier(courier);
        courier.setPassword("");
        response = postLogCourier(courier);
        assertEquals(SC_BAD_REQUEST, response.getStatusCode());
    }

    @After
    public void cleanUp() {
        deleteCourier(courier);

    }

    @Step("Удаляет курьера")
    public void deleteCourier(Courier courier) {
        response = postLogCourier(courier);
        String id = response.jsonPath().getString("id");
        courierClient.deleteCourier(id);
    }
}
