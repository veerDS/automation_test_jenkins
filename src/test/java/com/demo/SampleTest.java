package com.demo;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

public class SampleTest {
	
	public ResponseFromPage responseFromPage;
	public String log="";
	
	public ResponseFromPage test(int value1,int value2) {
		if(value1>value2) {
			return new ResponseFromPage(true, "This testcase is passed");
		}
		else {
			return new ResponseFromPage(false, "This testcase is failed");
		}
	}
	
	public void log() {
		assertTrue(responseFromPage.isTrue(), responseFromPage.getMessage());
		log=logExtent(log,responseFromPage.getMessage());
	}
	
	private String logExtent(String log, String message) {
		System.out.println(message);
		String logMessage = log+"<br />"+message+"<br />";
		return logMessage;
	}
	
	@Test
	public void check() {
		responseFromPage=test(20,10);
		log();
	}
	
}
