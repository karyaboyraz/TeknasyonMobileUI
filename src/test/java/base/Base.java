package base;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.ExecutionContext;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Base {
    public static AppiumDriver driver;
    IdentifyDevices device = new IdentifyDevices();
    private static final Logger logger = Logger.getLogger(Base.class.getName());

    @BeforeScenario
    public void beforeScenario(ExecutionContext context) throws Exception {
        AppiumServerManager.startAppiumServer();

        try {
            device.createCapabilities();
        } catch (Exception e) {
            logger.severe("Error creating capabilities: " + e.getMessage());
            throw e;
        }

        DesiredCapabilities capabilities = getDesiredCapabilities();
        handleReinstallIfNeeded(context, capabilities);
        driver = initializeDriver(capabilities);
    }


    private void handleReinstallIfNeeded(ExecutionContext context, DesiredCapabilities capabilities) throws Exception {
        if (context.getCurrentScenario().getTags().contains("reinstall")) {
            String appPath = (String) capabilities.getCapability("appPath");
            if (appPath != null && !appPath.isEmpty()) {
                capabilities.setCapability("fullReset", true);
                capabilities.setCapability("noReset", false);
                logger.info("App will be reinstalled from path: " + appPath);
            } else {
                throw new Exception("'app' capability is not set in capabilities.xml");
            }
        }
    }

    private AppiumDriver initializeDriver(DesiredCapabilities capabilities) throws Exception {
        URL appiumServerURL = new URL("http://127.0.0.1:4723");
        logger.info("Initializing driver with capabilities: " + capabilities);
        String platform = IdentifyDevices.devicePlatform;

        if (platform.equalsIgnoreCase("Android")) {
            return new AndroidDriver(appiumServerURL, capabilities);
        } else if (platform.equalsIgnoreCase("iOS")) {
            return new IOSDriver(appiumServerURL, capabilities);
        } else {
            throw new Exception("Unsupported platform: " + platform);
        }
    }

    private static DesiredCapabilities getDesiredCapabilities() throws Exception {
        String deviceConfigFile = "deviceProperties/capabilities.xml";
        File xmlFile = new File(deviceConfigFile);
        if (!xmlFile.exists()) {
            throw new FileNotFoundException("XML file not found for device configuration: " + deviceConfigFile);
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("capability");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String name = element.getAttribute("name");
            String value = element.getTextContent().trim();
            capabilities.setCapability(name, value);

            if (name.equals("appPath")) {
                capabilities.setCapability("app", value);
            }
        }
        return capabilities;
    }


    @AfterScenario
    public void afterScenario() {
        logger.info("------------ Scenario is ending ------------");
        if (driver != null) {
            driver.quit();
            logger.info("Driver quit successfully.");
        } else {
            logger.warning("Driver was not initialized.");
        }
    }
}
