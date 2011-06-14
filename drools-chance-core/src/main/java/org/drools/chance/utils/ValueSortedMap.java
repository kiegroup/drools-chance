package org.drools.chance.utils;

import java.util.*;

/**
 * This is an implementation of the Map interface, which is sorted
 * according to the natural order of the <emph>Value</emph> class,
 * or by the comparator for the <emph>Values</emph> that is provided
 * at creation time, depending on which constructor is used.

 *

 * Since this class is backed by a TreeMap, it still has log(n) time
 * cost for the containsKey, get, put and remove operations.
 *
 * @author Marco13, http://java-forum.org
 *
 * @param <K> The type of the keys in this ValueSortedMap
 * @param <V> The type of the values in this ValueSortedMap
 */
public class ValueSortedMap<K, V> implements Map<K, V>
{
    /**
     * The backing, sorted map (a TreeMap), which contains the
     * key-value pairs sorted according to the value.
     */
    private Map<K, V> map;

    /**
     * The map which will be used for lookup operations. When two
     * keys have to be compared, then their values will be looked
     * up in this map, and the result of the comparison of the
     * values will be returned.
     */
    private Map<K, V> lookupMap = new HashMap<K, V>();

    /**
     * The class performing the actual comparison of the keys.
     * The values for the keys are looked up in the lookupMap.
     * Then the values will be compared. If the values for both
     * keys are equal, then the keys themself will be compared.
     * If the keys are not comparable, then an arbitrary (but
     * constant) value will be returned, to indicate that the
     * keys are not equal.

     *

     * The values will either be compared using the Comparator
     * that was given in the constructor, or cast to Comparable
     * and compared according to their natural ordering.
     */
    private class KeyByValueComparator implements Comparator<K>
    {
        /**
         * The Comparator for the values
         */
        private Comparator<? super V> valueComparator = null;

        /**
         * Creates a new KeyByValueComparator, which will compare the
         * values with the given Comparator. If the given comparator
         * is <tt>null</tt>, then the values will be compared
         * according to their natural ordering.
         *
         * @param comparator The Comparator which will be used
         * to compare the values.
         */
        KeyByValueComparator(Comparator<? super V> comparator)
        {
            if (comparator == null)
            {
                this.valueComparator = new Comparator<V>()
                {
                    public int compare(V a, V b)
                    {
                        Comparable<? super V> ca = (Comparable<? super V>) a;
                        return ca.compareTo(b);
                    }
                };
            }
            else
            {
                this.valueComparator = comparator;
            }
        }

        /**
         * Compares the values that are associated with the given
         * keys. If the values for both keys are equal, then the
         * keys themself will be compared. If the keys are not
         * comparable, then an arbitrary (but constant) value
         * will be returned, to indicate that the keys are not
         * equal.

         *

         * Note that the case of equal keys has already been
         * checked in the put(K,V) method, so that this method
         * will never receive two equal keys.
         */
        public int compare(K a, K b)
        {
            V va = lookupMap.get(a);
            V vb = lookupMap.get(b);

            int valueResult = 0;
            if (va != null && vb != null) {
                valueResult = valueComparator.compare(va, vb);
            }
            if (valueResult != 0)
                return - valueResult;
            if (a instanceof Comparable)
            {
                Comparable ca = (Comparable) a;
                return - ca.compareTo(b);
            }
            return - 1;
        }
    }

    /**
     * Constructs a new, empty ValueSortedMap, using the natural ordering of
     * its values. All values inserted into the map must implement the
     * Comparable interface.  Furthermore, all such values must be
     * mutually comparable: <tt>v1.compareTo(v2)</tt> must not throw
     * a <tt>ClassCastException</tt> for any values <tt>v1</tt> and
     * <tt>v2</tt> in the map.  If the user attempts to put avalue into the
     * map that violates this constraint (for example, the user attempts to
     * put a string value into a map whose values are integers), the
     * <tt>put(Object key, Object value)</tt> call will throw a
     * <tt>ClassCastException</tt>.
     */
    public ValueSortedMap()
    {
        map = new TreeMap<K, V>(new KeyByValueComparator(null));
    }

    /**
     * Constructs a new, empty ValueSortedMap, ordered according to the given
     * comparator.  All values inserted into the map must be mutually
     * comparable by the given comparator: <tt>comparator.compare(v1,
     * v2)</tt> must not throw a <tt>ClassCastException</tt> for any values
     * <tt>v1</tt> and <tt>v2</tt> in the map.  If the user attempts to put
     * a value into the map that violates this constraint, the <tt>put(Object
     * key, Object value)</tt> call will throw a
     * <tt>ClassCastException</tt>.
     *
     * @param c the comparator that will be used to order this map.
     *        If <tt>null</tt>, the natural ordering of the values will be used.
     */
    public ValueSortedMap(Comparator<? super V> c)
    {
        map = new TreeMap<K, V>(new KeyByValueComparator(c));
    }

    /**
     * Constructs a new ValueSortedMap containing the same mappings as the given
     * map, ordered according to the natural ordering of its values.
     * All values inserted into the new map must implement the Comparable
     * interface.  Furthermore, all such values must be
     * mutually comparable: <tt>v1.compareTo(v2)</tt> must not throw
     * a <tt>ClassCastException</tt> for any values <tt>v1</tt> and
     * <tt>v2</tt> in the map.  This method runs in n*log(n) time.
     *
     * @param  m the map whose mappings are to be placed in this map
     * @throws ClassCastException if the values in m are not Comparable,
     *         or are not mutually comparable
     * @throws NullPointerException if the specified map is null
     */
    public ValueSortedMap(Map<? extends K, ? extends V> m)
    {
        map = new TreeMap<K, V>(m);
    }

    /**
     * Constructs a new ValueSortedMap map containing the same mappings as the given
     * map, ordered according to the natural ordering of its values.
     * All values inserted into the new map must implement the Comparable
     * interface.  Furthermore, all such values must be
     * mutually comparable: <tt>v1.compareTo(v2)</tt> must not throw
     * a <tt>ClassCastException</tt> for any values <tt>v1</tt> and
     * <tt>v2</tt> in the map.  This method runs in n*log(n) time.
     *
     * @param  m the map whose mappings are to be placed in this map
     * @throws ClassCastException if the values in m are not Comparable,
     *         or are not mutually comparable
     * @throws NullPointerException if the specified map is null
     */
    public ValueSortedMap(SortedMap<? extends K, ? extends V> m)
    {
        map = new TreeMap<K, V>(m);
    }

    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this map
     */
    public int size()
    {
        return map.size();
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.  More formally, returns <tt>true</tt> if and only if
     * this map contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key
     * @throws ClassCastException if the key is of an inappropriate type for
     *         this map
     * @throws NullPointerException if the specified key is null and this map
     *         does not permit null keys
     */
    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.  More formally, returns <tt>true</tt> if and only if
     * this map contains at least one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
     * will probably require time linear in the map size for most
     * implementations of the <tt>Map</tt> interface.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     * @throws ClassCastException if the value is of an inappropriate type for
     *         this map
     * @throws NullPointerException if the specified value is null and this
     *         map does not permit null values
     */
    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     *

More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     *

If this map permits null values, then a return value of
     * {@code null} does not necessarily indicate that the map
     * contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to {@code null}.  The containsKey
     * operation may be used to distinguish these two cases.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     * @throws ClassCastException if the key is of an inappropriate type for
     *         this map
     * @throws NullPointerException if the specified key is null and this map
     *         does not permit null keys
     */
    public V get(Object key)
    {
        return map.get(key);
    }

    /**
     * Associates the specified value with the specified key in this map
     * If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.  (A map
     * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
     * if m.containsKey(k) would return
     * <tt>true</tt>.)
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>,
     *         if the implementation supports <tt>null</tt> values.)
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this map
     * @throws NullPointerException if the specified key or value is null
     *         and this map does not permit null keys or values
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this map
     */
    public V put(K key, V value)
    {
        if (lookupMap.containsKey(key))
        {
            map.remove(key);
        }
        lookupMap.put(key, value);
        return map.put(key, value);
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * More formally, if this map contains a mapping
     * from key <tt>k</tt> to value <tt>v</tt> such that
     * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping
     * is removed.  (The map can contain at most one such mapping.)
     *
     *

Returns the value to which this map previously associated the key,
     * or <tt>null</tt> if the map contained no mapping for the key.
     *
     *

If this map permits null values, then a return value of
     * <tt>null</tt> does not necessarily indicate that the map
     * contained no mapping for the key; it's also possible that the map
     * explicitly mapped the key to <tt>null</tt>.
     *
     *

The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * @throws ClassCastException if the key is of an inappropriate type for
     *         this map
     * @throws NullPointerException if the specified key is null and this
     *         map does not permit null keys
     */
    public V remove(Object key)
    {
        lookupMap.remove(key);
        return map.remove(key);
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * The effect of this call is equivalent to that
     * of calling put(k, v) on this map once
     * for each mapping from key <tt>k</tt> to value <tt>v</tt> in the
     * specified map.  The behavior of this operation is undefined if the
     * specified map is modified while the operation is in progress.
     *
     * @param otherMap mappings to be stored in this map
     * @throws ClassCastException if the class of a key or value in the
     *         specified map prevents it from being stored in this map
     * @throws NullPointerException if the specified map is null, or if
     *         this map does not permit null keys or values, and the
     *         specified map contains null keys or values
     * @throws IllegalArgumentException if some property of a key or value in
     *         the specified map prevents it from being stored in this map
     */
    public void putAll(Map<? extends K, ? extends V> otherMap)
    {
        lookupMap.putAll(otherMap);
        for (K k : otherMap.keySet())
        {
            put(k, otherMap.get(k));
        }
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear()
    {
        lookupMap.clear();
        map.clear();
    }

    /**
     * Returns a Set view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<K> keySet()
    {
        return map.keySet();
    }

    /**
     * Returns a Set view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    public Set<Entry<K, V>> entrySet()
    {
        return map.entrySet();
    }

    /**
     * Returns a Collection view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection<V> values()
    {
        return map.values();
    }

    /**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two maps
     * represent the same mappings.  More formally, two maps <tt>m1</tt> and
     * <tt>m2</tt> represent the same mappings if
     * <tt>m1.entrySet().equals(m2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * @param o object to be compared for equality with this map
     * @return <tt>true</tt> if the specified object is equal to this map
     */
    public boolean equals(Object o)
    {
        return map.equals(o);
    }

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * <tt>entrySet()</tt> view.  This ensures that <tt>m1.equals(m2)</tt>
     * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
     * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
     * hashCode.
     *
     * @return the hash code value for this map
     * @see java.util.Map.Entry#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    public int hashCode()
    {
        return map.hashCode();
    }

    /**
     * Returns a string representation of this map.  The string representation
     * consists of a list of key-value mappings in the order returned by the
     * map's <tt>entrySet</tt> view's iterator, enclosed in braces
     * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
     * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
     * the key followed by an equals sign (<tt>"="</tt>) followed by the
     * associated value.  Keys and values are converted to strings as by
     * String#valueOf(Object).
     *
     * @return a string representation of this map
     */
    public String toString()
    {
        return map.toString();
    }

}
