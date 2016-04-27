package edu.utulsa.masters.search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by chad on 4/26/16.
 */
public class TreeSearch<T extends NodeEvaluatable<T>> {
    protected int MAX_ITERATIONS = 1000;
    protected final T start;
    protected final T end;

    private TreeSearch(T start, T end) {
        this.start = start;
        this.end = end;
    }

    public class Node implements Comparable<Node> {
        Node parent;
        T item;

        public Node(Node parent, T item) {
            this.parent = parent;
            this.item = item;
        }

        public T parentItem() {
            if(parent == null) return null;
            else return parent.item;
        }

        public LinkedList<T> path() {
            LinkedList<T> path;

            if(parent == null) path = new LinkedList<T>();
            else path = parent.path();

            path.add(item);
            return path;
        }

        @Override
        public int compareTo(Node other) {
            return (this.item.f() >= other.item.f()) ? 1 : -1;
        }
    }

    private LinkedList<T> doSearch() {
        HashSet<T> history = new HashSet<T>();
        PriorityQueue<Node> open = new PriorityQueue<Node>();
        history.add(start);

        Node s = new Node(null, start);
        open.add(s);

        int t = 0;
        while(true) {
            if(t > MAX_ITERATIONS) return null;

            Node best = open.poll();

            List<T> nodes = best.item.search();
            for(T item: nodes) {
                if(history.contains(item)) continue;
                if(item.equals(end)) {
                    Node n = new Node(best, item);
                    return n.path();
                }
                Node n = new Node(best, item);
                open.add(n);
                history.add(item);
            }
            t++;
        }
    }

    public static<T extends NodeEvaluatable<T>> LinkedList<T> search(T start, T end) {
        TreeSearch<T> ts = new TreeSearch<T>(start, end);
        return ts.doSearch();
    }
}
