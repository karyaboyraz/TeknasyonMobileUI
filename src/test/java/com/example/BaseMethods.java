package com.example;

import base.Base;
import base.IdentifyDevices;
import com.thoughtworks.gauge.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

public class BaseMethods extends Base {

    private String contextNotificationsTitle;
    private String contextNotificationsText;


    Logger logger = Logger.getLogger(String.valueOf(BaseMethods.class));
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    boolean isAndroid = IdentifyDevices.devicePlatform.equals("Android");


    @Step("Check element existence <ElementName>")
    public WebElement createWebElement(String ElementName) {
        String xpathTemplate;
        if (ElementName.toLowerCase().contains("xpath.")) {
            xpathTemplate = ElementName.replaceAll("xpath.", "");
        } else {
            if (isAndroid) {
                xpathTemplate = "//*[contains(@resource-id,'" + ElementName + "') or contains(@text ,'" + ElementName + "')]";
            } else {
                xpathTemplate = "//*[contains(@name,'" + ElementName + "') or contains(@label ,'" + ElementName + "')]";
            }
        }

        WebElement mainElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathTemplate)));
        Assert.assertNotNull(mainElement, "Element is not found: %s".formatted(ElementName));
        logger.info(mainElement.getText());
        logger.info("Element is found: %s".formatted(ElementName));

        return mainElement;
    }

    @Step("Check element existence <ElementName> must be <status>")
    public void checkElementVisibility(String ElementName, Visibility status) {
        boolean shouldBeVisible = status == Visibility.visible;
        boolean isVisible;

        try {
            WebDriverWait waitVisibility = new WebDriverWait(driver, Duration.ofSeconds(5));
            isVisible = waitVisibility.until(driver -> {
                try {
                    WebElement element = createWebElement(ElementName);
                    return element.isDisplayed();
                } catch (NoSuchElementException e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            isVisible = false;
        }
        Assert.assertEquals(isVisible, shouldBeVisible, "Element visibility mismatch: %s".formatted(ElementName));
    }

    public enum Visibility {
        visible,
        hidden
    }

    @Step("Click button <ElementName>")
    public void clickButton(String ElementName) {
        createWebElement(ElementName).click();
        logger.info("Clicked on the button: %s".formatted(ElementName));
    }

    @Step("Write text <text> to this element <ElementName>")
    public void writeTextToInputArea(String text, String ElementName) {
        String xpathTemplate;
        String pathExtensions = "/..//android.widget.EditText";

        if (isAndroid) {
            xpathTemplate = "//*[contains(@resource-id,'" + ElementName + "') or contains(@text ,'" + ElementName + "')]";
        } else {
            xpathTemplate = "//*[contains(@name,'" + ElementName + "') or contains(@label ,'" + ElementName + "')]";
        }

        WebElement element = createWebElement(ElementName);
        element = element != null ? element : createWebElement("xpath." + xpathTemplate + pathExtensions);

        clearAndSendKeys(element, text);
    }

    private void clearAndSendKeys(WebElement element, String text) {
        if (!element.getText().isEmpty()) {
            element.clear();
        }
        element.click();
        element.sendKeys(text);
    }

    @Step("Long press on element <ElementName>")
    public void longPress(String elementName) {
        WebElement element = createWebElement(elementName);

        Actions action = new Actions(driver);
        action.clickAndHold(element)
                .pause(Duration.ofSeconds(2))
                .release()
                .perform();
    }

    @Step("Search Element <ElementName> with Swipe <retries> times")
    public WebElement findElementWithSwipe(String ElementName, int retries) {
        for (int i = 0; i < retries; i++) {
            try {
                WebElement element = createWebElementForRetry(ElementName);
                logger.info("-------------------------------" + element + "-----------------------------------");
                if (element != null) {
                    logger.info("Element found: " + element);
                    return element;
                }
            } catch (TimeoutException e) {
                swipeWithoutDirection();
                logger.info("Element not found, retrying...");
            }
        }
        throw new NoSuchElementException("Element not found after " + retries + " retries.");
    }

    @Step("Search Element <ElementName> with Horizontal Swipe <retries> times")
    public WebElement findElementWithHorizontalSwipe(String ElementName, int retries) {
        for (int i = 0; i < retries; i++) {
            try {
                WebElement element = createWebElementForRetry(ElementName);
                logger.info("-------------------------------" + element + "-----------------------------------");
                if (element != null) {
                    logger.info("Element found: " + element);
                    return element;
                }
            } catch (TimeoutException e) {
                swipeHorizontalDirection();
                logger.info("Element not found, retrying...");
            }
        }
        throw new NoSuchElementException("Element not found after " + retries + " retries.");
    }

    private WebElement createWebElementForRetry(String ElementName) {
        WebDriverWait waitRetry = new WebDriverWait(driver, Duration.ofSeconds(3));
        String xpathTemplate;
        if (isAndroid) {
            xpathTemplate = "//*[contains(@resource-id,'" + ElementName + "') or contains(@text ,'" + ElementName + "')]";
        } else {
            xpathTemplate = "//*[contains(@name,'" + ElementName + "') or contains(@label ,'" + ElementName + "')]";
        }

        WebElement mainElement = waitRetry.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathTemplate)));
        Assert.assertNotNull(mainElement, "Element is not found: %s".formatted(ElementName));
        logger.info(mainElement.getText());
        logger.info("Element is found: %s".formatted(ElementName));

        return mainElement;
    }

    @Step("Get text and title value from Notifications and store it")
    public void captureNotification() {
        String xpathNotification = "//android.widget.FrameLayout[.//android.widget.TextView[@resource-id='android:id/app_name_text' and @text='API Demos']]//android.widget.TextView[@resource-id='android:id/title']";
        WebElement element = createWebElement("xpath." + xpathNotification);
        contextNotificationsTitle = element.getAttribute("text");

        xpathNotification = "//android.widget.FrameLayout[.//android.widget.TextView[@resource-id='android:id/app_name_text' and @text='API Demos']]//android.widget.TextView[@resource-id='android:id/text']";
        element = createWebElement("xpath." + xpathNotification);
        contextNotificationsText = element.getAttribute("text");

        logger.info("Notification title: " + contextNotificationsTitle);
        logger.info("Notification text: " + contextNotificationsText);
    }

    @Step("Check Notifications title and text values")
    public void checkNotificationsElement() {
        createWebElement(contextNotificationsText);
        createWebElement(contextNotificationsTitle);
    }

    @Step("Get Text Value from <elementName> and compare with <expectedValue>")
    public void getTextValueFromElement(String elementName, String expectedValue) {
        WebElement element = createWebElement(elementName);
        String actualValue = element.getAttribute("text");
        Assert.assertEquals(actualValue, expectedValue,
                "The value of element '" + elementName + "' does not match the expected value.");

        logger.info("Success: The value of '" + elementName + "' matches the expected value.");
    }

    public void swipeHorizontalDirection() {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.getWidth() * 0.75);
        int endX = (int) (size.getWidth() * 0.25);
        int startY = size.getHeight() / 8;

        Sequence scroll = new Sequence(finger, 0);
        scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, startY));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(scroll));
    }

    public void swipeWithoutDirection() {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = size.getHeight() / 2;
        int endY = (int) (size.getHeight() * 0.25);

        Sequence scroll = new Sequence(finger, 0);
        scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(scroll));
    }
}