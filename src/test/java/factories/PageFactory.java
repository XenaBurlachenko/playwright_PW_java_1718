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
    

    public Object getCurrentPage() {
        String url = page.url();
        
        if (url.contains("drag_and_drop") || url.contains("the-internet.herokuapp.com")) {
            return createDragDropPage();
        }
        
        throw new RuntimeException("Unknown page type for URL: " + url);
    }
}