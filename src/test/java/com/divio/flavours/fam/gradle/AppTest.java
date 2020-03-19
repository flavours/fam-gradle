package com.divio.flavours.fam.gradle;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

public class AppTest {
    @Test
    public void testAddCommand() throws IOException {
        /* TODO

        */

        var app = new App();

        var actual = app.add();

    }

    @Test
    public void testCheckCommand() throws IOException {

        var app = new App();

        var actual = app.check();
    }

    @Test
    public void testRemoveCommand() {
        var app = new App();

        var actual = app.remove(null);
    }
}
