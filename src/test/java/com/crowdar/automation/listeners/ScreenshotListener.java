package com.crowdar.automation.listeners;

import com.crowdar.automation.factory.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * En cada fallo: si hay un WebDriver activo (prueba UI) guarda una captura PNG en
 * target/screenshots y asocia la ruta al resultado; si no (prueba API) solo registra el error.
 * Cualquier problema al capturar se registra sin reemplazar el error original de la prueba.
 */
public class ScreenshotListener implements ITestListener {

    private static final Path SCREENSHOT_DIR = Paths.get("target", "screenshots");
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        WebDriver driver = DriverFactory.getDriver();

        if (driver == null) {
            Reporter.log("Fallo sin WebDriver (prueba no UI) en " + testName + ": " + result.getThrowable(), true);
            return;
        }

        String browser = DriverFactory.getBrowser();
        String fileName = "%s_%s_%s.png".formatted(testName, browser, LocalDateTime.now().format(TIMESTAMP));
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            Path target = SCREENSHOT_DIR.resolve(fileName);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(target, png);

            String absolutePath = target.toAbsolutePath().toString();
            result.setAttribute("screenshot", absolutePath);
            Reporter.setCurrentTestResult(result);
            Reporter.log("Captura de pantalla [" + browser + "]: " + absolutePath, true);
            Reporter.log("<a href=\"" + target.toAbsolutePath().toUri() + "\">Ver captura</a>");
        } catch (IOException | RuntimeException e) {
            Reporter.log("No se pudo guardar la captura de " + testName + ": " + e, true);
        }
    }
}
