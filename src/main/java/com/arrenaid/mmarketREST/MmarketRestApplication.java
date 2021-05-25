package com.arrenaid.mmarketREST;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.arrenaid.mmarketREST.controllermultiagent.MainController;

@SpringBootApplication
public class MmarketRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MmarketRestApplication.class, args);
		try {
			MainController mc = new MainController();
			//SecondController second = new SecondController();
			mc.initAgents();
			//second.initAgents();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
