package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.logging.Logger;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

public class ManualTest {
	
	@Test
	public void setPage5() {
		//
		Manual.setHelpPage("1", "help1");
		Manual.setHelpPage("2", "help2");
		Manual.setHelpPage("3", "help3");
		Manual.setHelpPage("4", "help4");
		
		Logger.log(Manual.getHelpPages("1"));
	}
	
}
