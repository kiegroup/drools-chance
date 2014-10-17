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

package org.drools.chance.common.trait;


import org.drools.chance.common.ImperfectField;
import org.drools.chance.common.fact.Price;
import org.drools.chance.common.fact.Weight;
import org.drools.chance.distribution.Distribution;

public interface ImpBean {

    //TODO Update by field ??

    public ImperfectField<String> getName();
    public Distribution<String> getNameDistr();
    public String getNameValue();

    public void setName( ImperfectField<String> x );
    public void setNameDistr( Distribution<String> x );
    public void setNameValue( String x );

    public void updateName( ImperfectField<String> x  );
    public void updateNameDistr( Distribution<String> x );
    public void updateNameValue( String x );

    
    
    
    public ImperfectField<Boolean> getFlag();
    public Distribution<Boolean> getFlagDistr();
    public Boolean getFlagValue();

    public void setFlag( ImperfectField<Boolean> x );
    public void setFlagDistr( Distribution<Boolean> x );
    public void setFlagValue( Boolean x );

    public void updateFlag( ImperfectField<Boolean> x );
    public void updateFlagDistr( Distribution<Boolean> x );
    public void updateFlagValue( Boolean x );
    
    
    
    
    public ImperfectField<Integer> getAge();
    public Distribution<Integer> getAgeDistr();
    public Integer getAgeValue();

    public void setAge( ImperfectField<Integer> x );
    public void setAgeDistr( Distribution<Integer> x );
    public void setAgeValue( Integer x );

    public void updateAge( ImperfectField<Integer> x );
    public void updateAgeDistr( Distribution<Integer> x );
    public void updateAgeValue( Integer x );

    
    
    
    
    public ImperfectField<Weight> getBody();
    public Distribution<Weight> getBodyDistr();
    public Weight getBodyValue();

    public void setBody( ImperfectField<Weight> x );
    public void setBodyDistr( Distribution<Weight> x );
    public void setBodyValue( Weight x );

    public void updateBody( ImperfectField<Weight> x );
    public void updateBodyDistr( Distribution<Weight> x );
    public void updateBodyValue( Weight x );

    public Double getWeight();
    public void setWeight( Double x );

    
    
    public ImperfectField<Price> getPrice();
    public Distribution<Price> getPriceDistr();
    public Price getPriceValue();

    public void setPrice( ImperfectField<Price> x );
    public void setPriceDistr( Distribution<Price> x );
    public void setPriceValue( Price x );

    public void updatePrice( ImperfectField<Price> x );
    public void updatePriceDistr( Distribution<Price> x );
    public void updatePriceValue( Price x );
        

    public Integer getBucks();
    public void setBucks( Integer x );


    
    public ImperfectField<Cheese> getLikes();
    public Distribution<Cheese> getLikesDistr();
    public Cheese getLikesValue();

    public void setLikes( ImperfectField<Cheese> x );
    public void setLikesDistr( Distribution<Cheese> x );
    public void setLikesValue( Cheese x );

    public void updateLikes( ImperfectField<Cheese> x );
    public void updateLikesDistr( Distribution<Cheese> x );
    public void updateLikesValue( Cheese x );



    public static class Cheese {
        private String name;

        public Cheese() {
        }

        public Cheese(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Cheese cheese = (Cheese) o;

            if (name != null ? !name.equals(cheese.name) : cheese.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Cheese{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
