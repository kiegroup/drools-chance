package org.drools.beliefs.provenance.annotations;

public @interface Display {

    String type() default "1";

    String value() default "";

    String template() default "";

}
