package edu.utulsa.masters.shop2;

import java.util.Arrays;

/**
 * Created by chad on 5/1/16.
 */
public class Task {
    protected String head;
    protected Variable[] variables;

    public Task(String head, Variable... variables) {
        this.head = head;
        this.variables = variables;
    }

    public String getHead() {
        return head;
    }

    public Variable[] getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return String.format("%s %s", head, Arrays.toString(variables));
    }
}
