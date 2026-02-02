package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.microsoft.playwright.*;
import static org.junit.jupiter.api.Assertions.*;
import components.DragDropArea;

public class DragDropTest {
    private Playwright playwright;
    private Browser browser;
    private Page page;
    private DragDropPage dragDropPage;
    
    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
        dragDropPage = new DragDropPage(page);
    }
    
    @AfterEach
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
    
    @Test
    public void testDragAndDrop() {
        dragDropPage.navigateTo("https://the-internet.herokuapp.com/drag_and_drop");
        dragDropPage.dragAToB();
        assertEquals("A", dragDropPage.dragDropArea().getTextB());
    }
}