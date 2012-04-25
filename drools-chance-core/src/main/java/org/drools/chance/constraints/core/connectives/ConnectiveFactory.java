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

package org.drools.chance.constraints.core.connectives;

import org.drools.chance.constraints.core.connectives.impl.LogicConnectives;

import java.util.Map;

public interface ConnectiveFactory {


    ConnectiveCore getConnective( LogicConnectives conn, String type, Object... params);
    
    Map<String,Class<?>> getKnownOperatorClasses();

    ConnectiveCore getAnd();
    ConnectiveCore getAnd(String type);
    ConnectiveCore getAnd(String type, Object... params);


    ConnectiveCore getOr();
    ConnectiveCore getOr(String type);
    ConnectiveCore getOr(String type, Object... params);


    ConnectiveCore getNot();
    ConnectiveCore getNot(String type);
    ConnectiveCore getNot(String type, Object... params);


    ConnectiveCore getMinus();
    ConnectiveCore getMinus(String type);
    ConnectiveCore getMinus(String type, Object... params);


    ConnectiveCore getXor();
    ConnectiveCore getXor(String type);
    ConnectiveCore getXor(String type, Object... params);


    ConnectiveCore getEquiv();
    ConnectiveCore getEquiv(String type);
    ConnectiveCore getEquiv(String type, Object... params);


    ConnectiveCore getImplies();
    ConnectiveCore getImplies(String type);
    ConnectiveCore getImplies(String type, Object... params);


}
