/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.chance.rule.constraint.core.connectives.impl;


public enum MvlFamilies {

    PRODUCT("PRODUCT"),

    GODEL("GODEL"),

    LUKAS("LUKAS");

    public static final String name = "family";

    private final String value;

    MvlFamilies( String value ) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static MvlFamilies parse( String x ) {
        if ( x == null || x.isEmpty() ) { return null; }
        int dotPos = x.indexOf( '.' );
        if ( dotPos >= 0 ) {
            x = x.substring( dotPos + 1 );
        }
        return MvlFamilies.valueOf( x );
    }
}
