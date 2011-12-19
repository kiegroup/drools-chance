package org.drools.chance.common.trait;


import org.drools.factmodel.traits.Entity;

public class LegacyBean extends Entity {

    private String name;
    private Double weight;

    public LegacyBean(String name, Double weight) {
        this.name = name;
        this.weight = weight;
    }

    public LegacyBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "LegacyBean{" +
                "weight=" + weight +
                ", name='" + name + '\'' +
                '}';
    }
}
