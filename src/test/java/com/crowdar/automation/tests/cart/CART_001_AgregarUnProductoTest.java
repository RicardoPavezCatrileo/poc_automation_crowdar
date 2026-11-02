package com.crowdar.automation.tests.cart;

import com.crowdar.automation.base.BaseTest;
import com.crowdar.automation.pages.CartPage;
import com.crowdar.automation.pages.InventoryPage;
import org.testng.annotations.Test;

import java.util.List;

import static com.crowdar.automation.utils.TestData.BACKPACK;
import static org.testng.Assert.assertEquals;

/** CART-001 — Agregar un producto al carrito (ver docs/casos_de_prueba.md). */
public class CART_001_AgregarUnProductoTest extends BaseTest {

    @Test(description = "CART-001 - Agregar un producto al carrito",
            groups = {"ui", "cart", "smoke"})
    public void CART_001_agregarUnProducto() {
        InventoryPage inventory = loginAsStandardUser().addProductToCart(BACKPACK);

        assertEquals(inventory.getCartCount(), 1, "Contador del carrito");

        CartPage cart = inventory.openCart();
        assertEquals(cart.getProductNames(), List.of(BACKPACK), "Productos en el carrito");
    }
}
