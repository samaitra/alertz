package com.flipkart.alert.dispatch;

import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.domain.Alert;
import com.flipkart.alert.domain.Rule;
import com.flipkart.alert.domain.RuleCheck;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 17/04/13
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailStatusDispatcher implements StatusDispatcher {
    private static Log log = Log.forClass(StatusDispatcher.class);
    private static HashMap<String, String> alertLevelMap;

    static {
        alertLevelMap = new HashMap<String, String>();
        alertLevelMap.put("0", "[INFO] ");
        alertLevelMap.put("1", "[WARNING] ");
        alertLevelMap.put("2", "[CRITICAL] ");
    }

    @Override
    public void dispatch(Alert alert, Map<String, Object> config) {
        List<String> alertLevels = new ArrayList<String>();
        List<RuleCheck> breachedChecks = alert.getBreachedChecks();
        StringBuffer body = new StringBuffer();

        body.append("Rule Name: ").append(alert.getRule().getName()).
                append("\n");
        body.append("Time of Breach: ").append(alert.getAlertDate()).
                append("\n\n");
        body.append("Breached Checks: \n");

        for(RuleCheck check: breachedChecks) {
            alertLevels.add(check.getAlertLevel());
            body.append("Description: ").append(check.getDescription()).
                    append("\n").
                    append("Expression breached: ").append(check.getBooleanExpression()).
                    append("\n").
                    append("Alert Level :"+alertLevelMap.get(check.getAlertLevel())).
                    append("\n\n");
        }

        String subject = buildSubject(alert.getRule().getName(), alertLevels);
        sendMail(subject, body.toString(), config);
    }

    private String buildSubject(String ruleName, List<String> alertLevels) {
        String criticality = alertLevelMap.get(Collections.max(alertLevels));
        return criticality + "ALERT! Breached Rule: " + ruleName;
    }

    @Override
    public void dispatch(Rule rule, Map<String, Object> config) {
        String subject = "Rule: " + rule.getName();
        String body = "Status OK";
        sendMail(subject, body, config);
    }

    private void sendMail(String subject, String body, Map<String,Object> configMap) {
        Session session;

        Properties properties = System.getProperties();
        for(String key: configMap.keySet()) {
            properties.setProperty(key, configMap.get(key).toString());
        }

        if(configMap.containsKey("username") && configMap.containsKey("password")) {
            final String username = configMap.get("username").toString();
            final String password = configMap.get("password").toString();

            session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }
        else {
            session = Session.getDefaultInstance(properties);
        }

        try{

            MimeMessage message = new MimeMessage(session);
            log.info("Sending mail to: " + configMap.get("to") + " with subject: " + subject);
            message.setFrom(new InternetAddress(configMap.get("from").toString()));
            String address = configMap.get("to").toString();
            String[] toAddresses = address.split((","));
            for(String to : toAddresses) {
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(to));
            }

            message.setSubject(subject);

            message.setText(body);

            Transport.send(message);
            log.info("Mail sent successfully!");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}

