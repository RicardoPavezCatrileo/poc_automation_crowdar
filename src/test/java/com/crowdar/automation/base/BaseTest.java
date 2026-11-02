package com.crowdar.automation.base;

import com.crowdar.automation.factory.DriverFactory;
import com.crowdar.automation.pages.InventoryPage;
import com.crowdar.automation.pages.LoginPage;
import com.crowdar.automation.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base de las pruebas UI: un navegador nuevo por método (pruebas independientes)
 * y cierre garantizado aunque la prueba falle.
 *
 * El orden es seguro para las evidencias: TestNG notifica onTestFailure a los listeners
 * antes de ejecutar @AfterMethod, por lo que la captura se toma con el navegador abierto.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        String browser = ConfigReader.get("browser");
        boolean headless = ConfigReader.getBoolean("headless");

        driver = DriverFactory.createDriver(browser, headless);
        result.setAttribute("browser", browser);
        Reporter.log("Navegador: " + browser + (headless ? " (headless)" : ""), true);

        driver.get(ConfigReader.get("base.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    protected LoginPage loginPage() {
        return new LoginPage(driver);
    }

    /** Atajo para pruebas cuyo objetivo no es el login. */
    protected InventoryPage loginAsStandardUser() {
        return loginPage().loginAs(ConfigReader.get("standard.user"), ConfigReader.get("valid.password"));
    }
}
