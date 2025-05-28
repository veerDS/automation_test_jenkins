package com.demo;

import java.util.HashMap;
import java.util.Map;

public class Automation {

	public static void main(String[] args) {
		String str="welcome to java";
		// Emoclew ot avaj
		
		String [] words=str.split(" ");
		
		String output="";
		
		int count=0;
		
		for(String word:words) {
			
			String reverse="";
			
			for(int i=word.length()-1;i>=0;i--) {
				reverse=reverse+word.charAt(i);
			}
			output=output+reverse+" ";
		}

		System.out.println(output.trim());
		
//		for (int i = 0; i < output.length(); i++) {
//			if(output.contains(" ")) {
//				count++;
//				
//			}
//		}
//		System.out.println("Occurrance of spaces : "+count);
		
//		Map<Character,Integer> map=new HashMap<Character,Integer>();
//		char [] chars=output.toCharArray();
//		for(char ch:chars) {
//			if(!map.containsKey(Character.isSpace(' '))) {
//				map.put(' ', 1);
//			}
//			else {
//				int value=map.get(Character.isSpace(' '));
//				map.put(' ', value+1);
//			}
//		}
//		
//		System.out.println(map);
		
		for (int i = 0; i < output.length()-1; i++) {
			if(output.charAt(i)==' ') {
				count++;
			}
		}
		System.out.println(count);
		
		
	}

}
