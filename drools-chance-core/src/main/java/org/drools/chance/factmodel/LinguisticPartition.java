package org.drools.chance.factmodel;


public @interface LinguisticPartition {

    Class<? extends Number> value() default Double.class;

}
