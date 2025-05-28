package com.utils;

import java.io.IOException;

import org.testng.annotations.DataProvider;

public class DataAccess {
	
	@DataProvider(name="unicornSignup")
	public String [][] unicornSignup() throws IOException{
		return getDatabyPath(".\\TestData\\unicornSignup.xlsx");
	}
	@DataProvider(name="unicornSignupD",parallel=true)
	public String [][] unicornSignupD() throws IOException{
		return getDatabyPath(".\\TestData\\unicornSignupD.xlsx");
	}
	@DataProvider(name="data")
	public String [][] getData() throws IOException{
		return getDatabyPath(".\\TestData\\data.xlsx");
	}	
	@DataProvider (name="LoginData")
	public String[][] loginWithData() throws InterruptedException, Exception{
		return getDatabyPath(".\\TestData\\dealerLoginData.xlsx");
	}

	@DataProvider(name="requestQuoteFormData")
	public String [][] formData() throws IOException{
		return getDatabyPath(".\\TestData\\testData.xlsx");
	}	
	public String[][] getDatabyPath(String path) throws IOException{
		ExcelAccess xlutil=new ExcelAccess(path);
		int totalrows=xlutil.getRowCount("Sheet1");
		int totalcols=xlutil.getCellCount("Sheet1",1);	
		String data[][]=new String[totalrows][totalcols];
		for(int i=1;i<=totalrows;i++) { //1
			for(int j=0;j<totalcols;j++){ //0
				data[i-1][j]=xlutil.getCellData("Sheet1", i, j);
			}
		}
		return data;
		}
}
