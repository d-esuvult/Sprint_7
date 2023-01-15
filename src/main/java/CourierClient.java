import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierClient extends Client {

    private static final String CREATE_COURIER = "/api/v1/courier";
    private static final String LOG_COURIER = "/api/v1/courier/login";
    private static final String DELETE_COURIER = "/api/v1/courier/";

    public Response postCreateCourier(Courier courier) {
        return given()
                .spec(getSpecs())
                .body(courier)
                .when()
                .post(CREATE_COURIER);
    }

    public Response postLogCourier(CourierCredentials credentials) {
        return given()
                .spec(getSpecs())
                .body(credentials)
                .when()
                .post(LOG_COURIER);
    }

    public Response deleteCourier(String id) {
        return given()
                .spec(getSpecs())
                .delete(DELETE_COURIER + id);
    }
}
