package com.tum.assd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
	public boolean loadASSD(String adminPassword) {
		List<String> commands = new ArrayList<String>();
		commands.add("sudo");
		commands.add("insmod");
		commands.add("assd.ko");
		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands, adminPassword);
		try {
			commandExecutor.execCommand();
			System.out.println("Executor: " + commandExecutor.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
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
				pb.command("sudo","python","./encrypt.py",plainText);
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
				pb.command("sudo","python","./decrypt.py",cipher);
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
