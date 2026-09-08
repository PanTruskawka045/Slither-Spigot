package me.pan_truskawka045.Slither.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProcessUtil {

    public String getValue(String env, String propety, String defaultValue) {
        if (System.getenv().containsKey(env) && System.getenv(env) != null && !System.getenv(env).isEmpty()) {
            return System.getenv(env);
        }
        if (System.getProperties().containsKey(propety)) {
            return System.getProperty(propety);
        }
        return defaultValue;
    }


}
