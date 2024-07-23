package com.utils;

import com.github.javafaker.Faker;

public final class RandomData {

	static Faker f = new Faker();
	
	public RandomData() {
		// TODO Auto-generated constructor stub
	}
	
	public static String generateCountryName() {
		String country = "";
		boolean flag = true;
		while(flag) {
			country = f.address().country();
			if(country.length() < 10) {
				flag = false;
			}
		}
		return country;
	}
}
