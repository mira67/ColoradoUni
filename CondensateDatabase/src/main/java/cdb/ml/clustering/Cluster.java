/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package cdb.ml.clustering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The cluster in K-mean with the matrix index
 * 
 * @author Chao Chen
 * @version $Id: Cluster.java, v 0.1 2014-10-14 上午11:26:37 chench Exp $
 */
public final class Cluster implements Iterable<Integer> {

    /** the list with matrix index */
    private List<Integer> elements;

    /** 
     * Construction
     */
    public Cluster() {
        elements = new ArrayList<Integer>();
    }

    /**
     * divide a element to this cluster
     * 
     * @param index the matrix index to add
     */
    public void add(int index) {
        elements.add(index);
    }

    /**
     * clear all the index in this cluster
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Returns true if this list contains no elements
     * 
     * @return
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * return the matrix index list
     * 
     * @return
     */
    public List<Integer> getList() {
        return elements;
    }

    /**
     * calculate the centroid in the cluster
     * 
     * @param matrix
     * @return
     */
    public Point centroid(Samples samples) {
        if (elements.isEmpty()) {
            return null;
        }

        int firstElemnt = elements.get(0);
        Point result = samples.getPoint(firstElemnt);
        for (int i = 1; i < elements.size(); i++) {
            Point a = samples.getPointRef(elements.get(i));
            result = result.plus(a);
        }
        return result.scale(1.0 / elements.size());
    }

    /** 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Integer> iterator() {
        return elements.iterator();
    }
}