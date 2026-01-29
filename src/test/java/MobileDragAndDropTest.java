import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class MobileDragAndDropTest {

    @Test
    void testDragAndDropOnSamsungGalaxy() {
        // Создаем Playwright
        Playwright playwright = Playwright.create();
        
        try {
            // Настройка параметров Samsung Galaxy S22 Ultra
            Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Linux; Android 12; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Mobile Safari/537.36")
                    .setViewportSize(384, 873)
                    .setDeviceScaleFactor(3.5)
                    .setIsMobile(true)
                    .setHasTouch(true);

            // Запускаем браузер в headless режиме для CI
            boolean headless = true; // Для CI ставим true
            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless)
            );
            
            BrowserContext context = browser.newContext(deviceOptions);
            Page page = context.newPage();

            // 1. Переходим на страницу
            page.navigate("https://the-internet.herokuapp.com/drag_and_drop");
            
            // 2. Ждем элементы
            page.waitForSelector("#column-a");
            page.waitForSelector("#column-b");
            
            Locator columnA = page.locator("#column-a");
            Locator columnB = page.locator("#column-b");
            
            // 3. Проверяем начальное состояние
            assertEquals("A", columnA.textContent().trim());
            assertEquals("B", columnB.textContent().trim());
            
            System.out.println("Начальное состояние: A=" + columnA.textContent().trim() + 
                             ", B=" + columnB.textContent().trim());
            
            // 4. Выполняем перетаскивание (как в задании)
            columnA.dragTo(columnB);
            
            // 5. Ждем изменения 
            // JavaScript вытянул из меня всю душу и не запустился, будет по-простому. Проверяем несколько раз с небольшими паузами
            boolean success = false;
            for (int i = 0; i < 20; i++) {
                String textA = columnA.textContent().trim();
                String textB = columnB.textContent().trim();
                
                if ("B".equals(textA) && "A".equals(textB)) {
                    success = true;
                    break;
                }
                page.waitForTimeout(500); 
            }
            
            // 6. Проверяем результат
            assertTrue(success, "Текст не изменился после перетаскивания");
            
            assertEquals("B", columnA.textContent().trim());
            assertEquals("A", columnB.textContent().trim());
            
            System.out.println("Финальное состояние: A=" + columnA.textContent().trim() + 
                             ", B=" + columnB.textContent().trim());
            System.out.println("✅ Тест успешно выполнен на Samsung Galaxy S22 Ultra!");
            
        } catch (Exception e) {
            fail("Тест упал с ошибкой: " + e.getMessage());
        } finally {
            playwright.close();
        }
    }
}