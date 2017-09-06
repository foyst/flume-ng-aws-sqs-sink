package com.dushyant.flume.sink.aws.sqs;

import org.apache.commons.lang.StringUtils;

public class ContextUtils {

    /**
     * The method for resolving the configured property value.
     * <p>
     * For example, the configuration allows properties to be specified in the {@code env.propertyName} format. This
     * method resolves that configuration property value using the environment variable named {@code propertyName}. In
     * other cases, the method returns the given property as is.
     *
     * @param property The configured property value to be resolved
     *
     * @return The resolved property value
     */
    public static String resolve(String property) {
        String resolved = property;
        if (StringUtils.isNotBlank(property) && property.startsWith("env.")) {
            String envVariableName = StringUtils.substringAfter(property, "env.");
            resolved = System.getenv(envVariableName);
        }
        return resolved;
    }
}
