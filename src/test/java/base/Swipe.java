package base;

import com.thoughtworks.gauge.Step;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

/**
 * This class provides methods to perform swipe actions on a touch screen.
 */
public class Swipe extends Base {
    // A PointerInput instance representing a finger touch.
    private final PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

    /**
     * Performs a swipe action a specified number of times.
     * @param repeatCount the number of times to perform the swipe
     */
    @Step("Swipe <repeatCount> times")
    public void swipeWithoutDirection(int repeatCount) {
        for (int i = 0; i < repeatCount; i++) {
            swipeWithoutDirection();
            waitTime(1);
        }
    }

    /**
     * Opens the notification bar by performing a swipe gesture from the top of the screen.
     */
    @Step("Open notifications bar by swiping down")
    public void openNotificationsBarSwipe() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = (int) (size.getHeight() * 0.01);
        int endY = (int) (size.getHeight() * 0.80);

        swipeSequence(startX, startY, startX, endY);
    }

    @Step("Close notifications bar by swiping down")
    public void closeNotificationsBarSwipe() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = (int) (size.getHeight() * 0.80);
        int endY = (int) (size.getHeight() * 0.01);

        swipeSequence(startX, startY, startX, endY);
    }

    /**
     * Performs a swipe action from one set of coordinates to another.
     * @param startX the starting x-coordinate
     * @param startY the starting y-coordinate
     * @param endX the ending x-coordinate
     * @param endY the ending y-coordinate
     */
    @Step("Swipe from coordinates <startX>, <startY> to <endX>, <endY>")
    public void swipeByCoordinate(int startX, int startY, int endX, int endY) {
        swipeSequence(startX, startY, endX, endY);
    }

    /**
     * Performs a swipe action in the middle of the screen without a specified direction.
     */
    @Step("Swipe")
    public void swipeWithoutDirection() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = size.getHeight() / 2;
        int endY = (int) (size.getHeight() * 0.25);
        int endX;
        endX = startX;

        swipeSequence(startX, startY, endX, endY);
    }

    /**
     * Performs a swipe action in a specified direction.
     * @param direction the direction to swipe
     */
    @Step("Swipe <direction>")
    public void swipe(SwipeDirection direction) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = size.getHeight() / 2;
        int endX = startX, endY = startY;

        switch (direction) {
            case SWIPE_RIGHT:
                endX = (int) (size.getWidth() * 0.75);
                break;
            case SWIPE_LEFT:
                endX = (int) (size.getWidth() * 0.25);
                break;
            case SWIPE_UP:
                endY = (int) (size.getHeight() * 0.25);
                break;
            case SWIPE_DOWN:
                endY = (int) (size.getHeight() * 0.75);
                break;
        }
        swipeSequence(startX, startY, endX, endY);
    }

    /**
     * Creates a sequence of actions to perform a swipe and executes it.
     * @param startX the starting x-coordinate
     * @param startY the starting y-coordinate
     * @param endX the ending x-coordinate
     * @param endY the ending y-coordinate
     */
    private void swipeSequence (int startX, int startY, int endX, int endY) {
        Sequence dragNDrop = new Sequence(finger, 0);
        dragNDrop.addAction(finger.createPointerMove(Duration.ofSeconds(0), PointerInput.Origin.viewport(), startX, startY));
        dragNDrop.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        dragNDrop.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), endX, endY));
        dragNDrop.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(dragNDrop));
    }

    /**
     * Pauses execution for a specified number of seconds.
     * @param time the number of seconds to pause
     */
    private void waitTime(int time) {
        try {
            Thread.sleep(time * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enum representing possible swipe directions.
     */
    public enum SwipeDirection {
        SWIPE_RIGHT,
        SWIPE_LEFT,
        SWIPE_DOWN,
        SWIPE_UP
    }
}