package com.crowdar.automation.tests.cart;

import com.crowdar.automation.base.BaseTest;
import com.crowdar.automation.pages.InventoryPage;
import org.testng.annotations.Test;

import static com.crowdar.automation.utils.TestData.BACKPACK;
import static org.testng.Assert.assertEquals;

/**
 * CART-003 — PRUEBA DE FALLO CONTROLADO (demostrativa, ver docs/casos_de_prueba.md).
 * Funcionalmente el contador debe ser 1; la prueba exige 2 a propósito para demostrar
 * que el framework reporta el fallo y guarda la captura de pantalla.
 * Excluible con -Dgroups.exclude=intentional-failure.
 */
public class CART_003_FalloIntencionalTest extends BaseTest {

    @Test(description = "CART-003 - FALLO INTENCIONAL: exige contador 2 tras agregar 1 producto",
            groups = {"ui", "cart", "intentional-failure"})
    public void CART_003_falloIntencionalContadorIncorrecto() {
        InventoryPage inventory = loginAsStandardUser().addProductToCart(BACKPACK);

        int intentionallyWrongExpectedCount = 2;
        assertEquals(inventory.getCartCount(), intentionallyWrongExpectedCount,
                "[FALLO INTENCIONAL] Contador del carrito");
    }
}
