import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MobileDynamicControlsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        // Настройка параметров iPad Pro 11
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)")
                .setViewportSize(834, 1194)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true);

        // Запускаем браузер в headless режиме для CI/CD
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(isHeadless));
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testInputEnabling() {
        // Переходим на страницу
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
        
        // Ждем загрузки страницы
        page.waitForTimeout(2000);
        
        // Находим поле ввода и проверяем, что оно изначально неактивно
        Locator inputField = page.locator("input[type='text']");
        assertTrue(inputField.isDisabled(), "Поле ввода должно быть изначально неактивным");
        
        // Находим кнопку "Enable" и кликаем на неё
        Locator enableButton = page.locator("button:has-text('Enable')");
        enableButton.click();
        
        // Ждем, пока поле станет активным (появление сообщения)
        page.waitForSelector("#message:has-text('It\\'s enabled!')", 
            new Page.WaitForSelectorOptions().setTimeout(10000));
        
        // Проверяем, что поле ввода стало активным
        assertFalse(inputField.isDisabled(), "Поле ввода должно стать активным после клика");
        
        // Дополнительная проверка - можно ввести текст
        inputField.fill("Тест с iPad Pro 11");
        assertEquals("Тест с iPad Pro 11", inputField.inputValue(), 
            "Должна быть возможность ввести текст");
        
        System.out.println("✅ Тест успешно выполнен на эмуляции iPad Pro 11!");
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}