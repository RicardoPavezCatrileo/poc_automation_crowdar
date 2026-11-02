package com.crowdar.automation.tests.cart;

import com.crowdar.automation.base.BaseTest;
import com.crowdar.automation.pages.CartPage;
import com.crowdar.automation.pages.InventoryPage;
import org.testng.annotations.Test;

import java.util.List;

import static com.crowdar.automation.utils.TestData.BACKPACK;
import static com.crowdar.automation.utils.TestData.BIKE_LIGHT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;

/** CART-002 — Agregar dos productos distintos al carrito (ver docs/casos_de_prueba.md). */
public class CART_002_AgregarDosProductosTest extends BaseTest {

    @Test(description = "CART-002 - Agregar dos productos distintos al carrito",
            groups = {"ui", "cart"})
    public void CART_002_agregarDosProductos() {
        InventoryPage inventory = loginAsStandardUser()
                .addProductToCart(BACKPACK)
                .addProductToCart(BIKE_LIGHT);

        assertEquals(inventory.getCartCount(), 2, "Contador del carrito");

        CartPage cart = inventory.openCart();
        assertEqualsNoOrder(cart.getProductNames(), List.of(BACKPACK, BIKE_LIGHT), "Productos en el carrito");
    }
}
