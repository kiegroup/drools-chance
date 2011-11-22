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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LegacyBean that = (LegacyBean) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (weight != null ? !weight.equals(that.weight) : that.weight != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LegacyBean{" +
                "weight=" + weight +
                ", name='" + name + '\'' +
                '}';
    }
}
