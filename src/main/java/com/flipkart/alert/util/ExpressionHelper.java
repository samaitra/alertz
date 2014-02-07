package com.flipkart.alert.util;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 20/08/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */

public class ExpressionHelper {
    private static ExpressionParser parserSpring;
    private static ScriptEngine parserJavax;   // Will be used in native js execution which spring parser not able to handle it.

    private static Pattern PATTERN_NOT_DEFINED = Pattern.compile(".+Field or property \'(.+)\' cannot be found.+");

    static {
        parserSpring = new SpelExpressionParser();
        ScriptEngineManager manager = new ScriptEngineManager();
        parserJavax = manager.getEngineByName("JavaScript");
    }

    public static Object evaluateExpression(String expression) {
        return evaluateExpressionWithRetries(expression);
    }

    public static Object evaluateExpressionWithRetries(String expression) {
        Object evaluatedValue = new Object();
        for(int i=1;i<=5;i++) {
            try {
                Expression exp = parserSpring.parseExpression(expression);
                evaluatedValue = exp.getValue();
                break;
            } catch (SpelEvaluationException e) {
                Matcher matcher = PATTERN_NOT_DEFINED.matcher(e.getLocalizedMessage());
                if(matcher.matches()) {
                    expression = expression.replace(matcher.group(1), "'" + matcher.group(1) + "'");
                }

                if(i==5)
                    throw e;
            } catch (IllegalStateException e) {
                try {
                    evaluatedValue = parserJavax.eval(expression);
                } catch (ScriptException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        return evaluatedValue;
    }
}
