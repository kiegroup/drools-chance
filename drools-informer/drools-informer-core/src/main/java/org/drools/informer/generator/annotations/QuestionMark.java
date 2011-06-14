package org.drools.informer.generator.annotations;


import org.drools.informer.Question;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface QuestionMark {

    Question.QuestionType type() default Question.QuestionType.TYPE_TEXT;

    String label() default "";

    boolean required() default true;

    String dateFormat() default "";

    boolean finalAnswer() default false;


    String when() default "";
}
