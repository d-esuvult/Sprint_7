import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class CreateCourierTests {

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

    @Step("Создать курьера")
    public Response postCreateCourier(Courier courier) {
        return courierClient.postCreateCourier(courier);
    }

    @Test
    @Description("Проверяет, что курьер может залогиниться, когда все поля заполнены правильно")
    public void checkCourierIsCreated() {
        postCreateCourier(courier);
        response = courierClient.postLogCourier(CourierCredentials.from(courier));
        int statusCode = response.getStatusCode();
        assertEquals(SC_OK, statusCode);
    }

    @Test
    @Description("Проверяет, что после создания курьера возращается код 201 и тело ответа, когда все поля заполнены правильно")
    public void checkCourierStatusIsCreated() {
        response = postCreateCourier(courier);
        int statusCode = response.getStatusCode();
        assertEquals(SC_CREATED, statusCode);
        String body = response.then().extract().body().asString();
        assertEquals("{\"ok\":true}", body);
    }

    @Test
    @Description("Проверяет, что нельзя создать двух одинаковых курьеров")
    public void checkCourierCanNotBeDuplicated() {
        postCreateCourier(courier);
        response = postCreateCourier(courier);
        int statusCode = response.getStatusCode();
        assertEquals(SC_CONFLICT, statusCode);
        String body = response.then().extract().body().asString();
        assertEquals("{\"code\":409,\"message\":\"Этот логин уже используется. Попробуйте другой.\"}", body);
    }

    @Test
    @Description("Проверяет, что нельзя создать курьера, если значение пароля отправить пустым")
    public void checkCourierCanNotBeCreatedWEmptyField() {
        courier.setPassword("");
        response = postCreateCourier(courier);
        int statusCode = response.getStatusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);
        String body = response.then().extract().body().asString();
        assertEquals("{\"code\":400,\"message\":\"Недостаточно данных для создания учетной записи\"}", body);
    }

    @Test
    @Description("Проверяет, что нельзя создать курьера, если в теле запроса полностью удалить поле пароля")
    public void checkCourierCanNotBeCreatedWOField() {
        response = postCreateCourier(wrongCourier);
        int statusCode = response.getStatusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);
        assertEquals("{\"code\":400,\"message\":\"Недостаточно данных для создания учетной записи\"}", response.then().extract().body().asString());
    }

    @After
    public void cleanUp() {
        deleteCourier(courier);
    }

    @Step("Удалить курьера")
    public void deleteCourier(Courier courier) {
        response = courierClient.postLogCourier(CourierCredentials.from(courier));
        String id = response.jsonPath().getString("id");
        courierClient.deleteCourier(id);
    }
}
