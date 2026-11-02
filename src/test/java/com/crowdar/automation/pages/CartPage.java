package com.crowdar.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_LIST = By.cssSelector("[data-test='cart-list']");
    private static final By CART_ITEM_NAME = By.cssSelector("[data-test='cart-list'] [data-test='inventory-item-name']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains("cart.html"));
        visible(CART_LIST);
        return this;
    }

    public List<String> getProductNames() {
        return driver.findElements(CART_ITEM_NAME).stream()
                .map(WebElement::getText)
                .toList();
    }
}
