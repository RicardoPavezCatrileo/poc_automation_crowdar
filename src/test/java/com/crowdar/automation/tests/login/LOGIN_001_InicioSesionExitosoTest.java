package com.crowdar.automation.tests.login;

import com.crowdar.automation.base.BaseTest;
import com.crowdar.automation.pages.InventoryPage;
import com.crowdar.automation.utils.ConfigReader;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** LOGIN-001 — Inicio de sesión exitoso (ver docs/casos_de_prueba.md). */
public class LOGIN_001_InicioSesionExitosoTest extends BaseTest {

    @Test(description = "LOGIN-001 - Inicio de sesión exitoso con usuario estándar",
            groups = {"ui", "login", "smoke"})
    public void LOGIN_001_inicioSesionExitoso() {
        InventoryPage inventory = loginPage().loginAs(
                ConfigReader.get("standard.user"), ConfigReader.get("valid.password"));

        assertTrue(inventory.isLoaded(), "Debería mostrarse el inventario tras un login válido");
        assertEquals(inventory.getTitle(), "Products", "Título de la página de inventario");
    }
}
