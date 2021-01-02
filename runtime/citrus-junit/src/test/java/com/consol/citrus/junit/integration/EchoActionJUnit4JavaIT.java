package com.consol.citrus.junit.integration;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.junit.spring.JUnit4CitrusSpringSupport;
import org.junit.Test;

import static com.consol.citrus.actions.EchoAction.Builder.echo;

/**
 * @author Christoph Deppisch
 */
public class EchoActionJUnit4JavaIT extends JUnit4CitrusSpringSupport {

    @Test
    @CitrusTest
    public void echoJavaTest() {
        variable("time", "citrus:currentDate()");

        run(echo("Hello Citrus!"));

        run(echo("CurrentTime is: ${time}"));
    }

    @Test
    @CitrusTest(name = "EchoSampleTest")
    public void echoTest() {
        variable("time", "citrus:currentDate()");

        run(echo("Hello Citrus!"));

        run(echo("CurrentTime is: ${time}"));
    }
}
