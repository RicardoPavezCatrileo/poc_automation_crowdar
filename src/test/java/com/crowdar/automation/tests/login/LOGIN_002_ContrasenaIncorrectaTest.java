package com.crowdar.automation.tests.login;

import com.crowdar.automation.base.BaseTest;
import com.crowdar.automation.pages.LoginPage;
import com.crowdar.automation.utils.ConfigReader;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/** LOGIN-002 — Inicio de sesión con contraseña incorrecta (ver docs/casos_de_prueba.md). */
public class LOGIN_002_ContrasenaIncorrectaTest extends BaseTest {

    private static final String WRONG_CREDENTIALS_ERROR =
            "Epic sadface: Username and password do not match any user in this service";

    @Test(description = "LOGIN-002 - Inicio de sesión con contraseña incorrecta",
            groups = {"ui", "login"})
    public void LOGIN_002_contrasenaIncorrectaMuestraError() {
        LoginPage login = loginPage().attemptInvalidLogin(
                ConfigReader.get("standard.user"), ConfigReader.get("invalid.password"));

        assertEquals(login.getErrorMessage(), WRONG_CREDENTIALS_ERROR, "Mensaje de error de credenciales");
        assertTrue(login.isLoginButtonDisplayed(), "El usuario debería permanecer en la pantalla de login");
        assertFalse(login.getCurrentUrl().contains("inventory.html"), "No debería accederse al inventario");
    }
}
