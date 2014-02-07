package flipkart.alert.storage;

import flipkart.alert.domain.NagiosAlertYaml;
import flipkart.alert.domain.NagiosService;
import flipkart.alert.domain.NagiosServiceEscalation;
import org.yaml.snakeyaml.*;
import com.yammer.dropwizard.logging.Log;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 14/4/13
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public enum NagiosYamlWriter {
    INSTANCE;
    private Log log = Log.forClass(NagiosYamlWriter.class);

    private NagiosAlertYaml nagiosYaml;


    public void initialize() {

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        try {
            InputStream nagiosYamlFile= new FileInputStream("/var/cache/fk-alert-service/nagiosAlerts.yaml");
            Yaml yaml= new Yaml(new Constructor(NagiosAlertYaml.class));
            nagiosYaml = (NagiosAlertYaml)yaml.load(nagiosYamlFile);
            log.info("Read Nagios yaml file as: " + nagiosYaml.toString());
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public NagiosAlertYaml getNagiosYaml() {
        return nagiosYaml;
    }

    public void setNagiosYaml(NagiosAlertYaml nagiosYaml) {
        this.nagiosYaml = nagiosYaml;
        updateYamlFile();
    }

    synchronized public void addServiceEscalations(Map<String,NagiosServiceEscalation> escalations) {
        nagiosYaml.ServiceEscalations.putAll(escalations);
        updateYamlFile();
    }

    synchronized public void addServiceEscalation(String escalationName, NagiosServiceEscalation escalation) {
        nagiosYaml.ServiceEscalations.put(escalationName, escalation);
        updateYamlFile();
    }

    synchronized public void removeServiceEscalation(String escalationName) {
        nagiosYaml.ServiceEscalations.remove(escalationName);
        updateYamlFile();
    }

    synchronized public void removeServiceEscalations(List<String> escalationNames) {
        for (String escalationName: escalationNames) {
            nagiosYaml.ServiceEscalations.remove(escalationName);
        }
        updateYamlFile();
    }

    synchronized public void addServices(Map<String, NagiosService> services) {
        nagiosYaml.Services.putAll(services);
        updateYamlFile();
    }

    /**
     * Add a service to the nagios yaml object. If a service with the same name exists, it is replaced.
     * @param serviceName
     * @param service
     */
    synchronized public void addService(String serviceName, NagiosService service) {
        nagiosYaml.Services.put(serviceName,service);
        updateYamlFile();
    }

    synchronized public void removeService(List<String> serviceNames) {
        for(String service: serviceNames) {
            nagiosYaml.Services.remove(service);
        }
        updateYamlFile();
    }

    synchronized public void updateYamlFile() {

        DumperOptions options = new DumperOptions();

        Yaml yaml = new Yaml(options);

        String yamlDump = yaml.dumpAs(nagiosYaml, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
        log.info("Writing into yaml file: ");
        log.info(yamlDump);

        try {
            FileOutputStream nagiosYamlfile = new FileOutputStream("/var/cache/fk-alert-service/nagiosAlerts.yaml");
            nagiosYamlfile.write(yamlDump.getBytes());
            nagiosYamlfile.flush();
            nagiosYamlfile.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
