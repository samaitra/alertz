package flipkart.alert.domain;

import flipkart.alert.dispatch.StatusDispatcher;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 03/09/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Setter @Getter
public class DispatcherContainer {

    private Object dispatcherObject;
    private StatusDispatcher dispatcher;
    private Map<String, Object> configurableParameters;
    private boolean publishAlways;

    public DispatcherContainer(Object o, StatusDispatcher statusDispatcher, Map<String, Object> configurableParams, boolean publishAlways) {
        this.dispatcherObject = o;
        this.dispatcher = statusDispatcher;
        this.configurableParameters = configurableParams;
        this.publishAlways = publishAlways;
    }
}
