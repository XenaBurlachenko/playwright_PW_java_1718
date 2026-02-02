package pages;

import com.microsoft.playwright.Page;

import components.DragDropArea;

public class DragDropPage extends BasePage {
    private DragDropArea dragDropArea;
    private static final String DRAG_DROP_URL = "https://the-internet.herokuapp.com/drag_and_drop";
    
    public DragDropPage(Page page) {
        super(page);
    }
    
    // Ленивая инициализация компонента
    public DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new DragDropArea(page);
        }
        return dragDropArea;
    }
    
    // Цепочки вызовов 
    public DragDropPage open() {
        navigateTo(DRAG_DROP_URL);
        return this;
    }
    
    public DragDropPage dragElementAToB() {
        dragDropArea().dragAToB();
        return this;
    }
    
    public DragDropPage verifyElementBContainsText(String expectedText) {
        dragDropArea().verifyTextInBEquals(expectedText);
        return this;
    }
    
    // Альтернативный подход с полной цепочкой
    public DragDropPage performDragAndDropAndVerify() {
        return open()
            .dragElementAToB()
            .verifyElementBContainsText("A");
    }
}
