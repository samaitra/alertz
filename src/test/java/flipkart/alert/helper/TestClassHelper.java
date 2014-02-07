package flipkart.alert.helper;

import flipkart.alert.util.ClassHelper;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import java.lang.reflect.InvocationTargetException;

/**
 * nitinka
 * Test the ClasHelper Utilities
 */
public class TestClassHelper {

    @Test(groups = {"smoke", "regression","util"})
    public void testCreateInstance() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        TestClassHelper helperObject = ClassHelper.createInstance(TestClassHelper.class);
        assertEquals(helperObject.getClass().getCanonicalName(), TestClassHelper.class.getCanonicalName(),"Class Canonical Name");
    }

    @Test(groups = {"regression","util", "negative"}, expectedExceptions = {java.lang.NullPointerException.class})
    public void testCreateInstanceWithNull() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        ClassHelper.createInstance(null);
    }

    @Test(groups = {"regression","util", "negative"},
            expectedExceptions = {java.lang.NoSuchMethodException.class},
            expectedExceptionsMessageRegExp = "flipkart.alert.helper.PrivateConstructorClass.<init>\\(\\)")
    public void testCreateInstancePrivateConstructor() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        ClassHelper.createInstance(PrivateConstructorClass.class);
    }


}

class PrivateConstructorClass {
    private void PrivateConstructorClass() {

    }
}