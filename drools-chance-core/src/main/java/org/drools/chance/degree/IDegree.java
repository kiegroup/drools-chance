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
 * @author sotty
 */
public interface IDegree extends Comparable<IDegree> {

    /**
     * @return the degree, narrowed down to a simple double value
     */
	public double getValue();

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
	public IDegree True();

    /**
     * @return the representation of false / bot / 0 / 0% ...,
     * according to the semantics of the current Degree
     */
	public IDegree False();

    /**
     * @return the representation of unknown, according to
     * the semantics of the current Degree (if allowed)
     */
	public IDegree Unknown();


    /**
     * Computes a new degree which is the ''sum'' of this and term
     * @param term
     * @return  this "+" term
     */
    public IDegree sum(IDegree term);


    /**
     * Computes a new degree which is the ''product'' of this and factor
     * @param factor
     * @return  this "*" factor
     */
    public IDegree mul(IDegree factor);

    /**
     * Computes a new degree which is the ''quotient'' of this and div
     * @param div
     * @return  this "/" div
     */
    public IDegree div(IDegree div);

    /**
     * Computes a new degree subtracting term to this
     * @param term
     * @return  this "-" term
     */
    public IDegree sub(IDegree term);



    /**
     * Computes a new degree taking the maximum between this and other
     * @param other
     * @return  max(this, other)
     */
    public IDegree max(IDegree other);


    /**
     * Computes a new degree taking the minimum between this and other
     * @param other
     * @return  min(this, other)
     */
    public IDegree min(IDegree other);



    /**
     * (instance) factory method: creates a new degree from a simple value.
     * @param val
     * @return  A degree such that this.asSimpleDegree().getValue() == val
     */
    public IDegree fromConst(double val);


    /**
     * (instance) factory method: creates a new degree from a simple value.
     * @param val
     * @return  A degree such that this.asSimpleDegree().getValue() == val
     */
    public IDegree fromString(String val);


}
