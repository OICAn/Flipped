package com.jackiifilwhh.services;

import com.jackiifilwhh.Java.JavaAnalysis;

public class testJSON {

	public static void main(String[] args) {
		String test = "≤‚ ‘";
		JavaAnalysis javaAnalysis = new JavaAnalysis();
		System.out.println(javaAnalysis.getSYM(test));
	}

}
