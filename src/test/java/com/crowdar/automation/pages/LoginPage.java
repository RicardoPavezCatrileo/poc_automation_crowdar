package com.crowdar.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterCredentials(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        return this;
    }

    public void submit() {
        click(LOGIN_BUTTON);
    }

    /** Login con credenciales válidas; devuelve el inventario ya cargado. */
    public InventoryPage loginAs(String username, String password) {
        enterCredentials(username, password).submit();
        return new InventoryPage(driver).waitUntilLoaded();
    }

    /** Login que se espera rechazado; espera a que aparezca el mensaje de error. */
    public LoginPage attemptInvalidLogin(String username, String password) {
        enterCredentials(username, password).submit();
        visible(ERROR_MESSAGE);
        return this;
    }

    public String getErrorMessage() {
        return visible(ERROR_MESSAGE).getText();
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(LOGIN_BUTTON);
    }
}
