package dev.alexisok.untitledbot.data;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataTest {
	
	//I'll be using my own Discord ID for this.
	private static final String ID = "541763812676861952";
	
	@BeforeEach
	public void setUp() {
		UserData.setKey(ID, "test", "value");
	}
	
	@Test
	public void testGetData() {
		if(!UserData.getKey(ID, "test").equals("value"))
			throw new RuntimeException();
	}
	
	@Test
	public void testSetData() {
		UserData.setKey(ID, "test2", "value2");
		if(!UserData.getKey(ID, "test2").equals("value2"))
			throw new RuntimeException();
		if(!UserData.getKey(ID, "test").equals("value"))
			throw new RuntimeException();
	}
	
	@AfterEach
	public void breakDown() {
		
	}
	
}
