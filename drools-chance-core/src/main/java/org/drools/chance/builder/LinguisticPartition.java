package org.drools.chance.builder;


public @interface LinguisticPartition {

    Class<? extends Number> value() default Double.class;

}
