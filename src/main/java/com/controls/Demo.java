package com.controls;

public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str="My name is veerdhaval";
		
		String [] words=str.split(" ");
		
		String result="";
		
		StringBuilder sb=new StringBuilder();
		
		for(int i=words.length-1;i>=0;i--) {
			
			result=result+words[i]+" ";
			
			char ch=words[i].charAt(0);
			
			 sb.append(Character.toUpperCase(ch)).append(result).append(" ");

		}
		System.out.println(sb);
		
		System.out.println(result);

		System.out.println("--------------------------------------------------------------------");
		
	}

}
