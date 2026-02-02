package factories;

import com.microsoft.playwright.Page;
import pages.DragDropPage;

public class PageFactory {
    private final Page page;
    
    public PageFactory(Page page) {
        this.page = page;
    }
    
    public DragDropPage createDragDropPage() {
        return new DragDropPage(page);
    }
    
    // Метод для динамического создания страниц на основе URL
    public Object getCurrentPage() {
        String url = page.url();
        if (url.contains("drag_and_drop")) {
            return createDragDropPage();
        }
        throw new RuntimeException("Unknown page: " + url);
    }
}
Обновленный DragDropTest.java с использованием фабрики:
java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.microsoft.playwright.*;
import factories.PageFactory;
import pages.DragDropPage;
import static org.junit.jupiter.api.Assertions.*;

public class DragDropTest {
    private Playwright playwright;
    private Browser browser;
    private Page page;
    private PageFactory pageFactory;
    private DragDropPage dragDropPage;
    
    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
        pageFactory = new PageFactory(page);
        dragDropPage = pageFactory.createDragDropPage();
    }
    
    @AfterEach
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
    
    @Test
    public void testDragAndDrop() {
        // Используем цепочки вызовов и ленивую инициализацию
        dragDropPage.open()
                   .dragAToB();
        
        // Проверяем через ленивую инициализацию компонента
        assertEquals("A", dragDropPage.dragDropArea().getTextB());
    }
    
    @Test
    public void testPageFactory() {
        dragDropPage.open();
        
        // Получаем страницу через фабрику на основе текущего URL
        Object currentPage = pageFactory.getCurrentPage();
        assertTrue(currentPage instanceof DragDropPage);
    }
}