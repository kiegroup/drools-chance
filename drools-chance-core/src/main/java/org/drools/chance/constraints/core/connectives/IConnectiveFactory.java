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

import org.drools.chance.constraints.core.IConstraintCore;

public interface IConnectiveFactory {

    IConstraintCore getAnd();
    IConstraintCore getAnd(String type);
    IConstraintCore getAnd(String type, Object... params);


    IConstraintCore getOr();
    IConstraintCore getOr(String type);
    IConstraintCore getOr(String type, Object... params);


    IConstraintCore getNot();
    IConstraintCore getNot(String type);
    IConstraintCore getNot(String type, Object... params);


    IConstraintCore getXor();
    IConstraintCore getXor(String type);
    IConstraintCore getXor(String type, Object... params);


    IConstraintCore getEquiv();
    IConstraintCore getEquiv(String type);
    IConstraintCore getEquiv(String type, Object... params);


    IConstraintCore getImplies();
    IConstraintCore getImplies(String type);
    IConstraintCore getImplies(String type, Object... params);





}
