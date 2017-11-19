package com.cgi.code.testng;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.AssertJUnit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.NetworkMode;

/**
 * This is the main entry point.
 * 
 */
public class CallActionKeywords  {

	private static final Logger logger = Logger.getLogger(CallActionKeywords.class.getName());
	private List<WebDriver> drivers;
	private ExtentReports extent;
	//Holds test case steps
	private Map<String, List<ExcelTestParams>> testCaseMap;
	private Map<String, List<ExcelTestParams>> functionMap;
	private String propFileName = "temp/tempVariables.properties";
	// Constructor is private because it is only for local use
	private CallActionKeywords() {
	}

	/**
	 * This returns each row of data from the "Test Suite" sheet as a set of
	 * strings which then are passed to a an instance of the constructor.
	 * 
	 * @return collection of strings to the test.
	 */
	@DataProvider(name = "name")
	public static Iterator<Object[]> TestData() {
		Collection<Object[]> ret = new ArrayList<Object[]>();
		double[] x = range();
		
		/*
		 * This is where the application decides which test cases to run.
		 */
		Collection<Object[]> readExcel = new ReadExcel("Test Suite").getData();
		for (Object[] testCase : readExcel) {

			if(!String.valueOf(testCase[2]).equals("Runmode")){
				if(Double.parseDouble((String)testCase[2]) >= x[0] && Double.parseDouble((String)testCase[2]) <= x[1]){
						ret.add(testCase);
				}
			}
		}
		
		
		return ret.iterator();
	}

	/**
	 * Setup before TestNG cases are run.
	 */
	@SuppressWarnings("deprecation")
	@BeforeClass
	public void BeforeClass() {
		DOMConfigurator.configure(System.getProperty("user.dir") + "//XML//log4j.xml");
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + 
				"/Binaries/chromedriver.exe");
		System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "/Binaries/IEDriverServer.exe");
		System.setProperty("webdriver.firefox.bin","C:\\Program Files\\Mozilla Firefox\\firefox.exe");
		drivers = new ArrayList<WebDriver>();
		
		// grab the test case data
		testCaseMap = getTestCaseMap("Test Cases");
		functionMap = getTestCaseMap("Function");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd'_'hh:mm:ss");
		String formattedDate = sdf.format(date);
		formattedDate = formattedDate.replaceAll(":", "");

		// Initiate Custom Reporting
		String ReportFile = "TestSuite" + formattedDate;
		extent = new ExtentReports(System.getProperty("user.dir") + "//Test Reports//" + 
				ReportFile + ".html", true, NetworkMode.OFFLINE);

		extent.config().reportHeadline(
				"<b>Test Report for ALC Regression Tests </b>");
		extent.config().reportHeadline("ALC REGRESSION RESULT");
		String css = "#topbar { background-color: #8bb1ec; }"
				+ ".topbar-items-right span { color: white; }"
				+ ".menu span { color: darkgreen; }"
				+ ".menu-item-selected span { border-bottom: 1px solid green; }"
				+ "#dashboard { background-color: transparent; }"
				+ ".test { border: 1px solid lightseagreen; }"
				+ ".description { background-color: transparent; border-left: 2px solid orange; "
				+ "padding: 2px 15px;}"
				+ ".name { color: darkgreen; }"
				+ ".extent-table { border: 1px solid #bbb; }"
				+ ".extent-table th { background: none repeat scroll 0 0 olivedrab; color: #fff; }"
				+ ".extent-table td { border-bottom: 1px solid #bbb; }";
		
		extent.config().addCustomStylesheet(css);
	}

	/**
	 * Loops through all the test steps.
	 * 
	 * @param testCaseXML The node where the data needed for tests is kept in the XML.
	 * @param testCaseID The name of the current test. 
	 * @param runmode Determines whether or not to run the test, if the text reads "Yes" in 
	 * 		the excel sheet then the test will run.
	 * @param description A short description of what the test does.
	 */
	@Test(dataProvider = "name")
	public void DoTest(String testCaseXML, String testCaseID, String runmode, String description) {
	
			// start the report for the current test
			ExtentTest extentTest = extent.startTest(testCaseID);
			startTestCase(testCaseID);
			
			//Grab the test steps
			List<ExcelTestParams> testSteps = testCaseMap.get(testCaseID);
			List<XMLParamInterface> testXML = null;
			for (ExcelTestParams testStep : testSteps) {
				if (testStep.getAction().toLowerCase().equals("function")) {
					List<ExcelTestParams> funcSteps = functionMap.get(testStep.getData());
					for (ExcelTestParams fs : funcSteps) {
						DoStep(fs, extentTest, testXML);
					}
				} else {
					DoStep(testStep, extentTest, testXML);
				}
			}
			closePropertyFile();
			extent.endTest(extentTest);
		
	}
	private void closePropertyFile(){
		try{
			FileInputStream in = new FileInputStream(propFileName);
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			Set<Object> keys = prop.keySet();
			for(Object k:keys){
				prop.remove(k);
			}
			FileOutputStream out = new FileOutputStream(propFileName);
			prop.store(out,null);
			out.close();
		}catch (Exception e){
			System.out.println(e);
		}
	}
	// Used to get a hash map of all the test cases.
	private Map<String, List<ExcelTestParams>> getTestCaseMap(String sheet) {
		Map<String, List<ExcelTestParams>> ret = new HashMap<String, List<ExcelTestParams>>();
		Collection<Object[]> testCases = new ReadExcel(sheet).getData();

		String testName = "";
		List<ExcelTestParams> testParams = new ArrayList<ExcelTestParams>();
		for (Object[] testCase : testCases) {
			int i = 0;
			if (testName.equals("")) {
				// first column will be the name of the test case/function
				testName = String.valueOf(testCase[0]);
			}
			if (!testName.equals(String.valueOf(testCase[0]))) {
				testName = String.valueOf(testCase[0]);
				testParams = new ArrayList<ExcelTestParams>();
			}
			
			
			/*
			 * This is where the excel test case values are stored for testing.
			 */
			testParams.add(new ExcelTestParams(String.valueOf(testCase[i++]),
					String.valueOf(testCase[i++]), 
					String.valueOf(testCase[i++]), 
					String.valueOf(testCase[i++]), 
					String.valueOf(testCase[i++])));
			ret.put(String.valueOf(testCase[0]), testParams);
		}
		return ret;
	}

	/**
	 * Contains keywords that map Selenium actions/methods in ActionKeywords.
	 * 
	 */
	public enum Actions {
		LOTTOLOOP, OPENBROWSER, NAVIGATEURL, ENTERTEXT, SELECTITEMBYVISIBLETEXT, 
		SELECTITEMBYVALUE, CLOSEBROWSER, VALIDATETEXT, SELECTITEMBYINDEX, 
		VERIFYOBJECTDISPLAYED, CLICK, WAITFORELEMENTTOBECLICKABLE, HANDLEMODALWINDOW, 
		CALLJSCRIPT, SETRADIOBUTTONFROMGROUP, SWITCHTOFRAME, SWITCHTOWINDOW, WAITFORELEMENTTOBEVISIBLE, 
		CLICKIMAGE, VALIDATEIMAGE, WAITFORIMAGE, TYPEONIMAGE, CLICKANYIMAGE, WAIT, SETVALUEIN, 
		SETDOLLARVALUEIN, VALIDATEVALUEIN, VALIDATEDOLLARVALUEIN, SCROLL, VALIDATETITLE, HOVER,VALIDATEPARTIALTEXT,DROPDOWN,
		SWITCHTAB, SCREENSHOT, SIMPLELANGUAGECHECK
	}

	private void DoStep(ExcelTestParams testParams, ExtentTest extentTest,
			List<XMLParamInterface> testXML) {

		ActionParams actionParams = new ActionParams(
				testParams.getPageObject(), testParams.getData(),
				drivers.size() > 0 ? drivers.get(drivers.size() - 1) : null,
				extentTest, testXML);

		ActionKeywordsLoop actionKeywords = new ActionKeywordsLoop();
		switch (Actions.valueOf(testParams.getAction().toUpperCase())) {
		case LOTTOLOOP:
			actionKeywords.LottoLoop(actionParams);
			break;
		case OPENBROWSER:
			drivers.add(actionKeywords.OpenBrowser(actionParams));
			break;
		case NAVIGATEURL:
			actionKeywords.NavigateURL(actionParams);
			break;
		case ENTERTEXT:
			actionKeywords.EnterText(actionParams);
			break;
		case SELECTITEMBYVISIBLETEXT:
			actionKeywords.SelectItemByVisibleText(actionParams);
			break;
		case SELECTITEMBYVALUE:
			actionKeywords.SelectItemByValue(actionParams);
			break;
		case SETRADIOBUTTONFROMGROUP:
			actionKeywords.SetRadioButtonFromGroup(actionParams);
			break;
		case CLOSEBROWSER:
			actionKeywords.CloseBrowser(actionParams);
			break;
		case VALIDATETEXT:
			actionKeywords.ValidateText(actionParams);
			break;
		case SELECTITEMBYINDEX:
			actionKeywords.SelectItemByIndex(actionParams);
			break;
		case VERIFYOBJECTDISPLAYED:
			actionKeywords.VerifyObjectDisplayed(actionParams);
			break;
		case CLICK:
			actionKeywords.Click(actionParams);
			break;
		case HOVER:
			actionKeywords.Hover(actionParams);
			break;
		case WAITFORELEMENTTOBECLICKABLE:
			actionKeywords.WaitForElementToBeClickable(actionParams);
			break;
		case HANDLEMODALWINDOW:
			actionKeywords.HandleModalWindow(actionParams);
			break;
		case CALLJSCRIPT:
			actionKeywords.CallJscript(actionParams);
			break;
		case SWITCHTOFRAME:
			actionKeywords.SwitchToFrame(actionParams);
			break;
		case SWITCHTOWINDOW:
			actionKeywords.SwitchToWindow(actionParams);
			break;
		case WAITFORELEMENTTOBEVISIBLE:
			actionKeywords.WaitForElementToBeVisible(actionParams);
			break;
		case CLICKIMAGE:
			actionKeywords.ClickImage(actionParams);
			break;
		case VALIDATEIMAGE:
			actionKeywords.ValidateImage(actionParams);
			break;
		case WAITFORIMAGE:
			actionKeywords.waitForImage(actionParams);
			break;
		case TYPEONIMAGE:
			actionKeywords.typeOnImage(actionParams);
			break;
		case CLICKANYIMAGE:
			actionKeywords.clickAnyImage(actionParams);
			break;
		case WAIT:
			actionKeywords.Wait(actionParams);
			break;
		case SETVALUEIN:
			actionKeywords.setValueIn(actionParams);
			break;
		case SETDOLLARVALUEIN:
			actionKeywords.setDollarValueIn(actionParams);
			break;
		case VALIDATEVALUEIN:
			actionKeywords.validateValueIn(actionParams);
			break;
		case VALIDATEDOLLARVALUEIN:
			actionKeywords.validateDollarValueIn(actionParams);
			break;
		case SCROLL:
			actionKeywords.scroll(actionParams);
			break;
		case VALIDATETITLE:
			actionKeywords.ValidateTitle(actionParams);
			break;
		case VALIDATEPARTIALTEXT:
			actionKeywords.ValidatePartialText(actionParams);
			break;
		case DROPDOWN:
			actionKeywords.DropDown(actionParams);
			break;
		case SWITCHTAB:
			actionKeywords.SwitchTab(actionParams);
			break;
		case SCREENSHOT:
			actionKeywords.ScreenShot(actionParams);
			break;
		case SIMPLELANGUAGECHECK:
			actionKeywords.SimpleLanguageCheck(actionParams);
			break;
		default:
			extentTest.log(LogStatus.FAIL,
					"Keyword  '" + testParams.getAction()
							+ "'  not found, check excel workbook.");
			logger.error("Unable to find action " + testParams.getAction()
					+ " check excel workbook." + actionParams.getPageObject());
			break;
		}
		if (extentTest.getRunStatus() == LogStatus.FAIL) {
			actionKeywords.CloseBrowser(actionParams);
			extent.endTest(extentTest);
		}
		AssertJUnit.assertNotSame(extentTest.getRunStatus(), LogStatus.FAIL);
	}

	/**
	 * Used to input start of test case to log4j log.
	 * 
	 * @param testCaseName The name of the current test.
	 */
	private void startTestCase(String testCaseName) {

		
		System.out.println("****************************************************************************************");
		System.out.println("****************************************************************************************");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$                 " + testCaseName
				+ "       $$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("****************************************************************************************");
		System.out.println("****************************************************************************************");
	}

	/**
	 * If the WebDriver is still alive then destroy it, write the report.
	 * 
	 */
	@AfterClass
	public void TearDown() {
		extent.flush();
		for (WebDriver driver : drivers) {
			if (((RemoteWebDriver)driver).getSessionId() != null) {
				try {
					System.out.println("Disposing WebDriver");					
					driver.quit();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					// try catch for FireFox
				}
			}
		}
	}
	
	public static double[] range(){
		try{
        String lowerbound = JOptionPane.showInputDialog("Execute From: ");

        String upperbound = JOptionPane.showInputDialog("Execute To: ");
        
        double int1 = Integer.parseInt(lowerbound);
        double int2 = Integer.parseInt(upperbound);
        if(int1 > int2){
        	JOptionPane.showMessageDialog(null, "Ensure the lower bound is smaller than the upper bound.");
        	range();
        }else{
        	return new double[] {int1,int2};
        }

        }
        catch(NumberFormatException nfe){
        	JOptionPane.showMessageDialog(null, "Please only place numeric values");
        	range();
        }
		return null;
	
    
        
	}


}
