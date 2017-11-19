package com.cgi.code.testng;

import java.util.List;

import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.ExtentTest;

/**
 * Contains parameters used in most methods found in ActionKeywords
 * 
 * 
 */
public class ActionParams {
	private String pageObject;
	private String data;
	private WebDriver driver;
	private ExtentTest extentTest;
	private List<XMLParamInterface> testCaseXMLData;

	/**
	 * Used to store data used in the Methods found in ActionKeywords.java
	 * 
	 * @param pageObject 
	 * @param data Data to be passed to an action, for example the amount of seconds to wait.
	 * @param driver Current Selenium WebDriver
	 * @param extentTest current ExtenTest object
	 * @param testCaseXMLData Contains data from XML relevant to current test.
	 */
	public ActionParams(String pageObject, String data, WebDriver driver,
			ExtentTest extentTest, List<XMLParamInterface> testCaseXMLData) {
		this.pageObject = pageObject;
		this.data = data;
		this.driver = driver;
		this.extentTest = extentTest;
		this.testCaseXMLData = testCaseXMLData;
	}

	
	public String getPageObject() {
		return pageObject;
	}

	
	public void setPageObject(String pageObject) {
		this.pageObject = pageObject;
	}

	
	public String getData() {
		return data;
	}

	
	public void setData(String data) {
		this.data = data;
	}

	
	public WebDriver getDriver() {
		return driver;
	}

	
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	
	public ExtentTest getExtentTest() {
		return extentTest;
	}

	
	public void setExtentTest(ExtentTest extentTest) {
		this.extentTest = extentTest;
	}

	
	public List<XMLParamInterface> getTestCaseXMLData() {
		return testCaseXMLData;
	}

	
	public void setTestCaseXMLData(List<XMLParamInterface> testCaseXMLData) {
		this.testCaseXMLData = testCaseXMLData;
	}

}
