import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class HoverTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    @BeforeEach
    void setup() {
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(10000);
    }

    @Test
    void testHoverProfiles() {
        // Переходим на страницу
        page.navigate("https://the-internet.herokuapp.com/hovers");
        
        // Ждем загрузки страницы
        page.waitForLoadState();

        // Находим все элементы с классом .figure
        Locator figures = page.locator(".figure");
        int count = figures.count();
        
        // Проверяем, что элементы найдены
        assertTrue(count > 0, "Должно быть найдено хотя бы одно изображение");
        System.out.println("Найдено элементов: " + count);

        for (int i = 0; i < count; i++) {
            System.out.println("\n--- Обработка элемента #" + (i + 1) + " ---");
            
            Locator figure = figures.nth(i);
            
            // Наводим курсор на элемент
            figure.hover();
            
            // Проверяем, что появилась ссылка "View profile"
            Locator profileLink = figure.locator("text=View profile");
            
            // Ждем пока ссылка станет видимой
            profileLink.waitFor();
            
            // Проверка видимости ссылки
            assertTrue(profileLink.isVisible(), 
                "Ссылка 'View profile' должна быть видимой после наведения на элемент " + (i + 1));
            
            // Кликаем на ссылку
            profileLink.click();
            
            // Ждем загрузки новой страницы
            page.waitForLoadState();
            
            // Проверяем, что URL соответствует /users/{id}
            String currentUrl = page.url();
            System.out.println("Текущий URL: " + currentUrl);
            
            // Проверяем наличие /users/ в URL
            assertTrue(currentUrl.contains("/users/"), 
                "URL должен содержать '/users/'. Текущий URL: " + currentUrl);
            
            // Проверяем формат /users/{id}, где id - число
            String[] urlParts = currentUrl.split("/users/");
            
            if (urlParts.length > 1) {
                String userId = urlParts[1].replaceAll("[^0-9]", "");
                assertFalse(userId.isEmpty(), "ID пользователя должен быть числом");
                System.out.println("ID пользователя: " + userId);
            } else {
                fail("URL не содержит ID пользователя");
            }
            
            // Возвращаемся назад
            page.goBack();
            
            // Ждем загрузки исходной страницы
            page.waitForLoadState();
            
            // Ждем пока элементы снова появятся
            page.waitForSelector(".figure");
            
            // Обновляем локаторы после возврата
            figures = page.locator(".figure");
        }
        
        System.out.println("\nВсе тесты успешно завершены");
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @AfterAll
    static void teardownClass() {

        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}