package org.login;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        runFacebookLoginTest();
    }

    private static void runFacebookLoginTest() throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(Main.class);
        WebDriver driver = null;
        String username = "";
        String password = "";

        // Read the JSON file
        try {
            logger.info("Import json file");
            File jsonFile = new File("C:\\temp\\facebook.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonFile);

            // Extract the username and password values
            username = jsonNode.get("facebookCredentials").get("email").asText();
            password = jsonNode.get("facebookCredentials").get("password").asText();
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }

        // Create ChromeOptions instance and add arguments
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");

        logger.info("Assigning Chromedriver");
        try {
            // Set the path to the ChromeDriver executable
            System.setProperty("webdriver.chrome.driver", "src/main/java/chromedriver.exe");
            //Initialize the driver
            driver = new ChromeDriver(options);
            // Navigate to the Facebook login page
            driver.get("https://sv-se.facebook.com/");
        } catch (Exception e){
            logger.error(e.getMessage());
        }

        // Accept only necessary cookies
        logger.info("Handling cookies window, allow only necessary cookies");
        try {
            assert driver != null;
            WebElement button = driver.findElement(By.xpath("//button[text()='Neka valfria cookies']"));
            button.click();
        } catch (Exception e) {
            logger.error(e.getMessage());
            try {
                WebElement button = driver.findElement(By.xpath("//button[text()='Tillåt endast nödvändiga cookies']"));
                button.click();
            } catch (Exception error) {
                logger.error(e.getMessage());
            }
        }

        // Enter the email address and password, and click login
        logger.info("Using WebElements to input username and password, and click login");
        try {
            WebElement emailInput = driver.findElement(By.id("email"));
            emailInput.sendKeys(username);

            WebElement passwordInput = driver.findElement(By.id("pass"));
            passwordInput.sendKeys(password);

            WebElement loginButton = driver.findElement(By.name("login"));
            loginButton.click();
        } catch (NoSuchElementException e){
            logger.error(e.getMessage());
        }

        //Give the browser some time to log in
        Thread.sleep(7000);

        //Check if login has been successful
        if (driver.getCurrentUrl().equals("https://www.facebook.com/?sk=welcome")){
            logger.info("Login successful");
        } else{
            logger.info("Login unsuccessful");
        }

        // Close the browser
        driver.quit();
    }
}
