package com.flipkart.alert.dispatch;

import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.domain.Alert;
import com.flipkart.alert.domain.Rule;
import com.flipkart.alert.domain.RuleCheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 22/4/13
 * Time: 7:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class NagiosDispatcher implements StatusDispatcher {
    private static Log log = Log.forClass(NagiosDispatcher.class);

    @Override
    public void dispatch(Alert alert, Map<String, Object> config) {
        List<String> alertLevels = new ArrayList<String>();
        StringBuffer alertDescription = new StringBuffer();
        for (RuleCheck check: alert.getBreachedChecks()) {
            alertDescription.append(check.getDescription() + " ");
            alertLevels.add(check.getAlertLevel());
        }
        buildNagiosExecutable(alert.getRule().getName(), Collections.max(alertLevels), alertDescription.toString(), config);
    }

    @Override
    public void dispatch(Rule rule, Map<String, Object> config) {
        buildNagiosExecutable(rule.getName(), "0", "No Breaches Found", config);
    }

    public void buildNagiosExecutable(String serviceName, String alertLevel, String alertDescription, Map<String, Object> configMap) {


//        StringBuffer executeString = new StringBuffer();
//        executeString.append(configMap.get("ncsa_wrapper"))
//                .append(" -H \"").append(configMap.get("host")).append("\"")
//                .append(" -N \"").append(configMap.get("IP")).append("\"")
//                .append(" -S \"").append(serviceName).append("\"")
//                .append(" -b \"").append(configMap.get("binary")).append("\"")
//                .append(" -c \"").append(configMap.get("config")).append("\"")
//                .append(" -C \"").append(configMap.get("nagiosHelper")).append(" ")
//                .append(alertLevel).append(" ").append(alertDescription).append("\"");

//        String command[] = executeString.toString().split(" ");
          String command[] = new String[]{configMap.get("nsca_wrapper").toString(),
                  "-H", "" + configMap.get("host")+ "",
                  "-N", "" + configMap.get("IP") + "",
                  "-S", ""+ serviceName+"",
                  "-b", ""+ configMap.get("binary")+ "",
                  "-c", ""+ configMap.get("config")+ "",
                  "-C", ""+ configMap.get("nagiosHelper")+" " +alertLevel+ " "+ alertDescription};
        log.info(Arrays.toString(command));

        Runtime runtime = Runtime.getRuntime();
        String output;
        try {
            Process process = runtime.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((output = input.readLine()) != null) {
                log.info(output);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
