package edu.berkeley.cs.sdb.bosswave;

import junit.framework.TestCase;

public class CommandTest extends TestCase {

    public void testFromCode() {
        for (Command command : Command.values()) {
            assertEquals(Command.fromCode(command.getCode()), command);
        }
    }
}