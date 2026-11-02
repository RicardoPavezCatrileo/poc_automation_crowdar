package com.crowdar.automation.tests;

import com.crowdar.automation.base.BaseTest;
import com.crowdar.automation.pages.InventoryPage;
import com.crowdar.automation.pages.LoginPage;
import com.crowdar.automation.utils.ConfigReader;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LoginTest extends BaseTest {

    private static final String WRONG_CREDENTIALS_ERROR =
            "Epic sadface: Username and password do not match any user in this service";

    @Test(description = "LOGIN-001 - Inicio de sesión exitoso con usuario estándar",
            groups = {"ui", "login", "smoke"})
    public void login001_successfulLogin() {
        InventoryPage inventory = loginPage().loginAs(
                ConfigReader.get("standard.user"), ConfigReader.get("valid.password"));

        assertTrue(inventory.isLoaded(), "Debería mostrarse el inventario tras un login válido");
        assertEquals(inventory.getTitle(), "Products", "Título de la página de inventario");
    }

    @Test(description = "LOGIN-002 - Inicio de sesión con contraseña incorrecta",
            groups = {"ui", "login"})
    public void login002_wrongPasswordShowsError() {
        LoginPage login = loginPage().attemptInvalidLogin(
                ConfigReader.get("standard.user"), ConfigReader.get("invalid.password"));

        assertEquals(login.getErrorMessage(), WRONG_CREDENTIALS_ERROR, "Mensaje de error de credenciales");
        assertTrue(login.isLoginButtonDisplayed(), "El usuario debería permanecer en la pantalla de login");
        assertFalse(login.getCurrentUrl().contains("inventory.html"), "No debería accederse al inventario");
    }
}
