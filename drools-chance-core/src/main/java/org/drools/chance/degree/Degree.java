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

package org.drools.chance.degree;

import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.kie.internal.runtime.beliefs.Mode;

import java.io.Serializable;


/**
 * Interface for any class implementing the concept of degree.
 * Possible subclasses may include
 * - degree of truth
 * - degree of probability
 * - degree of possibility
 * - degree of belief
 * - degree of confidence
 * - combinations thereof
 * - ...
 *
 */
public interface Degree extends Comparable<Degree>, Serializable {

    /**
     * @return the degree, narrowed down to a simple double value
     */
	public double getValue();

    public void setValue( double d );

    /**
     * @return the degree, narrowed down to a boolean
     */
	public boolean toBoolean();

    /**
     * @return the degree of confidence associated to this degree.
     * (Confidence is a second-order concept, which may be modelled using a Degree
     * In that case, this method may delegate to getValue(), invoked on the confidence Degree)
     */
	public double getConfidence();

    /**
     * @return the degree, cast to an interval representation
     */
	public IntervalDegree asIntervalDegree();

    /**
     * @return the degree, cast to a simple degree representation
     */
	public SimpleDegree asSimpleDegree();


    /**
     * @return the representation of true / top / 1 / 100% ...,
     * according to the semantics of the current Degree
     */
	public Degree True();

    /**
     * @return the representation of false / bot / 0 / 0% ...,
     * according to the semantics of the current Degree
     */
	public Degree False();

    /**
     * @return the representation of unknown, according to
     * the semantics of the current Degree (if allowed)
     */
	public Degree Unknown();


    /**
     * Computes a new degree which is the ''sum'' of this and term
     * @param term
     * @return  this "+" term
     */
    public Degree sum(Degree term);


    /**
     * Computes a new degree which is the ''product'' of this and factor
     * @param factor
     * @return  this "*" factor
     */
    public Degree mul(Degree factor);

    /**
     * Computes a new degree which is the ''quotient'' of this and div
     * @param div
     * @return  this "/" div
     */
    public Degree div(Degree div);

    /**
     * Computes a new degree subtracting term to this
     * @param term
     * @return  this "-" term
     */
    public Degree sub(Degree term);


    /**
     * Computes a new degree taking the maximum between this and other
     * @param other
     * @return  max(this, other)
     */
    public Degree max(Degree other);


    /**
     * Computes a new degree taking the minimum between this and other
     * @param other
     * @return  min(this, other)
     */
    public Degree min(Degree other);



    /**
     * (instance) factory method: creates a new degree from a simple value.
     * @param val
     * @return  A degree such that this.asSimpleDegree().getValue() == val
     */
    public Degree fromConst(double val);


    /**
     * (instance) factory method: creates a new degree from a simple value.
     * @param val
     * @return  A degree such that this.asSimpleDegree().getValue() == val
     */
    public Degree fromString( String val );


    /**
     * (instance) factory method: creates a new degree from a boolean value.
     * @param val
     * @return  A degree such that this.asSimpleDegree().getValue() == val
     */
    public Degree fromBoolean(boolean val);


}
