package flipkart.alert.helper;

import flipkart.alert.util.ExpressionHelper;
import static org.hamcrest.MatcherAssert.*;

import static org.hamcrest.Matchers.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 24/12/13
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestExpressionHelper {

    @DataProvider(name = "expressions")
    public Object[][] getExpressionsAndResults() {
        return new Object[][]{
                {"1 + 2", 3},
                {"(1 + 2) > 2", true},
                {"'Hello'.contains('Hell')", true},
                {"'Hello'.equals('Hello')", true},
                {"Hello","Hello"},
                {"true && true",true},
        };
    }

    @Test(dataProvider = "expressions")
    public void testExpressions(String expression, Object expectedResult) {
        Object result = ExpressionHelper.evaluateExpression(expression);
        assertThat(result, is(expectedResult));
    }
}
