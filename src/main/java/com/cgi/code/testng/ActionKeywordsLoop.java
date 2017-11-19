package com.cgi.code.testng;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

/**
 * ALC specific class for performing ActionKeywords found in xml/excel
 * 
 */
public class ActionKeywordsLoop extends ActionKeywords {
	private static final Logger logger = Logger.getLogger(ActionKeywordsLoop.class.getName());

	/**
	 * Used to pick numbers in a loop.
	 * 
	 * @param actionParams {@link com.cgi.code.testng.ActionParams} 
	 */
	public void LottoLoop(ActionParams actionParams) {
		ExtentTest extentTest = actionParams.getExtentTest();
		WebDriver driver = actionParams.getDriver();
		String data = actionParams.getData();
		String pageObject = actionParams.getPageObject();
		String[] numbers = data.split("\\|");
		List<XMLParamInterface> testCaseXMLData = actionParams.getTestCaseXMLData();
		
		try {
			logger.info("Lottery number selection function");
			String baseID = "";
			for (int i = 0; i < (testCaseXMLData != null ? testCaseXMLData.size() : 0); i++) {
				if (testCaseXMLData.get(i).getAttribute().equals(pageObject)) {
					baseID = testCaseXMLData.get(i).getValue();
				}
			}
						
			for(String number: numbers){
				driver.findElement(By.id(baseID + number)).click();
			}
			extentTest.log(LogStatus.PASS, "Numbers picked successfuly." );
		} catch (Exception e) {
			logger.error("Loop failed --- " + e.getClass().getSimpleName());
			extentTest.log(LogStatus.FAIL, "Error picking numbers " + pageObject + 
					"Exception :" + e.getClass().getSimpleName());
		}
	}

}
