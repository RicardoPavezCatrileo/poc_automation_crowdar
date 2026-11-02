package com.crowdar.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By INVENTORY_LIST = By.cssSelector("[data-test='inventory-list']");
    private static final By INVENTORY_ITEM = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_BUTTON = By.cssSelector("button.btn_inventory");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        visible(INVENTORY_LIST);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains("inventory.html") && isDisplayed(INVENTORY_LIST);
    }

    public String getTitle() {
        return visible(TITLE).getText();
    }

    /**
     * Agrega un producto buscándolo por su nombre visible, evitando depender
     * de la posición en la grilla. SauceDemo reemplaza el botón por uno nuevo ("Remove"),
     * por eso la espera vuelve a localizarlo en cada sondeo en lugar de reutilizar la referencia.
     */
    public InventoryPage addProductToCart(String productName) {
        findItemByName(productName).findElement(ITEM_BUTTON).click();
        wait.ignoring(StaleElementReferenceException.class)
                .until(d -> findItemByName(productName).findElement(ITEM_BUTTON).getText().equals("Remove"));
        return this;
    }

    /** Número que muestra el ícono del carrito; 0 si no hay badge. */
    public int getCartCount() {
        List<WebElement> badge = driver.findElements(CART_BADGE);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText().trim());
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver).waitUntilLoaded();
    }

    private WebElement findItemByName(String productName) {
        visible(INVENTORY_LIST);
        return driver.findElements(INVENTORY_ITEM).stream()
                .filter(item -> item.findElement(ITEM_NAME).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado en el inventario: " + productName));
    }
}
