package com.controls;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Input : How are you ?
//		Output : woH era uoy ?
		
		String str="How are you ?";
		
		String [] words=str.split(" ");

		
		
		String output="";
		
		for(String word:words) {
			String reverse="";
			for(int i=word.length()-1;i>=0;i--) {
				reverse=reverse+word.charAt(i);
			}
			output=output+reverse+" ";
		}
		
		
		
		System.out.println(output);
		
		
		
		
	}

}
