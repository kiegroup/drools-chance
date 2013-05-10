/*
 * Copyright 2013 JBoss Inc
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

package org.drools.semantics.util.area;

import org.drools.util.CodedHierarchy;
import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

public interface Area<C,P> {

    public Collection<C> getElements();

    public Collection<C> getRoots();

    public BitSet getAreaCode();

    public BitSet getElementRootCode();

    public void setAreaCode(BitSet areaCode);

    public String getNodeName();

    public Set<AreaNode<C,P>> getImmediateParents();

    public Set<P> getKeys();

    public CodedHierarchy<C> getConHir();

    public BitSet getRootBitSet();

    public void setRoots( Set<C> roots );

    public void addElement(C concept, BitSet code);

    //added by mh
    public Set<C> getOverlappingElements();

}
