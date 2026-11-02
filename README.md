# poc_automation_crowdar

Challenge técnico de QA Automation para Crowdar. Incluye:

- **UI:** pruebas de inicio de sesión y carrito de compras en [SauceDemo](https://www.saucedemo.com/), con Selenium WebDriver, TestNG y Page Object Model.
- **API:** validación del servicio `GET https://www.mercadolibre.com.ar/menu/departments` con REST Assured.
- **Evidencias:** capturas de pantalla automáticas cuando falla una prueba UI, y reportes HTML/XML de TestNG y Surefire.

## Tecnologías

| Herramienta | Versión |
|---|---|
| Java | 21 LTS |
| Maven | 3.9+ |
| Selenium WebDriver (con Selenium Manager) | 4.52.0 |
| TestNG | 7.12.0 |
| REST Assured | 6.3.0 |
| Jsoup | 1.23.2 |
| Maven Surefire | 3.5.6 |

> Se usa Surefire 3.5.x porque la serie 3.6.x ejecuta TestNG sobre JUnit Platform y ya no admite `testng.xml`.

## Requisitos previos

- JDK 21 y Maven 3.9 o superior en el `PATH`.
- Google Chrome y/o Mozilla Firefox instalados.
- Acceso a internet. No hace falta descargar ChromeDriver ni GeckoDriver: los resuelve **Selenium Manager**.

## Instalación

```powershell
git clone https://github.com/RicardoPavezCatrileo/poc_automation_crowdar.git
cd poc_automation_crowdar
mvn clean test-compile
```

## Ejecución

| Objetivo | Comando | Resultado esperado |
|---|---|---|
| Suite completa en Chrome (predeterminado) | `mvn clean test -Dbrowser=chrome` | 4 PASS, 1 FAIL intencional, API según disponibilidad → **BUILD FAILURE** |
| Suite completa en Firefox | `mvn clean test -Dbrowser=firefox` | Igual que en Chrome |
| Sin el fallo intencional | `mvn clean test -Dbrowser=chrome -Dgroups.exclude=intentional-failure` | **BUILD SUCCESS** |
| Modo headless (opcional) | agregar `-Dheadless=true` | — |

Cualquier clave de `src/test/resources/config.properties` puede sobrescribirse con `-D<clave>=<valor>`.

La ejecución predeterminada **termina con error a propósito** por CART-003, aunque los reportes se generan igual.

## Reportes y evidencias

| Qué | Dónde |
|---|---|
| Reporte HTML de TestNG | `target/surefire-reports/index.html` |
| Reporte resumido (emailable) | `target/surefire-reports/emailable-report.html` |
| Resultados XML | `target/surefire-reports/testng-results.xml`, `target/surefire-reports/TEST-TestSuite.xml` |
| Capturas de pantalla | `target/screenshots/<Clase>.<método>_<navegador>_<yyyyMMdd_HHmmss_SSS>.png` |

Los reportes muestran el nombre de cada prueba, su resultado (PASS/FAIL/SKIP), el mensaje de error, la duración y el navegador usado (`Navegador: chrome`). Cuando una prueba UI falla, la ruta absoluta de la captura aparece en la consola y en el *Reporter output* del reporte HTML, y también queda guardada como atributo `screenshot` del resultado.

## Cobertura automatizada

| ID | Prueba | Clase (un archivo .java por caso) | Resultado esperado |
|---|---|---|---|
| LOGIN-001 | Inicio de sesión exitoso | `LOGIN_001_InicioSesionExitosoTest` | PASS |
| LOGIN-002 | Contraseña incorrecta | `LOGIN_002_ContrasenaIncorrectaTest` | PASS |
| CART-001 | Agregar un producto | `CART_001_AgregarUnProductoTest` | PASS |
| CART-002 | Agregar dos productos | `CART_002_AgregarDosProductosTest` | PASS |
| CART-003 | Fallo intencional | `CART_003_FalloIntencionalTest` | **FAIL** (deliberado) |
| API-001 | Departamentos de Mercado Libre | `API_001_DepartamentosMercadoLibreTest` | PASS, o SKIP si la CDN bloquea |

La matriz completa de casos manuales (login, carrito y API) está en [`docs/casos_de_prueba.md`](docs/casos_de_prueba.md).

## Fallo deliberado (CART-003)

El challenge pide demostrar cómo el framework reporta un fallo. CART-003 agrega **un** producto y, con una aserción real de TestNG, exige que el contador muestre **2**. Falla con:

```
java.lang.AssertionError: [FALLO INTENCIONAL] Contador del carrito expected [2] but found [1]
```

El listener `ScreenshotListener` captura la pantalla antes de cerrar el navegador. La aplicación funciona bien: la expectativa incorrecta está en la prueba. Esta prueba pertenece al grupo TestNG `intentional-failure` y puede excluirse con `-Dgroups.exclude=intentional-failure`.

## Prueba API (API-001)

Envía un `GET` a `https://www.mercadolibre.com.ar/menu/departments` sin usar WebDriver.

- **Respuesta 200 con JSON:** valida que exista una lista `departments` no vacía, que cada departamento tenga `name` y una lista `categories` no vacía, y que cada categoría tenga `id` y `name`. Es la estructura que devuelve el endpoint al consultarlo desde un navegador.
- **Bloqueo de la CDN:** desde un navegador el endpoint devuelve 200 con JSON, pero a los clientes HTTP automatizados (REST Assured, curl) CloudFront les responde **HTTP 403 `text/html`** (`x-cache: Error from cloudfront`). En ese caso la prueba queda **SKIPPED** y deja la evidencia en el reporte. No se marca como PASS ni se evade el bloqueo haciéndose pasar por un navegador.
- **Cualquier otra respuesta:** la prueba falla.

Detalle y limitaciones en [`docs/casos_de_prueba.md`](docs/casos_de_prueba.md#3-api--mercado-libre).

## Estructura

```text
src/test/java/com/crowdar/automation/
├── base/BaseTest.java              # Ciclo de vida del navegador por prueba
├── factory/DriverFactory.java      # Chrome/Firefox con Selenium Manager (ThreadLocal)
├── pages/                          # Page Objects: BasePage, LoginPage, InventoryPage, CartPage
├── tests/                          # Un caso por clase, nombrada con su ID: login/, cart/, api/
├── listeners/ScreenshotListener.java
└── utils/ConfigReader.java         # config.properties + overrides -D
```
