package com.cgi.code.testng;

/**
 * Used to hold data when reading a sheet in the excel document.
 * 
 */
public class ExcelTestParams {
	private String id;
	private String testStep;
	private String pageObject;
	private String action;
	private String data;
	
	/**
	 * Parameters used to map test steps used in an excel sheet.
	 * @param id The name of the test case.
	 * @param testStep The step number, at the moment this is cosmetic.
	 * @param pageObject 
	 * @param action used to identify which method we call from
	 *            {@link com.cgi.code.testng.ActionKeywords}
	 * @param data Input passed to a method in {@link com.cgi.code.testng.ActionKeywords}
	 */
	public ExcelTestParams(String id, String testStep, String pageObject,
			String action, String data) {
		this.id = id;
		this.testStep = testStep;
		this.pageObject = pageObject;
		this.action = action;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTestStep() {
		return testStep;
	}

	public void setTestStep(String testStep) {
		this.testStep = testStep;
	}

	public String getPageObject() {
		return pageObject;
	}

	public void setPageObject(String pageObject) {
		this.pageObject = pageObject;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
