package flipkart.alert.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 24/10/12
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */

@Setter @Getter
public class MetricTag implements Serializable{
    private String tag, value;

    public MetricTag(String tag, String value) {
        this.tag = tag;
        this.value = value;
    }

    @Override
    public String toString() {
        return "MetricTag : Key: "+ tag +" Value: "+value;
    }
}
