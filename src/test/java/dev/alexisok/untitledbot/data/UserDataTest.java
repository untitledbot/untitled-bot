package dev.alexisok.untitledbot.data;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.util.Objects;

public class UserDataTest {
	
	//I'll be using my own Discord ID for this.
	static final String ID = "541763812676861952";
	
	@BeforeAll
	static void setUp() {
		
	}
	
	@Test
	public void testSetData() {
		Logger.log("Data directory: " + Main.DATA_PATH);
		UserData.setKey(ID, "test2", "value2");
		if(!Objects.equals(UserData.getKey(ID, "test2"), "value2"))
			throw new RuntimeException();
	}
	
	@AfterEach
	public void breakDown() {
		
	}
	
}
