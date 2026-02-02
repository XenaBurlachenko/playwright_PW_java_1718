package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import factories.PageFactory;
import pages.DragDropPage;

public class DragDropTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;
    PageFactory pageFactory;
    
    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)); // Для визуального наблюдения
        context = browser.newContext();
        page = context.newPage();
        pageFactory = new PageFactory(page);
    }
    
    @AfterEach
    public void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
    
    @Test
    public void testDragAndDrop() {
        // Создание страницы через фабрику
        DragDropPage dragDropPage = pageFactory.createDragDropPage();
        
        // Тест с использованием цепочек вызовов
        dragDropPage.open()
                   .dragElementAToB()
                   .verifyElementBContainsText("A");
        
        // Дополнительная проверка
        String textInB = dragDropPage.dragDropArea().getTextB();
        Assertions.assertEquals("A", textInB, 
            "После перетаскивания в элементе B должен быть текст 'A'");
    }
    
    @Test
    public void testInitialState() {
        DragDropPage dragDropPage = pageFactory.createDragDropPage();
        
        dragDropPage.open();
        
        // Проверка начального состояния
        String initialTextA = dragDropPage.dragDropArea().getTextA();
        String initialTextB = dragDropPage.dragDropArea().getTextB();
        
        Assertions.assertEquals("A", initialTextA, 
            "Начальный текст в элементе A должен быть 'A'");
        Assertions.assertEquals("B", initialTextB, 
            "Начальный текст в элементе B должен быть 'B'");
    }
    
    @Test
    public void testCompleteWorkflow() {
        // Полная цепочка действий одним вызовом
        DragDropPage dragDropPage = pageFactory.createDragDropPage();
        
        dragDropPage.performDragAndDropAndVerify();
        
        // Проверка, что элементы поменялись местами
        Assertions.assertEquals("B", dragDropPage.dragDropArea().getTextA(),
            "После перетаскивания в элементе A должен быть текст 'B'");
    }
    
    @Test
    public void testPageFactoryDynamicPageDetection() {
        // Переход на страницу
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");
        
        // Получение страницы через фабрику на основе текущего URL
        Object currentPage = pageFactory.getCurrentPage();
        
        Assertions.assertTrue(currentPage instanceof DragDropPage,
            "Фабрика должна вернуть DragDropPage для URL с drag_and_drop");
    }
}
