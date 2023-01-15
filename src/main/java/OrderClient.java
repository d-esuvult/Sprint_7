import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient extends Client {
    private static final String POST_ORDER = "/api/v1/orders";
    private static final String GET_ORDER = "/api/v1/orders/track?t=";
    private static final String CANCEL_ORDER = "/api/v1/orders/cancel?track=";
    private static final String GET_LIST = "/api/v1/orders";

    public Response postCreateOrder(Order order) {
        return given()
                .spec(getSpecs())
                .body(order)
                .when()
                .post(POST_ORDER);
    }

    public Response getOrderByTrack(int track) {
        return given()
                .spec(getSpecs())
                .get(GET_ORDER + track);
    }

    public Response cancelOrder(int track) {
        return given()
                .spec(getSpecs())
                .put(CANCEL_ORDER + track);
    }

    public Response getOrderList() {
        return given()
                .spec(getSpecs())
                .get(GET_LIST);
    }

}
