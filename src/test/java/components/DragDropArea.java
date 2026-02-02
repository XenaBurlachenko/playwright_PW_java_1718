package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class DragDropArea {
    private final Page page;
    private Locator elementA;
    private Locator elementB;
    
    public DragDropArea(Page page) {
        this.page = page;
    }
    
    private void initElements() {
        if (elementA == null) {
            elementA = page.locator("#column-a");
            elementB = page.locator("#column-b");
        }
    }
    
    public DragDropArea dragAToB() {
        initElements();
        elementA.dragTo(elementB);
        return this;
    }
    
    public String getTextA() {
        initElements();
        return elementA.textContent();
    }
    
    public String getTextB() {
        initElements();
        return elementB.textContent();
    }
    
    public DragDropArea verifyTextInBEquals(String expectedText) {
        initElements();
        String actualText = getTextB();
        if (!actualText.equals(expectedText)) {
            throw new AssertionError(
                String.format("Expected text '%s' in element B, but found '%s'", 
                expectedText, actualText)
            );
        }
        return this;
    }
}
