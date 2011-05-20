package org.drools.chance.constraints.core.connectives.impl;

/**
 * Created by IntelliJ IDEA.
 * User: doncat
 * Date: 27/01/11
 * Time: 16.43
 * To change this template use File | Settings | File Templates.
 */
public enum MVLFAMILIES {

    PRODUCT("prod"),

    GODEL("godel"),

    LUKAS("lukas");

    private final String value;

    MVLFAMILIES(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
