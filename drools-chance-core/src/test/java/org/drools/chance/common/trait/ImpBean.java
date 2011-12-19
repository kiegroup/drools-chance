package org.drools.chance.common.trait;


import org.drools.chance.common.IImperfectField;
import org.drools.chance.common.fact.Price;
import org.drools.chance.common.fact.Weight;
import org.drools.chance.distribution.IDistribution;

public interface ImpBean {

    //TODO Update by field ??

    public IImperfectField<String> getName();
    public IDistribution<String> getNameDistr();
    public String getNameValue();

    public void setName( IImperfectField<String> x );
    public void setNameDistr( IDistribution<String> x );
    public void setNameValue( String x );

    public void updateName( IImperfectField<String> x  );
    public void updateNameDistr( IDistribution<String> x );
    public void updateNameValue( String x );

    
    
    
    public IImperfectField<Boolean> getFlag();
    public IDistribution<Boolean> getFlagDistr();
    public Boolean getFlagValue();

    public void setFlag( IImperfectField<Boolean> x );
    public void setFlagDistr( IDistribution<Boolean> x );
    public void setFlagValue( Boolean x );

    public void updateFlag( IImperfectField<Boolean> x );
    public void updateFlagDistr( IDistribution<Boolean> x );
    public void updateFlagValue( Boolean x );
    
    
    
    
    public IImperfectField<Integer> getAge();
    public IDistribution<Integer> getAgeDistr();
    public Integer getAgeValue();

    public void setAge( IImperfectField<Integer> x );
    public void setAgeDistr( IDistribution<Integer> x );
    public void setAgeValue( Integer x );

    public void updateAge( IDistribution<Integer> x );
    public void updateAge( Integer x ); 

    
    
    
    
    public IImperfectField<Weight> getBody();
    public IDistribution<Weight> getBodyDistr();
    public Weight getBodyValue();

    public void setBody( IImperfectField<Weight> x );
    public void setBodyDistr( IDistribution<Weight> x );
    public void setBodyValue( Weight x );

    public void updateBody( IDistribution<Weight> x );
    public void updateBody( Weight x );
    

    public Double getWeight();
    public void setWeight( Double x );

    
    
    public IImperfectField<Price> getPrice();
    public IDistribution<Price> getPriceDistr();
    public Price getPriceValue();

    public void setPrice( IImperfectField<Price> x );
    public void setPriceDistr( IDistribution<Price> x );
    public void setPriceValue( Price x );

    public void updatePrice( IDistribution<Price> x );
    public void updatePrice( Price x );
        

    public Integer getBucks();
    public void setBucks( Integer x );


    
    public IImperfectField<Cheese> getLikes();
    public IDistribution<Cheese> getLikesDistr();
    public Cheese getLikesValue();

    public void setLikes( IImperfectField<Cheese> x );
    public void setLikesDistr( IDistribution<Cheese> x );
    public void setLikesValue( Cheese x );

    public void updateLikes( IDistribution<Cheese> x );
    public void updateLikes( Cheese x ); 



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
    }
}
