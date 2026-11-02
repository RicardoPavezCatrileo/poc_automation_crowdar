package com.crowdar.automation.tests;

import com.crowdar.automation.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * API-001. No usa WebDriver.
 *
 * Inspección real del endpoint (02/11/2026, desde Chile, sin cabeceras especiales):
 * HTTP 403, Content-Type text/html, Server CloudFront, x-cache "Error from cloudfront",
 * cuerpo HTML con el texto "Hubo un error accediendo a esta pagina...".
 * Es un bloqueo de la CDN previo a la aplicación, no una respuesta del servicio.
 *
 * Por eso: si se detecta ese bloqueo la prueba queda SKIPPED con la evidencia en el reporte
 * (no se oculta como PASS ni se intenta evadir el bloqueo). Si el servicio responde 200,
 * se validan los departamentos.
 */
public class MercadoLibreApiTest {

    @Test(description = "API-001 - Validar departamentos de Mercado Libre (GET /menu/departments)",
            groups = {"api"})
    public void api001_departmentsAreReturned() {
        String url = ConfigReader.get("api.departments.url");

        Response response = RestAssured.given()
                .when().get(url)
                .then().extract().response();

        int status = response.getStatusCode();
        String contentType = response.getContentType();
        String body = response.asString();
        Reporter.log("GET " + url + " -> HTTP " + status + " | Content-Type: " + contentType
                + " | Server: " + response.getHeader("Server") + " | x-cache: " + response.getHeader("x-cache")
                + " | bytes: " + body.length(), true);

        if (isCdnBlock(response)) {
            String pageMessage = Jsoup.parse(body).select("h4, h1, title").text();
            String evidence = "Endpoint bloqueado por la CDN (HTTP " + status + ", x-cache: "
                    + response.getHeader("x-cache") + "). Mensaje de la página: '" + pageMessage + "'";
            Reporter.log(evidence, true);
            throw new SkipException(evidence);
        }

        assertEquals(status, 200, "Código HTTP");
        assertFalse(body.isBlank(), "El cuerpo de la respuesta no debería estar vacío");

        if (contentType != null && contentType.contains("json")) {
            assertJsonDepartments(response.jsonPath());
        } else {
            fail("Formato no esperado (" + contentType + "); revisar el contrato del servicio. Inicio del cuerpo: "
                    + body.substring(0, Math.min(300, body.length())));
        }
    }

    /**
     * Estructura esperada: objeto con lista "departments" cuyos elementos tienen "name".
     * Nota: no pudo verificarse contra una respuesta 200 real por el bloqueo documentado arriba.
     */
    private void assertJsonDepartments(JsonPath json) {
        List<Map<String, Object>> departments = json.getList("departments");
        assertTrue(departments != null && !departments.isEmpty(), "Debería existir una lista 'departments' no vacía");
        for (Map<String, Object> department : departments) {
            Object name = department.get("name");
            assertTrue(name != null && !name.toString().isBlank(), "Cada departamento debería tener nombre: " + department);
        }
        Reporter.log("Departamentos recibidos: " + departments.size(), true);
    }

    private boolean isCdnBlock(Response response) {
        String server = response.getHeader("Server");
        return response.getStatusCode() == 403 && server != null && server.contains("CloudFront");
    }
}
