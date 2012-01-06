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

package org.drools.semantics.model.domain;

public class IEcho_Impl implements IEcho_Trait {

    private static IEcho_Impl singleton = null;

    protected IEcho_Impl() { }

    public static IEcho_Trait newInstance() {
        if (singleton == null) {
            singleton = new IEcho_Impl();
        }
        return singleton;
    }



    private String lastEchoMessage;

    public String getLastEchoMessage() {
        return lastEchoMessage;
    }

    public void setLastEchoMessage(String lastEchoMessage) {
        this.lastEchoMessage = lastEchoMessage;
    }

    public String echo(String s) {
        StringBuffer sb = new StringBuffer(s);
        String eko = sb.reverse().toString();
        setLastEchoMessage(eko);
        return eko;
    }

}
