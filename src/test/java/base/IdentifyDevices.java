package base;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class IdentifyDevices {

    public static void main(String[] args) {
        IdentifyDevices device = new IdentifyDevices();
        try {
            device.createCapabilities();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static final String ANDROID_AUTOMATION_NAME = "UiAutomator2";
    private static final String ANDROID_APP_PACKAGE = "io.appium.android.apis";
    private static final String ANDROID_APP_ACTIVITY = "io.appium.android.apis.ApiDemos";
    private static final String IOS_AUTOMATION_NAME = "";
    private static final String IOS_APP_PACKAGE = "";
    private static final String IOS_APP_ACTIVITY = "";
    private static final String NO_RESET = "false";
    private static final String FULL_RESET = "false";
    private static final String FILE_PATH = "deviceProperties/capabilities.xml";
    private static final String AUTO_PERMISSIONS = "true";


    public static String devicePlatform;
    private static String deviceUUID;

    public void createCapabilities() throws IOException, InterruptedException {
        detectAndroidDevice();
        if (deviceUUID == null) {
            System.out.println("No Android devices found.");
            detectIOSDevice();
        }
        if (deviceUUID == null) {
            throw new RuntimeException("No devices found.");
        }
        System.out.println(devicePlatform + " device found: " + deviceUUID);
        createCapFile();
    }

    private void detectAndroidDevice() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("adb devices");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        reader.readLine(); // Skip the first line

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.endsWith("device")) {
                deviceUUID = line.split("\\s+")[0];
                devicePlatform = "Android";
                return;
            } else if (line.endsWith("unauthorized")) {
                throw new RuntimeException("Unauthorized device. Please check your device and allow USB debugging.");
            }
        }
    }


    private void detectIOSDevice() throws IOException, InterruptedException {
        // To work on Mac devices, the following add-ons must be installed:
        // brew install libimobiledevice
        // brew install ideviceinstaller
        // brew install ios-webkit-debug-proxy

        Process process = Runtime.getRuntime().exec("idevice_id --list");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            System.out.println(reader.readLine());
            if ((deviceUUID = reader.readLine()) != null && !deviceUUID.isEmpty()) {
                deviceUUID = deviceUUID.trim();
            } else {
                throw new RuntimeException("No iOS devices found.");
            }
        }
        process.waitFor();
    }

    public static void createCapFile() {
        System.out.println("Creating capabilities for " + devicePlatform);
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = doc.createElement("capabilities");
            doc.appendChild(rootElement);

            addCapability(doc, rootElement, "platformName", devicePlatform);
            addCapability(doc, rootElement, "deviceName", deviceUUID);
            addCapability(doc, rootElement, "automationName", getAutomationName());
            addCapability(doc, rootElement, "appPackage", getAppPackage());
            addCapability(doc, rootElement, "appActivity", getAppActivity());
            addCapability(doc, rootElement, "noReset", NO_RESET);
            addCapability(doc, rootElement, "fullReset", FULL_RESET);
            addCapability(doc, rootElement, "autoGrantPermissions", AUTO_PERMISSIONS);

            String currentDir = System.getProperty("user.dir");
            String apkPath = currentDir + "/ApkFiles/ApiDemos.apk";
            addCapability(doc, rootElement, "appPath", apkPath);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(new java.io.File(FILE_PATH)));

            System.out.println("Saved the capabilities to " + FILE_PATH);
        } catch (Exception e) {
            System.err.println("An error occurred while creating capabilities: " + e.getMessage());
        }
    }


    private static String getAutomationName() {
        return devicePlatform.equals("Android") ? ANDROID_AUTOMATION_NAME : IOS_AUTOMATION_NAME;
    }

    private static String getAppPackage() {
        return devicePlatform.equals("Android") ? ANDROID_APP_PACKAGE : IOS_APP_PACKAGE;
    }

    private static String getAppActivity() {
        return devicePlatform.equals("Android") ? ANDROID_APP_ACTIVITY : IOS_APP_ACTIVITY;
    }

    private static void addCapability(Document doc, Element parent, String name, String value) {
        Element capability = doc.createElement("capability");
        capability.setAttribute("name", name);
        capability.appendChild(doc.createTextNode(value));
        parent.appendChild(capability);
    }
}