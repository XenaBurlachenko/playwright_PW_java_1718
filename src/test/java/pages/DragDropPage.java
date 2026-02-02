package pages;

import com.microsoft.playwright.Page;

public class DragDropPage {
    private final Page page;
    private components.DragDropArea dragDropArea;  // Полный путь
    
    public DragDropPage(Page page) {
        this.page = page;
    }
    
    public components.DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new components.DragDropArea(page);
        }
        return dragDropArea;
    }
    
    public DragDropPage open() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");
        return this;
    }
    
    public DragDropPage dragAToB() {
        dragDropArea().dragAToB();
        return this;
    }
}
