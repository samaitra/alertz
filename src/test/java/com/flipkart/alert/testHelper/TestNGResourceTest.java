package com.flipkart.alert.testHelper;

import com.yammer.dropwizard.logging.Log;
import com.yammer.dropwizard.testing.ResourceTest;
import org.testng.annotations.*;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 11/12/12
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TestNGResourceTest extends ResourceTest {
    protected static Log log = Log.forClass(TestNGResourceTest.class);

    @BeforeClass(alwaysRun = true)
    public void classSetup() throws Exception {
        setUpJersey();
    }

    @AfterClass
    public void classTeardown() throws Exception {
        tearDownJersey();
    }
}
