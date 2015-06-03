package edu.berkeley.cs.sdb.bosswave;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandTest {

    @Test
    public void testFromCode() {
        for (Command command : Command.values()) {
            assertEquals(Command.fromCode(command.getCode()), command);
        }
    }
}