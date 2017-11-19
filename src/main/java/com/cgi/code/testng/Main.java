package com.cgi.code.testng;

import java.util.List;

import org.testng.TestNG;
import org.testng.collections.Lists;

/**
 * Entry point to the application, you can pass a numeric argument to run the
 * test(s) in a loop for a specified amount of seconds (note that test suite
 * will not stop when the timer stops, when the timer stops the program will not
 * start a new suite).
 * 
 * 
 */
public class Main {
	public static void main(String[] args) {
		if (args != null) {
			// default is one second, this will cause the tests to only be run once.
			args = new String[] { "1" };
		}
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		suites.add(System.getProperty("user.dir") + "//XML//testng.xml");
		testng.setTestSuites(suites);
		final long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime) < (Integer.parseInt(args[0])*1000)) {
			testng.run();
		}
	}
}
