package edu.utulsa.masters.search;

import java.util.List;

/**
 * Created by chad on 4/26/16.
 */
public interface NodeEvaluatable<T> {
    public double f();
    public List<T> search();
}
