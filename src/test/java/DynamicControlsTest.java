import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    @Test
    void testDynamicCheckbox() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        // Находим чекбокс с атрибутом "checkbox"
        Locator checkbox = page.locator("input[type='checkbox']");
        
        // Нажимает на кнопку "Remove"
        page.click("button:has-text('Remove')");
        
        // Ожидаем исчезновения чекбокса
        checkbox.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        
        // Проверяем, что появляется текст
        Locator message = page.locator("#message");
        assertEquals("It's gone!", message.textContent().trim());
        
        // Нажимаем на кнопку "Add"
        page.click("button:has-text('Add')");
        
        // Проверяем, что чекбокс снова отображается
        checkbox.waitFor(new Locator.WaitForOptions());
        assertTrue(checkbox.isVisible());
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}
