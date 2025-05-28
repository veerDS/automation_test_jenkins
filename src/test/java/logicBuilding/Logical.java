package logicBuilding;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Logical {

	public static void main(String[] args) {
		findDuplicates();
		charOccurance("data");
		removeSpecialCharFromString();
		reverseStringByWords();
	}

	public static void findDuplicates() {
		int [] numbers= {10,20,30,10,20};

		Set<Integer> set=new HashSet<Integer>();

		for (int num : numbers) {
			if(set.add(num)==false) {
				System.out.print(num+" ");
			}
		}	
	}

	public static void charOccurance(String str) {

//		String str="veerdhaval";

		char [] ch=str.toCharArray();

		Map<Character, Integer> map=new LinkedHashMap<Character, Integer>();

		for(char c:ch) {
			if(!map.containsKey(c)) {
				map.put(c, 1);
			}
			else {
				int value=map.get(c);
				map.put(c, value+1);
			}
		}
		System.out.println(map);
	}


	public static void removeSpecialCharFromString() {
		String str="D*i@w#a$l&i";
		String output="";

		for(int i=0;i<str.length();i++) {
			char ch=str.charAt(i);
			if(Character.isLetter(ch)) {
				output=output+ch;
			}
		}
		System.out.println("Output is "+output);

	}
	
	public static void reverseStringByWords() {
		String str="This is automation testing";
		String [] words=str.split(" ");	
		String output="";
		
		for (String word : words) {
			String reverse="";
			
			for(int i=word.length()-1;i>=0;i--) {
				reverse=reverse+word.charAt(i);
			}
			output=output+reverse+" ";
		}
		
		System.out.println(output);
	}



	































































}
