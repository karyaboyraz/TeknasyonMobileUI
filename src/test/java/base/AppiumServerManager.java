package base;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class AppiumServerManager {

    private static final int TIMEOUT = 5;
    private static final long TIMEOUT_MILLIS = TIMEOUT * 1000L;
    private static final int APPIUM_PORT = 4723;
    private static final String APPIUM_ADDRESS = "127.0.0.1";
    private static final String[] APPIUM_COMMAND_WIN = {"cmd", "/c", "start", "appium", "--address", APPIUM_ADDRESS, "--port", String.valueOf(APPIUM_PORT), "--allow-cors"};

    private static String[] getAppiumCommandMac() {
        String scriptPath = new File(System.getProperty("user.dir"), "startAppium.sh").getAbsolutePath();

        if (scriptPath.contains(" ")) {
            scriptPath = "'" + scriptPath + "'";
        }

        System.out.println("Script path: " + scriptPath);
        return new String[]{
                "osascript",
                "-e", "tell application \"Terminal\"",
                "-e", "do script \"bash " + scriptPath + "\"",
                "-e", "end tell"
        };
    }

    public static void startAppiumServer() {
        if (isAppiumServerRunning()) {
            System.out.println("Appium server is already running.");
            return;
        }

        String[] command = isWindows() ? APPIUM_COMMAND_WIN : getAppiumCommandMac();
        System.out.println("Starting Appium server with command: " + Arrays.toString(command));
        System.out.println("Server will be available at " + APPIUM_ADDRESS + ":" + APPIUM_PORT + " in "+ TIMEOUT +" seconds");
        executeCommand(command);
        try {
            Thread.sleep(TIMEOUT_MILLIS);
        } catch (InterruptedException e) {
            handleException(e, "The operation was interrupted.");
        }
    }

    private static void executeCommand(String... command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            if (process.waitFor() != 0) {
                System.err.println("Error executing command: " + Arrays.toString(command));
            }
        } catch (IOException | InterruptedException e) {
            handleException(e, "Failed to execute command: " + Arrays.toString(command));
        }
    }

    private static void handleException(@org.jetbrains.annotations.NotNull Exception e, String message) {
        System.err.println(message);
        e.printStackTrace();
    }

    private static boolean isAppiumServerRunning() {
        try (Socket ignored = new Socket(APPIUM_ADDRESS, APPIUM_PORT)) {
            System.out.println("Successfully connected to Appium server on port " + APPIUM_PORT);
            return true;
        } catch (IOException e) {
            System.out.println("Cannot connect to Appium server on port " + APPIUM_PORT + ", it seems to be free.");
            return false;
        }
    }

    private static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Windows");
    }

    public static void main(String[] args) {
        startAppiumServer();
    }
}