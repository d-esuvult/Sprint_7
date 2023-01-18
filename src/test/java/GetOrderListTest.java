
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

public class GetOrderListTest {

    private OrderClient clientOrder;
    private Response response;

    @Before
    public void setUp() {
        clientOrder = new OrderClient();
    }

    @Test
    @Description("Проверяет, что запрос возвращает код 200, и в теле ответа есть массив с заказами")
    public void getOrderList(){
        response = clientOrder.getOrderList();
        int statusCode = response.getStatusCode();
        assertEquals(SC_OK, statusCode);
        response.then().assertThat().body(anything("orders"));
    }
}
