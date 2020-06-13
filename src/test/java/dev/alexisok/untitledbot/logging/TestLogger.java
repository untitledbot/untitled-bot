package dev.alexisok.untitledbot.logging;

import org.junit.Test;

public class TestLogger {
	
	@Test
	public void testLogger() {
		Logger.log("This is a log!");
		Logger.critical("This is a really bad error!", 3, false);
	}
	
}
