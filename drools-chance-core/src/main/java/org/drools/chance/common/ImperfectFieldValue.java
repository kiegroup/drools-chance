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

package org.drools.chance.common;


import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.DistributionStrategies;

public class ImperfectFieldValue<T> extends ImperfectFieldImpl<T> {


    public ImperfectFieldValue(DistributionStrategies<T> tiDistributionStrategies) {
        super(tiDistributionStrategies);
    }

    public ImperfectFieldValue(DistributionStrategies<T> tiDistributionStrategies, String distrAsString) {
        super(tiDistributionStrategies, distrAsString);
    }

    public ImperfectFieldValue(DistributionStrategies<T> tiDistributionStrategies, Distribution<T> distr0) {
        super(tiDistributionStrategies, distr0);
    }


}
