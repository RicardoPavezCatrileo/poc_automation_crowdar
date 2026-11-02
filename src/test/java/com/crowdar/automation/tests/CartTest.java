package com.crowdar.automation.tests;

import com.crowdar.automation.base.BaseTest;
import com.crowdar.automation.pages.CartPage;
import com.crowdar.automation.pages.InventoryPage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;

public class CartTest extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    @Test(description = "CART-001 - Agregar un producto al carrito",
            groups = {"ui", "cart", "smoke"})
    public void cart001_addOneProduct() {
        InventoryPage inventory = loginAsStandardUser().addProductToCart(BACKPACK);

        assertEquals(inventory.getCartCount(), 1, "Contador del carrito");

        CartPage cart = inventory.openCart();
        assertEquals(cart.getProductNames(), List.of(BACKPACK), "Productos en el carrito");
    }

    @Test(description = "CART-002 - Agregar dos productos distintos al carrito",
            groups = {"ui", "cart"})
    public void cart002_addTwoProducts() {
        InventoryPage inventory = loginAsStandardUser()
                .addProductToCart(BACKPACK)
                .addProductToCart(BIKE_LIGHT);

        assertEquals(inventory.getCartCount(), 2, "Contador del carrito");

        CartPage cart = inventory.openCart();
        assertEqualsNoOrder(cart.getProductNames(), List.of(BACKPACK, BIKE_LIGHT), "Productos en el carrito");
    }

    /**
     * PRUEBA DE FALLO CONTROLADO (demostrativa).
     * Funcionalmente el contador debe ser 1; la prueba exige 2 a propósito para demostrar
     * que el framework reporta el fallo y guarda la captura de pantalla.
     * Excluible con -Dgroups.exclude=intentional-failure.
     */
    @Test(description = "CART-003 - FALLO INTENCIONAL: exige contador 2 tras agregar 1 producto",
            groups = {"ui", "cart", "intentional-failure"})
    public void cart003_intentionalFailure_wrongCartCount() {
        InventoryPage inventory = loginAsStandardUser().addProductToCart(BACKPACK);

        int intentionallyWrongExpectedCount = 2;
        assertEquals(inventory.getCartCount(), intentionallyWrongExpectedCount,
                "[FALLO INTENCIONAL] Contador del carrito");
    }
}
