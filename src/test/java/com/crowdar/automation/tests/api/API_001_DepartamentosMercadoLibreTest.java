package com.crowdar.automation.tests.api;

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
 * Respuestas reales observadas el 02/11/2026:
 * - Desde un navegador: HTTP 200 con JSON {"departments":[{"name","categories":[{"id","name",
 *   "permalink","children_categories"}]}], "landings", "more_categories", "high_priority"}.
 * - Desde clientes HTTP automatizados (REST Assured, curl), con o sin "Accept: application/json":
 *   HTTP 403 text/html de CloudFront (x-cache "Error from cloudfront") con la página
 *   "Hubo un error accediendo a esta pagina...". La CDN filtra clientes no-navegador.
 *
 * Si se detecta ese bloqueo, la prueba queda SKIPPED con la evidencia en el reporte
 * (no se marca como PASS ni se intenta evadir el bloqueo falsificando un navegador).
 * Si el servicio responde 200, se validan los departamentos y sus categorías.
 */
public class API_001_DepartamentosMercadoLibreTest {

    @Test(description = "API-001 - Validar departamentos de Mercado Libre (GET /menu/departments)",
            groups = {"api"})
    public void API_001_validarDepartamentosMercadoLibre() {
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
     * Valida la estructura observada en la respuesta real: lista "departments" no vacía,
     * cada departamento con "name" y una lista "categories" no vacía cuyos elementos tienen "id" y "name".
     * No se exige "name" en "children_categories" porque la respuesta real incluye hijos sin ese campo.
     */
    @SuppressWarnings("unchecked")
    private void assertJsonDepartments(JsonPath json) {
        List<Map<String, Object>> departments = json.getList("departments");
        assertTrue(departments != null && !departments.isEmpty(), "Debería existir una lista 'departments' no vacía");

        for (Map<String, Object> department : departments) {
            String departmentName = String.valueOf(department.get("name"));
            assertTrue(department.get("name") != null && !departmentName.isBlank(),
                    "Cada departamento debería tener nombre: " + department);

            List<Map<String, Object>> categories = (List<Map<String, Object>>) department.get("categories");
            assertTrue(categories != null && !categories.isEmpty(),
                    "El departamento '" + departmentName + "' debería tener categorías");
            for (Map<String, Object> category : categories) {
                assertTrue(category.get("id") != null && category.get("name") != null,
                        "Cada categoría de '" + departmentName + "' debería tener id y name: " + category);
            }
            Reporter.log("Departamento '" + departmentName + "': " + categories.size() + " categorías", true);
        }
    }

    private boolean isCdnBlock(Response response) {
        String server = response.getHeader("Server");
        return response.getStatusCode() == 403 && server != null && server.contains("CloudFront");
    }
}
