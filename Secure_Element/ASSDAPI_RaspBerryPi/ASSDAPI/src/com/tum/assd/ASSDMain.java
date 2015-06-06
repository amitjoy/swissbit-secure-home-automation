package com.tum.assd;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ASSDMain implements ASSDInterface{

	@Override
	public boolean checkASSD() {
		String filename = "/dev/assd";
		File assd = new File(filename);
		if(assd.exists()){
			return true;
		}
		return false;
	}

	@Override
	public boolean loadASSD() {
		boolean assdAvail = checkASSD();
		if(assdAvail){
			return true;
		} else{
			StringBuffer output = new StringBuffer();
			String command = "sudo insmod ~/assd.ko"; 
			Process p;
			try {
				p = Runtime.getRuntime().exec(command);
				p.waitFor();

				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";			
				while ((line = reader.readLine())!= null) {
					output.append(line + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean isCardPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String encrypt(String plainText) {
		String cipher = "";
		boolean assdAvail = checkASSD();
		if(assdAvail){
			try{
				ProcessBuilder pb = new ProcessBuilder();
				pb.command("python","encrypt.py",plainText);
				Process p = pb.start();

				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				cipher = in.readLine();

			}catch(Exception e){
				System.out.println(e);
			}
			return cipher;
		} else{
			return cipher;
		}
	}

	@Override
	public String decrypt(String cipher) {
		String msg = "";
		boolean assdAvail = checkASSD();
		if(assdAvail){
			try{
				ProcessBuilder pb = new ProcessBuilder();
				pb.command("python","decrypt.py",cipher);
				Process p = pb.start();

				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				msg = in.readLine();

			}catch(Exception e){
				System.out.println(e);
			}
			return msg;

		} else{
			return msg;
		}
	}

}
