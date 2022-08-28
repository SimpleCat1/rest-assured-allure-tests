package com.simbirsoft.tests;

import com.simbirsoft.data.AddWishApiData;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.simbirsoft.filters.CustomLogFilter.customLogFilter;
import static com.simbirsoft.page.ReqresApiMethods.checkValue;
import static com.simbirsoft.page.ReqresApiMethods.getQuantity;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

public class DemowebshopApiMethodTests extends TestBase {

    @Epic("Неавторизованный пользователь")
    @Feature("Добавление товара")
    @Story("Добавление товара api")
    @Owner("SimplePerson")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Добавление товара")
    @Description("Проверяем api добавления товара")
    @Test
    void addingProductToTheCart() {
        step("Добавляем товар", () -> {
            AddWishApiData data = new AddWishApiData();

            Response response = given()
                    .filter(customLogFilter().withCustomTemplates())
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .body(data.body)
                    .log().uri()
                    .log().body()
                    .when()
                    .post("/addproducttocart/details/72/1")
                    .then()
                    .log().body()
                    .body(matchesJsonSchemaInClasspath("shemas/GenerateSheme.json"))
                    .statusCode(200)
                    .extract()
                    .response();

            assertThat(response.path("success").toString()).isGreaterThanOrEqualTo("true");
            assertThat(response.path("message").toString()).isGreaterThanOrEqualTo("The product has been added to " +
                    "your <a href=\"/cart\">shopping cart</a>");
            assertThat(response.path("updatetopcartsectionhtml").toString()).isGreaterThanOrEqualTo("(1)");
        });
    }

    @Epic("Авторизованный пользователь")
    @Feature("Добавление товара")
    @Story("Добавление товара у одного пользователя с использованием куков")
    @Owner("SimplePerson")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Добавление товара по кукам")
    @Description("Проверяем api добавления товара и их увеличение от при каждом тесте")
    @CsvSource({
            "1, 1",
            "3, 2",
    })
    @ParameterizedTest(name = "{index} - {0} is a addingMultipleProductsToTheCart")
    void addingMultipleProductsToTheCart(int amountItem, int amountTest) {
        step("Количество товара увеличивается, после каждого теста, в корзине", () -> {
            AddWishApiData data = new AddWishApiData();
            int numberOfCycles = 0;

            do {
                given()
                        .filter(customLogFilter().withCustomTemplates())
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .cookie(cookie)
                        .body(data.body)
                        .log().uri()
                        .log().body()
                        .when()
                        .post("/addproducttocart/details/72/1");
                numberOfCycles += 1;
            } while (numberOfCycles < amountTest);
            Response response2 = given()
                    .filter(customLogFilter().withCustomTemplates())
                    .log().uri()
                    .log().body()
                    .when()
                    .cookie(cookie)
                    .post("/cart")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            assertThat(getQuantity(response2.getBody().print())).isEqualTo(amountItem);
        });
    }

    @Epic("Неавторизованный пользователь")
    @Feature("Проверка коризны")
    @Story("Корзина")
    @Owner("SimplePerson")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Проверка корзины")
    @Description("Проверяем api корзины")
    @Test
    void checkingTheShoppingCart() {
        step("Проверяем ,что в коризине нету товара", () -> {

            Response response1 = given()
                    .filter(new AllureRestAssured())
                    .log().uri()
                    .log().body()
                    .when()
                    .post("/cart")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            assertThat(checkValue(response1.getBody().print())).isEqualTo("");
        });
    }
}
