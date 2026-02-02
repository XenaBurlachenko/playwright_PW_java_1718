package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public abstract class BasePage {
    protected final Page page;
    
    public BasePage(Page page) {
        this.page = page;
    }
    
    public BasePage navigateTo(String url) {
        page.navigate(url);
        return this;
    }

    protected void waitForElementToBeVisible(String selector) {
        page.locator(selector).waitFor(
            new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
        );
    }
}
