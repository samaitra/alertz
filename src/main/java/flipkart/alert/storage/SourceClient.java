package flipkart.alert.storage;

import flipkart.alert.domain.Metric;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 21/10/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */

@Setter @Getter
public abstract class SourceClient {

    private String query;
    private String queryName;
    private String metricSource;

    abstract public List<Metric> execute() throws IOException, ParseException;
}
