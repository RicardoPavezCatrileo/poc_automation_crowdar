package com.crowdar.automation.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

/**
 * Crea y mantiene el WebDriver de la prueba en curso (ThreadLocal).
 * Los drivers (chromedriver/geckodriver) los resuelve Selenium Manager automáticamente.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<String> BROWSER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver createDriver(String browser, boolean headless) {
        String normalized = browser.toLowerCase();
        WebDriver driver = switch (normalized) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            default -> throw new IllegalArgumentException(
                    "Navegador no soportado: '" + browser + "'. Valores válidos: chrome, firefox");
        };
        DRIVER.set(driver);
        BROWSER.set(normalized);
        return driver;
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        // Evita el diálogo de "contraseña filtrada" de Chrome, que bloquea la UI tras el login en SauceDemo.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        options.addArguments("--window-size=1366,900", "--disable-notifications");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1366", "--height=900");
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    /** Devuelve el driver activo o null si la prueba en curso no es de UI. */
    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static String getBrowser() {
        return BROWSER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
            BROWSER.remove();
        }
    }
}
