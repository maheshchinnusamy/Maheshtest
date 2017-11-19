/*
 * Authors: Nickolas Reid
 * Date Last Modified: April 28th 2017
 * Purpose: 
 * The purpose of this file is to all of the methods or "action keywords" of the ActionKeywords object. This works by the ExcelTestData Object
 * reading in the TestSuite Excel file. ExcelTestData will store all of the rows for a selected test case(s) in memory. From here an ActionParams object
 * is created and depending on what action is in Excel's actionkeyword is, actionParams will call the appropriate action keyword method.
 */



package com.cgi.code.testng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
/**
 * Base class for performing ActionKeywords found in excel
 *
 */
public abstract class ActionKeywords {
	
	//Purpose of this file is to store multiple values for testing fields.
	String propFileName = "temp/tempVariables.properties";
	Properties prop = new Properties();
	
	/**
	 * Constructor
	 */
	public ActionKeywords() {

	}


	/***
	 * 
	 * @param objectName
	 * @param testCaseXMLData
	 * @return
	 */
	public String GetJS(String objectName, List<XMLParamInterface> testCaseXMLData) {
		String ret = null;
		
		for (int i = 0; i < (testCaseXMLData != null ? testCaseXMLData.size() : 0); i++) {
			if (testCaseXMLData.get(i).getAttribute().equals(objectName)) {
				ret = testCaseXMLData.get(i).getValue();
			}
		}
		if (ret == null) {
			System.out.print("EXCEPTION - " + objectName
					+ "' not defined in Object Repository!!");
		}
		return ret;		
	}

	/**
	 * Opens a browser, "mozilla" opens firefox, "ie" for Internet Explorer, and
	 * "chrome" for chrome.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 * @return WebDriver return the created WebDriver, possible options are 
	 * 		   InternetExplorerDriver, ChromeDriver, FirefoxDriver or null.
	 */
	public WebDriver OpenBrowser(ActionParams actionParams) {
		System.out.println("Opening Browser - " + actionParams.getData());
		String browserName = actionParams.getData().toLowerCase();
		WebDriver ret = null;
		try {
			if (browserName.equals("mozilla")) {
	
				ret = new FirefoxDriver();
				
				System.out.println("Mozilla browser started");
			} else if (browserName.equals("ie")) {

				ret = new InternetExplorerDriver();
				System.out.println("IE browser started");
			} else if (browserName.equals("chrome")) {

				ret = new ChromeDriver();
				System.out.println("Chrome browser started");
			}
			else
			{
				ret = null;
				System.out.println("No browser selected! Check Excel");
			}
			if (((RemoteWebDriver) ret).getSessionId() != null) {
				ret.manage().window().maximize();
			}

		} catch (Exception e) {
			System.out.println("Not able to open the Browser --- Check Excel " + e.getClass().getSimpleName());
			actionParams.getExtentTest().log(LogStatus.FAIL, "Not able to open the Browser --- " +
					e.getClass().getSimpleName());
		}
		return ret;
	}

	/**
	 * Takes in value from data set in excel and navigates to the url.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void NavigateURL(ActionParams actionParams) {
		try {
			//Logs where the application is trying to navigate to.
			System.out.println("Navigating to URL - " + actionParams.getData());
			//Grabs where the application needs to navigate to, and sends a get request to get the page data.
			actionParams.getDriver().get(actionParams.getData());
		} catch (Exception e) {
			//Logs that the url had a problem.
			System.out.println("Not able to navigate to URL - " + actionParams.getData() + 
					". Error message - " + e.getClass().getSimpleName());
			//Grabs the reporting tool and logs the failure
			actionParams.getExtentTest().log(LogStatus.FAIL,"Not able to navigate to URL - " + 
					actionParams.getData() + ". Error message - " + e.getClass().getSimpleName());
		}
	}

	/**
	 * Takes in a value from the dataset in TestSuite.xsl,
	 *  finds the item on the webpage and then performs a left mouse click.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void Click(ActionParams actionParams) {
		// This is the report object that generates a report file for a test run.
		ExtentTest extentTest = actionParams.getExtentTest();
		// This is a web driver to pull data from a page.
		WebDriver driver = actionParams.getDriver();
		// The page object refers to the Page Object column on the excel sheet.
		String pageObject = actionParams.getPageObject();

		try {
			//Log that a click is happening.
			System.out.println("Clicking on Webelement " + actionParams.getPageObject());
			//Identifies the element via xpath via the pageObject String
			driver.findElement(By.xpath(pageObject)).click();
			//Log into the report that the click was successful.
			extentTest.log(LogStatus.PASS, "Clicked on " + pageObject);
		} catch (Exception e) {
			//Log the error
			System.out.println("Not able to click --- " + e.getClass().getSimpleName());
			//log in the report that the click was not successful and why.
			extentTest.log(LogStatus.FAIL, "Unable to click on " + pageObject 
					+ " Exception :" + e.getClass().getSimpleName());
		}
	}
	/**
	 * Hover mouse on given page object.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void Hover(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		actionParams.getTestCaseXMLData();
		//WebDriverWait wait = new WebDriverWait(driver,5000);
		try {
			System.out.println("Hover on Webelement " + actionParams.getPageObject());
			Actions action = new Actions(driver);
			//Fix this
			WebElement webElement = driver.findElement(By.xpath(actionParams.getPageObject()));
			action.moveToElement(webElement).perform();
			extentTest.log(LogStatus.PASS, "Hover on " + pageObject);
		} catch (Exception e) {
			System.out.println("Not able to hover on --- " + e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Unable to hover on " + pageObject 
					+ " Exception :" + e.getClass().getSimpleName());
		}
	}
	/**
	 * Switch to another frame.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void SwitchToFrame(ActionParams actionParams) {
		WebDriver driver = actionParams.getDriver();
		String data = actionParams.getData();
		driver.switchTo().frame(data.trim());
	}

	/**
	 * Select a window.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void SwitchToWindow(ActionParams actionParams) {
		WebDriver driver = actionParams.getDriver();
		String data = actionParams.getData();
		driver.switchTo().window(data.trim());
	}
	
	
	/**
	 * Used to call a JavaScript method
	 * 
	 * @param actionParams 
	 */
	public void CallJscript(ActionParams actionParams) {
		String object = actionParams.getPageObject();
		String jScript = null;
		WebDriver driver = actionParams.getDriver();
		ExtentTest extentTest = actionParams.getExtentTest();
		List<XMLParamInterface> testCaseXMLData = actionParams.getTestCaseXMLData();
		try {
			System.out.println("Calling " + object);
			for (int i = 0; i < (testCaseXMLData != null ? testCaseXMLData.size() : 0); i++) {
				if (testCaseXMLData.get(i).getAttribute().equals(object)) {
					jScript = testCaseXMLData.get(i).getValue();
				}
			}
			if (driver instanceof JavascriptExecutor) {
				((JavascriptExecutor) driver).executeScript(jScript);
			}
		} catch (Exception e) {
			System.out.println("Unable to call " + object + " method. (" + e.getStackTrace() + ")");
			extentTest.log(LogStatus.FAIL, "Unable to call " + object + " method");
		}
	}

	/**
	 * Handle modal dialog pop up box. Code "yes" for == ok.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void HandleModalWindow(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String data = actionParams.getData();
		try {
			// Set argument in excel sheet to "yes" or "no" (yes=ok, no=cancel)
			if (data.toLowerCase().equals("yes")) {
				WebDriverWait wait = new WebDriverWait(driver, 10);
				Alert alert = wait.until(ExpectedConditions.alertIsPresent());
				alert.accept();
			} else {
				WebDriverWait wait = new WebDriverWait(driver, 10);
				Alert alert = wait.until(ExpectedConditions.alertIsPresent());
				alert.dismiss();
			}
		} catch (Exception e) {
			System.out.println("Unable to handle modal diaglog box. (" + "Argument=" + data + ")");
			extentTest.log(LogStatus.FAIL, "Modal diaglog box failed");
		}
	}

	/**
	 * Used to enter to enter text.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void EnterText(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {
			System.out.println("Entering the text in " + actionParams.getPageObject());
			driver.findElement(By.xpath(pageObject)).clear();
			driver.findElement(By.xpath(pageObject)).click();
			driver.findElement(By.xpath(pageObject)).sendKeys(data);

			extentTest.log(LogStatus.PASS, "Entered text " + data + "  on " + pageObject);
		} catch (Exception e) {
			System.out.println("Not able to Enter text in " + pageObject + " . Error Message  - " + 
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Unable to enter text on " + pageObject + 
					" Exception :" + e.getClass().getSimpleName());
		}
	}

	/**
	 * Used to select an item by visible text.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void SelectItemByVisibleText(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {
			System.out.println("Selecting " + data + " in " + pageObject);
			//Fix this
			Select selectObject = new Select(driver.findElement(By.xpath(actionParams.getPageObject())));
			selectObject.selectByVisibleText(data);
			extentTest.log(LogStatus.PASS, "Selected  '" + data + "'  on " + pageObject);
		} catch (Exception e) {
			System.out.println("Not able to select item in " + pageObject + " . Error Message  - " + 
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Unable to select item on " + pageObject + 
					" Exception :" + e.getClass().getSimpleName());
		}
	}
	
	/**
	 * Used to select an item by value.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void SelectItemByValue(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {
			System.out.println("Selecting " + data + " in " + pageObject);
			//Fix this
			Select selectObject = new Select(driver.findElement(By.xpath(actionParams.getPageObject())));
			selectObject.selectByValue(data);
			extentTest.log(LogStatus.PASS, "Selected  '" + data + "'  on " + pageObject);
		} catch (Exception e) {
			System.out.println("Not able to select item in " + pageObject + " . Error Message  - " + 
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Unable to select item on " + pageObject + 
					" Exception :" + e.getClass().getSimpleName());
		}
	}
	
	/**
	 * Used to select an item by index.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void SelectItemByIndex(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {
			System.out.println("Selecting " + data + " in " + pageObject);
			//Fix this
			Select selectObject = new Select(driver.findElement(By.xpath(actionParams.getPageObject())));
			selectObject.selectByIndex(Integer.parseInt(data));
			extentTest.log(LogStatus.PASS, "Selected index  '" + data + "'  on " + pageObject);
		} catch (Exception e) {
			System.out.println("Not able to select item in " + pageObject + " . Error Message  - " + 
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Unable to select item on " + pageObject + 
					" Exception :" + e.getClass().getSimpleName());
		}
	}

	/**
	 * Used to toggle a radio button from a group.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void SetRadioButtonFromGroup(ActionParams actionParams) {
		Boolean found = false;
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {
			System.out.println("Setting RadioButton " + pageObject);
			//Fix this 
			List<WebElement> radioButtonGroup = driver.findElements(By.xpath(actionParams.getPageObject()));
			for (WebElement element : radioButtonGroup) {
				if (element.getAttribute("value").equals(data)) {
					element.click();
					found = true;
				}
			}
			if (found) {
				extentTest.log(LogStatus.PASS, "Selected  '" + data + "'  on " + pageObject);
			} else {
				extentTest.log(LogStatus.FAIL, "Unable to find radio button on " + pageObject);
				System.out.println("Unable to find radio button on " + pageObject);
			}
		} catch (Exception e) {
			System.out.println("Not able to select item in " + pageObject + " . Error Message  - " +
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Unable to select item on " + pageObject + 
					" Exception :" + e.getClass().getSimpleName());
		}
	}

	/**
	 * Waits for an element to be clickable.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void WaitForElementToBeClickable(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {
			System.out.println("Clicking on Webelement " + pageObject);
			WebDriverWait wait = new WebDriverWait(driver, Integer.parseInt(actionParams.getData()));
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(pageObject)));
			actionParams.getExtentTest().log(LogStatus.PASS, "Found element " + pageObject);
		} catch (Exception e) {
			System.out.println("Unable to find element " + actionParams.getPageObject() + 
					" Exception :" + e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Unable to find element " + pageObject + 
					" Exception :" + e.getClass().getSimpleName());
		}
	}

	/**
	 * Closes the current browser.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void CloseBrowser(ActionParams actionParams) {
		try {
			System.out.println("Closing the browser");
			actionParams.getDriver().close();
			actionParams.getDriver().quit();
			actionParams.getExtentTest().log(LogStatus.INFO, "Browser closed");
		} catch (Exception e) {
			System.out.println("Not able to Close the Browser --- " + e.getClass().getSimpleName());
		}
	}

	public void WaitForElementToBeVisible(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		float fTime = Float.parseFloat(data);
		long time = (long)fTime;
		try {
			WebDriverWait wait = new WebDriverWait(driver, time);
			System.out.println("Waiting on Webelement " + pageObject);			
			wait.until(ExpectedConditions.visibilityOfElementLocated
					//Fix this
					(By.xpath(actionParams.getPageObject())));
			actionParams.getExtentTest().log(LogStatus.PASS, "Found element " + pageObject);
		} catch (Exception e) {
			System.out.println("Unable to find element " + actionParams.getPageObject() + 
					" Exception :" + e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Unable to find element " + pageObject + 
					" Exception :" + e.getClass().getSimpleName());
		}
	}
	
	/**
	 * Verifies correct object is displayed.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams}
	 */
	public void VerifyObjectDisplayed(ActionParams actionParams) {
		LogStatus status = LogStatus.FAIL;
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {
			System.out.println("Verify object displayed " + pageObject);
			//Fix this
			if (driver.findElement(By.xpath(actionParams.getPageObject())).isDisplayed()) {
				System.out.println(pageObject + " is displayed");
				status = LogStatus.PASS;
			} else {
				System.out.println(pageObject + " is not displayed");
			}
			extentTest.log(status, "Validate object " + pageObject + " is " + data + 
					extentTest.addScreenCapture(CreateScreenshot(driver)));
		} catch (Exception e) {
			System.out.println("Failed to verify " + pageObject + " . Error Message  - " +
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Validate object " + pageObject + " is " + data + 
					extentTest.addScreenCapture(CreateScreenshot(driver)));
		}
	}
	
	

	/**
	 * Used to validate text on page.
	 * 
	 * @param actionParams 
	 */
	public void ValidateText(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {						
			System.out.println("Verify text displayed on " + pageObject + " is " + data);
			//Fix this
			String s = actionParams.getDriver().findElement(By.xpath(actionParams.getPageObject()))
					.getText();
			if (s.equals(data)) {
				System.out.println(data + " message is displayed");
				extentTest.log(LogStatus.PASS, "Validate Text on " + pageObject + " is " + 
						data + extentTest.addScreenCapture(CreateScreenshot(driver)));
			} else {
				System.out.println(data + " message is not displayed");
				extentTest.log(LogStatus.FAIL, "Validate Text on " + pageObject + " is Not " + 
						data +" actual text is " + s + extentTest.addScreenCapture(CreateScreenshot(driver)));
			}
		} catch (Exception e) {
			System.out.println("Failed to verify " + pageObject + " . Error Message  - " + 
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Failed to verify " + pageObject + 
					" . Error Message  - " + e.getClass().getSimpleName());
		}
	}
	public void ValidatePartialText(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try {						
			System.out.println("Verify text displayed on " + pageObject + " is " + data);
			//fix this
			String s = actionParams.getDriver().findElement(By.xpath(actionParams.getPageObject()))
					.getText();
			if (s.contains(data)) {
				System.out.println(data + " message is displayed");
				extentTest.log(LogStatus.PASS, "Validate Text on " + pageObject + " is " + 
						data + extentTest.addScreenCapture(CreateScreenshot(driver)));
			} else {
				System.out.println(data + " message is not displayed");
				extentTest.log(LogStatus.FAIL, "Validate Text on " + pageObject + " is Not " + 
						data +" actual text is " + s + extentTest.addScreenCapture(CreateScreenshot(driver)));
			}
		} catch (Exception e) {
			System.out.println("Failed to verify " + pageObject + " . Error Message  - " + 
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Failed to verify " + pageObject + 
					" . Error Message  - " + e.getClass().getSimpleName());
		}
	}
	// Validate String in Title
	public void ValidateTitle(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		
		String data = actionParams.getData();
		try {						
			System.out.println("Verify title displayed is " + data);
			String s = driver.getTitle();
			if (s.contains(data)) {
				System.out.println(data + " message is displayed");
				extentTest.log(LogStatus.PASS, "Validate title displayed is " + 
						data + extentTest.addScreenCapture(CreateScreenshot(driver)));
			} else {
				System.out.println(data + " message is not displayed");
				extentTest.log(LogStatus.FAIL, "Validate tile is " + 
						data + extentTest.addScreenCapture(CreateScreenshot(driver)));
			}
		} catch (Exception e) {
			System.out.println("Failed to verify title. Error Message  - " + 
					e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Failed to verify title"  + 
					" . Error Message  - " + e.getClass().getSimpleName());
		}
	}
	
	//Sikuli Image Click
	public void ClickImage(ActionParams actionParams){
		
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String[] image = actionParams.getData().split(",",2);
		try{
			System.out.println("Clicking on " + image[0]);
			
			System.out.println(image[0]+ "   "+ image[1]);
			Screen s = new Screen();
			String location = "C://Selenium Framework/Selenium Framework Oct14/Keyword Framework/Image Repository/";
			String path = location + image[0] + ".png";
			s.wait(path, 300);
			Iterator<Match>matches = s.findAll(path);
	        int imageNum = Integer.parseInt(image[1]);
	        for(int i = 0;matches.hasNext() && (i <= imageNum) ;i++){
	        	System.out.println(i);
	        	if(i == imageNum){
	        		s.click(matches.next());
	        		System.out.println(image[0] + " Image is displayed");
	        		extentTest.log(LogStatus.PASS, "Image " + image[0] + " found " + 
							extentTest.addScreenCapture(CreateScreenshot(driver)));
	        	}else{
	        		System.out.println(matches.next());
	        	}
	        }
   
	        TimeUnit.SECONDS.sleep(5);
		} catch (Exception e){
			System.out.println("Not able to find the image "+ image[0]);
			extentTest.log(LogStatus.FAIL, "Image " + image[0] + " not found " + 
					 extentTest.addScreenCapture(CreateScreenshot(driver)));
			actionParams.getExtentTest().log(LogStatus.FAIL, "Unable to click image " 
					+ image[0]);
		}
	}
	
	//Sikuli Image Validate
	public void ValidateImage(ActionParams actionParams){
		String[] image = actionParams.getData().split(",",2);
		try{
			System.out.println("Found image " + image[0]);
			
			System.out.println(image[0]+ "   "+ image[1]);
			Screen s = new Screen();
			String location = "C://Selenium Framework Oct14/Keyword Framework/Image Repository/";
			String path = location + image[0] + ".png";
			s.wait(path, 60);
			Iterator<Match>matches = s.findAll(path);
		       int imageNum = Integer.parseInt(image[1]);
		       for(int i = 0;matches.hasNext() && (i <= imageNum) ;i++){
		       	System.out.println(i);
		       	if(i == imageNum){
		       		//System.out.println("ImageFound");
		       		System.out.println("ImageFound");
		       	}else{
		       		System.out.println(matches.next());
		       	}
		       }
		       //s.click(path, imageNum);    
		       TimeUnit.SECONDS.sleep(5);
		} catch (Exception e){
			System.out.println("Not able to find the image "+ image[0]);
			actionParams.getExtentTest().log(LogStatus.FAIL, "Unable to find image " 
					+ image[0]);
		}
	}
	//Sikuli any image in given folder
	public void clickAnyImage(ActionParams actionParams){
		String folderName = actionParams.getData();
		try{
			
			System.out.println("Found image from" + folderName +" folder" );
			
			System.out.println();
			Screen s = new Screen();
			String location = "C://Selenium Framework Oct14/Keyword Framework/Image Repository/";
			String path = location + folderName + "/";
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			for(int i= 0; i < listOfFiles.length;i++){
				String imageName = listOfFiles[i].getName();
				System.out.println(imageName);
				imageName = path + imageName;
				s.wait(imageName, 60);
				if(s.exists(imageName)!= null){
					
					s.click(imageName,0);
					i = listOfFiles.length + 1;
				}
			}
			//s.click(path, imageNum);    
		     TimeUnit.SECONDS.sleep(5);
		} catch (Exception e){
			System.out.println("Not able to find any image from " + folderName + " folder");
			actionParams.getExtentTest().log(LogStatus.FAIL, "Unable to find image " );
		}
	}
	//wait for given time
	public void Wait(ActionParams actionParams){
		int time = Integer.parseInt(""+actionParams.getData());
		ExtentTest extentTest = actionParams.getExtentTest();
		try{
			System.out.println("Waiting for "+ time + " seconds" );	
			TimeUnit.SECONDS.sleep(time);
		} catch (Exception e){
			System.out.println("Unable to wait");
			extentTest.log(LogStatus.FAIL, "Not able to wait");
		}
	}

	//Sikuli Image Click
	public void waitForImage(ActionParams actionParams){
		String[] image = actionParams.getData().split(",",2);
		try{
			System.out.println("Clicking on " + image[0]);
			
			System.out.println(image[0]+ "   "+ image[1]);
			Screen s = new Screen();
			String location = "C://Selenium Framework Oct14/Keyword Framework/Image Repository/";
			String path = location + image[0] + ".png";
			int time = Integer.parseInt(image[1]);
			s.wait(path, time);
			
	        TimeUnit.SECONDS.sleep(5);
		} catch (Exception e){
			System.out.println("Not able to find the image "+ image[0]);
			actionParams.getExtentTest().log(LogStatus.FAIL, "Unable to find image " 
					+ image[0]);
		}
	}
	//Sikuli enter text on Image
	public void typeOnImage(ActionParams actionParams){
		String[] image = actionParams.getData().split(",",2);
		try{
			System.out.println("Clicking on " + image[0]);
			
			System.out.println(image[0]+ "   "+ image[1]);
			Screen s = new Screen();
			String location = "C://Selenium Framework Oct14/Keyword Framework/Image Repository/";
			String path = location + image[0] + ".png";
			
			s.wait(path, 60);
			s.click(path, 0);
			s.paste(image[1]);
			//s.type(null,,0);
	        TimeUnit.SECONDS.sleep(5);
		} catch (Exception e){
			System.out.println("Not able to find the image "+ image[0]);
			actionParams.getExtentTest().log(LogStatus.FAIL, "Unable to click image " 
					+ image[0]);
		}
	}
	
	//Set temporary stored property
	public void setValueIn(ActionParams actionParams){
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try{
			//Fix this
			String s = actionParams.getDriver().findElement(By.xpath(actionParams.getPageObject()))
					.getText();
			FileInputStream in = new FileInputStream(propFileName);
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			
			prop.setProperty(data, s);
			FileOutputStream out = new FileOutputStream(propFileName);
			prop.store(out,null);
			out.close();
		}catch (Exception e){
			System.out.println(e);
			actionParams.getExtentTest().log(LogStatus.FAIL, "Not able to save value of " 
					+ pageObject);
		}
	}
	
	//Set temporary dollar value
	public void setDollarValueIn(ActionParams actionParams){
		actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try{
			//Fix this
			String s = actionParams.getDriver().findElement(By.xpath(actionParams.getPageObject()))
					.getText();
			s = s.replace("$", "");
			s = s.replace(",", ".");
			s = s.replace(" ", "");
			FileInputStream in = new FileInputStream(propFileName);
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			
			prop.setProperty(data, s);
			FileOutputStream out = new FileOutputStream(propFileName);
			prop.store(out,null);
			out.close();
		}catch (Exception e){
			System.out.println(e);
			actionParams.getExtentTest().log(LogStatus.FAIL, "Not able to save dollar value of " 
					+ pageObject);
		}
	}
	//Compare value of page object is equal to temporarily stored value
	public void validateValueIn(ActionParams actionParams){
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		actionParams.getTestCaseXMLData();
		try{
			//Fix this
			String s = actionParams.getDriver().findElement(By.xpath(actionParams.getPageObject()))
					.getText();
						
			FileInputStream in = new FileInputStream(propFileName);
			Properties prop = new Properties();
			prop.load(in);
			if(s.equals(prop.getProperty(data))){
				System.out.println(data + " is " + s);
				extentTest.log(LogStatus.PASS, "Validated " +data +"value on" + pageObject 
						+ extentTest.addScreenCapture(CreateScreenshot(driver)));
			} else {
				System.out.println(data + " is not correct value");
				extentTest.log(LogStatus.FAIL, "Validated " +data +"value on" + pageObject 
						+ extentTest.addScreenCapture(CreateScreenshot(driver)));
			}
		}catch (Exception e){
			System.out.println(e);
			actionParams.getExtentTest().log(LogStatus.FAIL, "Not able to validate value of " 
					+ pageObject);
		}
	}
	
	//Compare Dollar value of page Object with the temporarily stored value
	public void validateDollarValueIn(ActionParams actionParams){
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String pageObject = actionParams.getPageObject();
		String data = actionParams.getData();
		String k = null;
		String[] d;
		if(data.contains("-")){
			d = data.split("-");
			k = "subtract";
		}else if(data.contains("+")){
			d = data.split("+");
			k = "addition";
		}else if(data.contains("/")){
			d = data.split("/");
			k= "division";
		}else if(data.contains("*")){
			d = data.split("*");
			k = "multiplication"; 
		}else{
			d = new String[1];
			d[0] = data;
		}
		
		actionParams.getTestCaseXMLData();
		try{
			
			//Fix this
			String s = actionParams.getDriver().findElement(By.xpath(actionParams.getPageObject()))
					.getText();
			s = s.replace("$", "");
			s = s.replace(",", ".");
			s = s.replace(" ", "");
			float currentValue = Float.parseFloat(s);
			float expectedValue;
			
			FileInputStream in = new FileInputStream(propFileName);
			Properties prop = new Properties();
			prop.load(in);
			d[0] = prop.getProperty(d[0]);
			if(k.equals("subtract")){
				float num1 = Float.parseFloat(d[0]);
				float num2 = Float.parseFloat(d[1]);
				expectedValue = num1 - num2;
			}else if(k.equals("addition")){
				float num1 = Float.parseFloat(d[0]);
				float num2 = Float.parseFloat(d[1]);
				expectedValue = num1 + num2;
			}else if(k.equals("division")){
				float num1 = Float.parseFloat(d[0]);
				float num2 = Float.parseFloat(d[1]);
				expectedValue = num1 / num2;
			}else if(k.equals("multiplication")){
				float num1 = Float.parseFloat(d[0]);
				float num2 = Float.parseFloat(d[1]);
				expectedValue = num1 * num2;
			}else{
				expectedValue = Float.parseFloat(d[0]);
			}
			
			if(currentValue == expectedValue){
				System.out.println(data + " is " + s);
				extentTest.log(LogStatus.PASS, "Validated " +data +" value on" + pageObject 
					+" is "	+ s + extentTest.addScreenCapture(CreateScreenshot(driver)));
			} else {
				System.out.println(data + " is not correct value");
				extentTest.log(LogStatus.FAIL, "Validated " +data +" value on" + pageObject 
						+" is "+ s + extentTest.addScreenCapture(CreateScreenshot(driver)));
			}
			
		}catch (Exception e){
			System.out.println(e);
			actionParams.getExtentTest().log(LogStatus.FAIL, "Not able to validate dollar value of " 
					+ pageObject);
		}
	}
	//ScrollUp or ScrollDown with given value
		public void scroll(ActionParams actionParams){
			WebDriver driver = actionParams.getDriver();
			JavascriptExecutor jse = (JavascriptExecutor)driver;
			
			String scrollValue = actionParams.getData();
			
			String scroll = "window.scroll(0," + scrollValue +")";
			jse.executeScript(scroll, "");
		}
	/**
	 * Takes a screenshot of any errors and saves it to "...Test
	 * Reports\Screenshots"
	 * 
	 * @param driver Current Selenium WebDriver             
	 * @return file path and name of image.
	 */
	public String CreateScreenshot(WebDriver driver) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd'_'hh:mm:ss");
		String formattedDate = sdf.format(date);
		formattedDate = formattedDate.replaceAll(":", "");
		String imageFileName = System.getProperty("user.dir") + "/Test Reports/Screenshots/" +
				"Screenshot_" + formattedDate + ".png";
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			// copy file object to designated location
			FileUtils.copyFile(scrFile, new File(imageFileName));
		} catch (IOException e) {
			System.out.println("Error while generating screenshot:\n" + e.toString());
		}
		return imageFileName;
	}
	

	public void DropDown(ActionParams actionParams){
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		try{

		Select dropdown = new Select(driver.findElement(By.xpath(actionParams.getPageObject())));
		//dropdown.selectByValue(actionParams.getData());
		dropdown.selectByVisibleText(actionParams.getData());
		WebElement we = dropdown.getFirstSelectedOption();
		System.out.println("DROP DOWN IS ACTIVATED"); 
		
	
			System.out.println(actionParams.getData() + " is " + we.getText());
			extentTest.log(LogStatus.PASS, "Validated " + we.getText() +" value on" + actionParams.getPageObject()
				+" is "	+ we.getText() + extentTest.addScreenCapture(CreateScreenshot(driver)));
		} catch(Exception e) {
			System.out.println(actionParams.getData() + " is not correct value");
			extentTest.log(LogStatus.FAIL, "Validated " +actionParams.getData() +" value on" + actionParams.getPageObject()
					+" is "+ actionParams.getData() + extentTest.addScreenCapture(CreateScreenshot(driver)));
		}

	}
	
	public void ScreenShot(ActionParams actionParams){
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		System.out.println("Captured Result - Ending Test");
		extentTest.log(LogStatus.PASS, "Validated  (test) "+ extentTest.addScreenCapture(CreateScreenshot(driver)));
		
	}
	
	public String SwitchTab(ActionParams actionParams){
		WebDriver driver = actionParams.getDriver();
        String currentWindow = driver.getWindowHandle();
        
        for (String handle : driver.getWindowHandles()) {
       	    if (!handle.equals(currentWindow)) {
       	        driver.switchTo().window(handle);
       	    }
       	}
		return "";
	}
	
	public void SimpleLanguageCheck(ActionParams actionParams){
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		
		try{
			String value = driver.findElement(By.tagName("HTML")).getAttribute("lang");
		if(value.equals(actionParams.getData())){
			System.out.println("Language IS english");
		extentTest.log(LogStatus.PASS, "Validated  LANGUAGE "+ extentTest.addScreenCapture(CreateScreenshot(driver)));
		}else{
			//extentTest.log(LogStatus.FAIL, "Validated  LANGUAGE "+ extentTest.addScreenCapture(CreateScreenshot(driver)));
			System.out.println("Language IS NOT english");
		}}catch(Exception e){
			//extentTest.log(LogStatus.FAIL, "Validated  LANGUAGE "+ extentTest.addScreenCapture(CreateScreenshot(driver)));
			System.out.println("Language something went wrong");
		}
	}
		

	}
	

