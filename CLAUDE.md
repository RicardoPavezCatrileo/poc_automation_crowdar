# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Proyecto

Challenge de QA Automation para Crowdar (`poc_automation_crowdar`). Usa Java 21, Maven, Selenium 4, TestNG y REST Assured, con Page Object Model. Prueba la UI de SauceDemo (login y carrito) y el endpoint `GET https://www.mercadolibre.com.ar/menu/departments`. Todo el código vive en `src/test` (no hay `src/main`).

## Comandos

```powershell
mvn clean test -Dbrowser=chrome                                         # suite completa; termina en BUILD FAILURE a propósito (CART-003)
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=chrome -Dgroups.exclude=intentional-failure    # suite verde
mvn clean test -Dheadless=true                                          # opcional
mvn test -Dtest=CART_001_AgregarUnProductoTest                          # una sola prueba (ignora testng.xml, sin listener)
```

## Puntos no evidentes

- **Surefire está fijado en 3.5.x** a propósito. La serie 3.6.x ejecuta TestNG sobre JUnit Platform y rechaza `suiteXmlFiles`. Si se actualiza, `testng.xml` deja de funcionar, y con él el registro del listener.
- `-Dgroups.exclude` es una propiedad del POM que se pasa a TestNG como `excludegroups` mediante `<properties>` de Surefire. `excludedGroups` de Surefire no sirve porque se ignora cuando hay `suiteXmlFiles`.
- `ScreenshotListener` se registra solo en `testng.xml`. Depende de que TestNG invoque `onTestFailure` **antes** de `@AfterMethod` (`BaseTest.tearDown`, que cierra el driver). Esto está verificado con TestNG 7.12. El listener obtiene el driver por `DriverFactory.getDriver()` (ThreadLocal); si es `null`, trata la prueba como no UI.
- `ConfigReader`: una propiedad de sistema `-Dclave` tiene prioridad sobre `config.properties`. `browser` y `headless` llegan desde el POM vía `systemPropertyVariables`.
- SauceDemo **reemplaza** el botón "Add to cart" por un elemento "Remove" nuevo. Las esperas deben volver a localizarlo; si reutilizan la referencia, obtienen `StaleElementReferenceException`. Ver `InventoryPage.addProductToCart`.
- Chrome se lanza con las preferencias del gestor de contraseñas desactivadas. Así se evita el diálogo de "contraseña filtrada", que bloquea la UI tras el login con `secret_sauce`.
- Las páginas no contienen aserciones: estas viven en `tests/`. Convención: **un caso por clase**, nombrada con el ID de `docs/casos_de_prueba.md` (`CART_001_AgregarUnProductoTest`, método `CART_001_agregarUnProducto`). Cada clase nueva debe añadirse a `testng.xml`.
- **API-001:** desde un navegador el endpoint devuelve 200 con JSON (`departments[].name`, `departments[].categories[].id/name`), pero a los clientes automatizados CloudFront les responde 403. En ese caso la prueba lanza `SkipException` con la evidencia. No evadas el bloqueo (User-Agent falso, etc.). La estructura validada viene de una respuesta real del navegador (02/11/2026); `children_categories` puede venir sin `name`.
- CART-003 (grupo `intentional-failure`) debe seguir fallando con una aserción real. No la "arregles".
- Al lanzar `mvn clean`, ningún proceso puede tener `target/` como directorio actual: en Windows eso bloquea el borrado.
