/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.model;

import org.drools.shapes.model.datatypes.CD;

import java.util.Date;

public class Condition {

    private String id;
    private Date dateTime;
    private CD code;
    private String pid;


    public Condition( String id, Date dateTime, CD code, String pid ) {
        this.id = id;
        this.dateTime = dateTime;
        this.code = code;
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime( Date dateTime ) {
        this.dateTime = dateTime;
    }

    public CD getCode() {
        return code;
    }

    public void setCode( CD code ) {
        this.code = code;
    }

    public String getPid() {
        return pid;
    }

    public void setPid( String pid ) {
        this.pid = pid;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Condition ) ) return false;

        Condition condition = (Condition) o;

        if ( id != null ? !id.equals( condition.id ) : condition.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
