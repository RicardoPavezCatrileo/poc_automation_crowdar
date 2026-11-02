# Casos de prueba — SauceDemo y API Mercado Libre

**Aplicación web:** https://www.saucedemo.com/  
**Servicio REST:** `GET https://www.mercadolibre.com.ar/menu/departments`

## Alcance y criterio de diseño

La cobertura se diseñó con **partición de equivalencia** (credenciales válidas / inválidas / vacías / usuario bloqueado), **valores límite** (carrito con 0, 1 y N productos) y **transición de estados** (no autenticado → autenticado → sesión cerrada; producto "Add to cart" ↔ "Remove"). No pretende cubrir todas las combinaciones posibles, sino un conjunto representativo y justificable de las dos funcionalidades pedidas. Solo se incluyen comportamientos que SauceDemo realmente ofrece.

**Datos de prueba comunes** (usuarios públicos de demostración que muestra la propia página de login):

| Dato | Valor |
|---|---|
| Usuario estándar | `standard_user` |
| Usuario bloqueado | `locked_out_user` |
| Contraseña válida | `secret_sauce` |
| Contraseña inválida | `wrong_password` |

**Leyenda — Estado de automatización:** `Automatizado` (implementado en `src/test/java`), `Manual` (documentado, no automatizado en este challenge).

---

## 1. Inicio de sesión

**Precondición general:** navegador abierto en `https://www.saucedemo.com/` sin sesión iniciada.

| ID | Título | Objetivo | Precondiciones | Datos | Pasos | Resultado esperado | Tipo | Automatización |
|---|---|---|---|---|---|---|---|---|
| **LOGIN-001** | Inicio de sesión exitoso | Verificar que un usuario válido accede al inventario | General | `standard_user` / `secret_sauce` | 1. Ingresar usuario<br>2. Ingresar contraseña<br>3. Clic en **Login** | Redirige a `/inventory.html`; se muestra el título **Products** y la lista de productos | Funcional positivo | **Automatizado** (`LOGIN_001_InicioSesionExitosoTest`) |
| **LOGIN-002** | Contraseña incorrecta | Verificar el rechazo de una contraseña inválida | General | `standard_user` / `wrong_password` | 1. Ingresar usuario válido<br>2. Ingresar contraseña incorrecta<br>3. Clic en **Login** | Mensaje `Epic sadface: Username and password do not match any user in this service`; permanece en el login; no accede al inventario | Funcional negativo | **Automatizado** (`LOGIN_002_ContrasenaIncorrectaTest`) |
| LOGIN-003 | Usuario inexistente | Verificar el rechazo de un usuario no registrado | General | `usuario_inexistente` / `secret_sauce` | 1. Ingresar usuario inexistente<br>2. Ingresar contraseña<br>3. Clic en **Login** | Mismo mensaje que LOGIN-002 (no revela si el usuario existe) | Funcional negativo | Manual |
| LOGIN-004 | Usuario vacío | Validar campo obligatorio "Username" | General | vacío / `secret_sauce` | 1. Dejar usuario vacío<br>2. Ingresar contraseña<br>3. Clic en **Login** | Mensaje `Epic sadface: Username is required`; campos marcados con ícono de error | Negativo / validación | Manual |
| LOGIN-005 | Contraseña vacía | Validar campo obligatorio "Password" | General | `standard_user` / vacío | 1. Ingresar usuario<br>2. Dejar contraseña vacía<br>3. Clic en **Login** | Mensaje `Epic sadface: Password is required` | Negativo / validación | Manual |
| LOGIN-006 | Ambos campos vacíos | Verificar la prioridad de validación | General | vacío / vacío | 1. Clic en **Login** sin datos | Mensaje `Epic sadface: Username is required` (se valida primero el usuario) | Negativo / límite | Manual |
| LOGIN-007 | Usuario bloqueado | Verificar que un usuario bloqueado no accede | General | `locked_out_user` / `secret_sauce` | 1. Ingresar credenciales del usuario bloqueado<br>2. Clic en **Login** | Mensaje `Epic sadface: Sorry, this user has been locked out.`; no accede al inventario | Funcional negativo | Manual |
| LOGIN-008 | Usuario sensible a mayúsculas | Verificar que el usuario distingue mayúsculas/minúsculas | General | `Standard_User` / `secret_sauce` | 1. Ingresar usuario con mayúsculas<br>2. Clic en **Login** | Mensaje de credenciales inválidas (igual a LOGIN-002) | Negativo / límite | Manual |
| LOGIN-009 | Cerrar mensaje de error | Verificar que el mensaje de error puede descartarse | Haber provocado un error (p. ej. LOGIN-002) | — | 1. Clic en el botón **X** del mensaje | El mensaje y los íconos de error desaparecen; los campos siguen editables | UI / transición | Manual |
| LOGIN-010 | Acceso directo sin sesión | Verificar que el inventario está protegido | Sin sesión | URL `https://www.saucedemo.com/inventory.html` | 1. Navegar directo a la URL | Vuelve al login con mensaje `Epic sadface: You can only access '/inventory.html' when you are logged in.` | Seguridad / sesión | Manual |
| LOGIN-011 | Cierre de sesión | Verificar el logout y que la sesión no persiste | Sesión iniciada con `standard_user` | — | 1. Abrir el menú lateral<br>2. Clic en **Logout**<br>3. Usar **Atrás** del navegador | Paso 2: vuelve al login. Paso 3: no muestra el inventario; aparece el mensaje de acceso restringido de LOGIN-010 | Sesión / transición | Manual |

## 2. Carrito de compras

**Precondición general:** sesión iniciada con `standard_user` y carrito vacío (si no, usar **Reset App State** del menú lateral).

| ID | Título | Objetivo | Precondiciones | Datos | Pasos | Resultado esperado | Tipo | Automatización |
|---|---|---|---|---|---|---|---|---|
| **CART-001** | Agregar un producto | Verificar el agregado de un producto desde el inventario | General | Sauce Labs Backpack | 1. Clic en **Add to cart** del producto<br>2. Abrir el carrito | El botón cambia a **Remove**; el contador muestra **1**; el carrito contiene "Sauce Labs Backpack" | Funcional positivo | **Automatizado** (`CART_001_AgregarUnProductoTest`) |
| **CART-002** | Agregar dos productos distintos | Verificar el agregado múltiple | General | Sauce Labs Backpack, Sauce Labs Bike Light | 1. Agregar ambos productos<br>2. Abrir el carrito | El contador muestra **2**; el carrito contiene exactamente ambos productos | Funcional positivo | **Automatizado** (`CART_002_AgregarDosProductosTest`) |
| **CART-003** | **Fallo intencional** (demostrativo) | Demostrar el reporte de fallos y la captura de evidencia | General | Sauce Labs Backpack | 1. Agregar un producto<br>2. Verificar el contador exigiendo **2** | **La prueba debe FALLAR** (`expected [2] but found [1]`) y generar una captura en `target/screenshots/`. El comportamiento real de la app (contador = 1) es correcto | Demostración de evidencia | **Automatizado** (`CART_003_FalloIntencionalTest`, grupo `intentional-failure`) |
| CART-004 | Precio en el carrito | Verificar que el precio del carrito coincide con el inventario | General | Sauce Labs Backpack ($29.99) | 1. Anotar el precio en el inventario<br>2. Agregar el producto<br>3. Abrir el carrito | Precio **$29.99** y cantidad **1** en el carrito | Funcional positivo | Manual |
| CART-005 | Agregar desde el detalle | Verificar el agregado desde la página de detalle | General | Sauce Labs Bike Light | 1. Clic en el nombre del producto<br>2. En el detalle, clic en **Add to cart**<br>3. Volver al inventario | En el detalle el botón cambia a **Remove**; el contador muestra 1; en el inventario el botón del producto también figura como **Remove** | Funcional positivo | Manual |
| CART-006 | Eliminar desde el carrito | Verificar la eliminación de un producto | Backpack y Bike Light en el carrito | — | 1. Abrir el carrito<br>2. Clic en **Remove** de Backpack | Backpack desaparece de la lista; el contador baja a **1** | Funcional positivo | Manual |
| CART-007 | Eliminar desde el inventario | Verificar la transición Remove → Add to cart | Backpack en el carrito | — | 1. En el inventario, clic en **Remove** de Backpack | El botón vuelve a **Add to cart**; el contador desaparece | Transición de estado | Manual |
| CART-008 | Carrito vacío | Verificar el estado vacío (límite 0) | General | — | 1. Abrir el carrito sin productos | Lista vacía; sin contador en el ícono; botones **Continue Shopping** y **Checkout** visibles | Límite | Manual |
| CART-009 | Persistencia al navegar | Verificar que el carrito se mantiene en la navegación | Backpack en el carrito | — | 1. Abrir el detalle de otro producto<br>2. Volver con **Back to products**<br>3. Abrir el carrito y pulsar **Continue Shopping** | El contador sigue en **1** en todas las pantallas y el producto permanece en el carrito | Persistencia | Manual |
| CART-010 | Sin duplicados | Verificar que la interacción normal no duplica productos | General | Sauce Labs Backpack | 1. Agregar Backpack<br>2. Intentar agregarlo otra vez desde el inventario y desde el detalle | Tras el primer agregado el botón solo ofrece **Remove**; el carrito contiene una única unidad y el contador sigue en **1** | Negativo / límite | Manual |
| CART-011 | Agregar todos los productos | Verificar el límite superior del catálogo | General | Los 6 productos del inventario | 1. Clic en **Add to cart** en cada producto<br>2. Abrir el carrito | Contador **6**; el carrito lista los 6 productos | Límite | Manual |
| CART-012 | Reset App State | Verificar que el reinicio vacía el carrito | Productos en el carrito | — | 1. Abrir el menú lateral<br>2. Clic en **Reset App State** | El contador desaparece y el carrito queda vacío | Transición de estado | Manual |

## 3. API — Mercado Libre

| ID | Título | Objetivo | Precondiciones | Datos | Pasos | Resultado esperado | Tipo | Automatización |
|---|---|---|---|---|---|---|---|---|
| **API-001** | Validar departamentos de Mercado Libre | Verificar que el servicio responde y devuelve departamentos | Acceso a internet; el endpoint no debe estar bloqueado por la CDN | `GET https://www.mercadolibre.com.ar/menu/departments` | 1. Enviar el GET sin cabeceras especiales<br>2. Revisar estado, Content-Type y cuerpo | HTTP **200**; cuerpo JSON no vacío; lista `departments` no vacía; cada departamento con `name` y una lista `categories` no vacía cuyos elementos tienen `id` y `name` | API / contrato | **Automatizado** (`API_001_DepartamentosMercadoLibreTest`) |

### Evidencia real del endpoint (02/11/2026)

El endpoint responde distinto según el tipo de cliente:

| Cliente | Respuesta |
|---|---|
| Navegador web | **HTTP 200, JSON**: `departments` (p. ej. "Tecnología", con categorías como `MLA1051` "Celulares y Teléfonos", `MLA1648` "Computación"), además de `landings`, `more_categories` y `high_priority` |
| Cliente HTTP automatizado (REST Assured, curl), con o sin `Accept: application/json` | **HTTP 403, `text/html`**, `server: CloudFront`, `x-cache: Error from cloudfront`, página *"Hubo un error accediendo a esta pagina..."* |

La CDN de Mercado Libre (CloudFront) rechaza los clientes que no son navegadores antes de que la solicitud llegue al servicio.

**Tratamiento en la automatización:**

- Si el servicio responde 200, la prueba valida la estructura observada en la respuesta real del navegador. No exige `name` en `children_categories`, porque la respuesta real incluye hijos sin ese campo.
- Si se detecta el bloqueo de CloudFront, la prueba queda **SKIPPED** y registra en el reporte el estado, las cabeceras y el mensaje de la página, que extrae con Jsoup. No se marca como PASS ni se evade el bloqueo haciéndose pasar por un navegador (User-Agent falso).
- Cualquier otra respuesta hace **fallar** la prueba.
