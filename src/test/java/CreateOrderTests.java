import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Locale;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CreateOrderTests {
    private String firstName;
    private String lastName;
    private String address;
    private int metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;
    private Order order;
    private OrderClient clientOrder;
    private Response response;

    public CreateOrderTests(String firstName, String lastName, String address, int metroStation, String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] fillOrder() {
        Faker faker = new Faker(new Locale("ru"));
        return new Object[][]{
                {faker.name().firstName(), faker.name().lastName(), faker.address().fullAddress(), faker.number().randomDigit(), faker.phoneNumber().phoneNumber(), faker.number().randomDigit(), "2022", faker.overwatch().quote(), new String[]{""}},
                {faker.name().firstName(), faker.name().lastName(), faker.address().fullAddress(), faker.number().randomDigit(), faker.phoneNumber().phoneNumber(), faker.number().randomDigit(), "2022", faker.overwatch().quote(), new String[]{"BLACK"}},
                {faker.name().firstName(), faker.name().lastName(), faker.address().fullAddress(), faker.number().randomDigit(), faker.phoneNumber().phoneNumber(), faker.number().randomDigit(), "2022", faker.overwatch().quote(), new String[]{"BLACK", "GREY"}},
        };
    }


    @Before
    public void setUp() {
        order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        clientOrder = new OrderClient();
    }

    @Test
    @Description("Проверяет, что заказ создан, и в теле ответа есть поле track")
    public void checkOrderStatusIsCreated() {
        response = clientOrder.postCreateOrder(order);
        int statusCode = response.getStatusCode();
        assertEquals(SC_CREATED, statusCode);
        response.then().assertThat().body(anything("track"));
    }

    @Test
    @Description("Проверяет, что в заказ можно передать массив color, и он отображается в теле ответа")
    public void checkOrderContent() {
        response = clientOrder.postCreateOrder(order);
        int id = response.jsonPath().getInt("track");
        order.setTrack(id);
        response = clientOrder.getOrderByTrack(id);
        response.then().assertThat().body("order.color", contains(color));
    }

    @After
    @Description("После удаления проверяет, что по треку нельзя найти заказ")
    public void cleanUp() {
        response = clientOrder.cancelOrder(order.getTrack());
        assertEquals(SC_NOT_FOUND, clientOrder.getOrderByTrack(order.getTrack()).getStatusCode());
    }

}

