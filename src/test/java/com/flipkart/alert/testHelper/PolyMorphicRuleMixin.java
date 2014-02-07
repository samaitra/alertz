package com.flipkart.alert.testHelper;

import com.flipkart.alert.domain.OnDemandRule;
import com.flipkart.alert.domain.ScheduledRule;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 24/05/13
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "isScheduled")
@JsonSubTypes({
    @Type(value = ScheduledRule.class, name = "true"),
    @Type(value = OnDemandRule.class, name = "false"),
    @Type(value = ScheduledRule.class) })

public abstract class PolyMorphicRuleMixin {}
