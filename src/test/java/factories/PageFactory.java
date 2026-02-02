package factories;

public class PageFactory {
    private final com.microsoft.playwright.Page page;
    
    public PageFactory(com.microsoft.playwright.Page page) {
        this.page = page;
    }
    
    public pages.DragDropPage createDragDropPage() {
        return new pages.DragDropPage(page);
    }
}