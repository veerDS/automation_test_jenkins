package com.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.testng.annotations.Test;

public class SampleTest {

	public static void main(String[] args) {
//
//		WebDriverManager.chromedriver().setup();
//		WebDriver driver=new ChromeDriver();
//		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(45));
//		driver.manage().window().maximize();
		
		combinations();
		wordOcc();
		charOcc('a');
		findDuplicates();
		anagramString();
		firstNonRepeatedCharacter();
		extractChars();
		reverseSentance();
		removeDuplicates();
		test();
		dupli();
		check();
	}


	// output = a ab abc ac b bc c
	public static void combinations() {
		String str="abc";

		for(int i=0;i<str.length();i++) {
			StringBuilder sb=new StringBuilder();
			for(int j=i;j<str.length();j++) {
				sb.append(str.charAt(j));
				System.out.println(sb);
			}


		}

	}
	
	public static void wordOcc() {
		String str="this is java programing in java this";
		String [] words=str.split(" ");
		Map<String, Integer> map=new HashMap<String, Integer>();
		
		for(String word:words) {
			if(!map.containsKey(word)) {
				map.put(word, 1);
			}
			else {
				int value=map.get(word);
				map.put(word, value+1);
			}
		}
		
		for(String s:map.keySet()) {
			System.out.println(s+" = "+map.get(s));
		}
		
		
		
	}
	
	public static void charOcc(char ch) {
		String str="java is object oriented programing language";
		
		char chars[]=str.toCharArray();
		int count=0;
		
		for(int i=0;i<chars.length;i++) {
			if(str.charAt(i)==ch) {
				count++;
			}
		}
		System.out.println(ch+" = "+count);
		
		
	}

	public static void findDuplicates() {
		String str="veerdhaval";
		char [] chars=str.toCharArray();
		Map<Character, Integer> map=new HashMap<Character, Integer>();
		
		for(char ch:chars) {
			if(!map.containsKey(ch)) {
				map.put(ch, 1);
			}
			else {
				int value=map.get(ch);
				map.put(ch, value+1);
			}
		}
		
		for(Character c:map.keySet()) {
			if(map.get(c)>1) {
				System.out.println(c+" = "+map.get(c));
			}
		}
			
		
	}
	
	public static void anagramString() {
		String str1="acer";
		String str2="care";
		
		char[] arr1=str1.toLowerCase().toCharArray();
		char[] arr2=str2.toLowerCase().toCharArray();
		
		Arrays.sort(arr1);
		Arrays.sort(arr2);
		
		if(Arrays.equals(arr1, arr2)) {
			System.out.println("The given String is Anagram");
		}
		else {
			System.out.println("The given String is not Anagram");
		}
		
	}

	public static void firstNonRepeatedCharacter() {
		String str="veerdhaval";
		char [] chars=str.toCharArray();
		Map<Character, Integer> map=new LinkedHashMap<Character, Integer>();
		
		for(char ch:chars) {
			if(!map.containsKey(ch)) {
				map.put(ch, 1);
			}
			else {
				int value=map.get(ch);
				map.put(ch, value+1);
			}
		}
		
		for(Character c:map.keySet()) {
			if(map.get(c)==1) {
				System.out.println("The first non repeated character is : "+c);
				break;
			}
		}
	
	}
	
	public static void extractChars() {
		String str="www.automation.com";
		String extract=str.substring(0, str.length()-14);
		System.out.println(extract);
	}
	
	public static void reverseSentance() {
		String str="we are learning java";
		String [] words=str.split(" ");
		String result="";
		
		for(int i=words.length-1;i>=0;i--) {
			result=result+words[i]+" ";
		}
		System.out.println(result);
		
		
		
	}
	
	
	
	public static void removeDuplicates() {
		String str="veerdhaval";
		
		char [] chars=str.toCharArray();
		
		Set<Character> set=new LinkedHashSet<Character>();
		
		for(Character ch:chars) {
			set.add(ch);
		}
		
		Iterator<Character> it= set.iterator();
		
		while(it.hasNext()) {
			System.out.print(it.next());
		}
	}

	
	public static void test() {
		int attempt=0;
		
		while(attempt<3) {
			try {
				int a=0 ,b=5;
				if(a/b==0) {
					System.out.println("This is attempt 1");
					break;
				}
				
			} catch (Exception e) {
				attempt++;
			}
		}
		
	}
	
	
	public static void exception() throws Exception {
		int a=0;
		if(a<18) {
			throw new IllegalArgumentException();
		}
	}
	
	// remove duplicates using String Builder
	
	public static void dupli() {
		String str="veerdhaval";
		Set<Character> set=new HashSet<Character>();
		StringBuilder sb=new StringBuilder();
		
		for(int i=0;i<str.length();i++) {
			set.add(str.charAt(i));
		}
		for(Character ch:set) {
			sb.append(ch);
		}
		System.out.println(sb);
	
	}
	
	public static void check() {
		String str="welcome";
		int count=0;
		boolean flag=false;
		for(int i=0;i<str.length();i++) {
			if(str.charAt(i)=='a'||(str.charAt(i)=='e'||(str.charAt(i)=='i'||(str.charAt(i)=='o'||(str.charAt(i)=='u'))))){
				flag=true;
				if(flag) {
					count++;
				}
				else {
					System.out.println("Vowels are not present");
				}
				
			}
		}
		System.out.println("Vowles count in String "+str+" is = "+count);
	
		
		
	}
}


