package com.tum.assd;

public interface ASSDInterface {

	public boolean checkASSD();
	
	public boolean loadASSD(String adminPassword);
	
	public boolean isCardPresent();
	
	public String encrypt(String plainText);
	
	public String decrypt(String cipher);
}
