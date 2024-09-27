package recitation;

import soot.jimple.DefinitionStmt;
import soot.options.*;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
  Analysis to collect variables being defined. 
  The abstract data flow value (sigma) is a set of variable names (strings).
  This is like reaching definitions, but without tracking where variables
  were defined. So, the flow functions only have a GEN and no KILL.
*/
public class IntraDefsAnalysis extends ForwardFlowAnalysis<Unit, Set<Pair<String,Integer>>> {


    IntraDefsAnalysis(UnitGraph graph) {
        super(graph);
    }

    // Runs the analysis
    public void run() {
        this.doAnalysis();
    }

    /**
     * Run flow function for this unit
     *
     * @param inValue  The initial Sigma at this point
     * @param unit     The current Unit
     * @param outValue The updated Sigma after the flow function
     */
    @Override
    protected void flowThrough(Set<Pair<String,Integer>> inValue, Unit stmt, Set<Pair<String,Integer>> outValue) {
        // First copy the sigma-in to sigma-out as-is
        outValue.addAll(inValue);

        // Check if a variable is being defined
        if (stmt instanceof DefinitionStmt) {
            DefinitionStmt def = (DefinitionStmt) stmt;
            Value lhs = def.getLeftOp();
            if (lhs instanceof Local) {
                // Get name of local variable being defined
                String varName = ((Local) lhs).getName();

                //String defText = varName + "@L" + stmt.getJavaSourceStartLineNumber();
                // Add it to the sigma
                // Guaranteed Defs Analysis
                //outValue.add(new Pair(varName, stmt.getJavaSourceStartLineNumber()));

                // Reaching Defs Analysis
                Set<Pair<String,Integer>> genSet = gen(varName, stmt.getJavaSourceStartLineNumber());
                Set<Pair<String,Integer>> killSet = kill(varName, inValue);
                //outValue = union(minus(inValue, killSet), genSet); // what we want, but doesn't work because outValue is a copy of the mem address

                outValue.clear();
                //outValue.addAll(union(inValue, genSet)); // equivalent to GDA
                outValue.addAll(union(minus(inValue, killSet), genSet));
            }
        }
    }

    private Set<Pair<String,Integer>> gen(String varName, Integer lineNum) {
        return Set.of(new Pair(varName, lineNum));
    }

    private Set<Pair<String,Integer>> kill(String varName, Set<Pair<String,Integer>> sigma) {
        return sigma.stream()
            .filter(p -> p.getO1().equals(varName))
            .collect(Collectors.toSet());
    }

    private <T> Set<T> union(Set<T> s1, Set<T> s2) {
        Set<T> u = new HashSet<>(s1);
        u.addAll(s2);
        return u;
    }

    private <T> Set<T> minus(Set<T> s1, Set<T> s2) {
        Set<T> u = new HashSet<>(s1);
        u.removeAll(s2);
        return u;
    }

    /**
     * Initial flow information at the start of a method
     */
    @Override
    protected Set<Pair<String,Integer>> entryInitialFlow() {
        return new HashSet<Pair<String,Integer>>(); // empty set
    }

    /**
     * Initial flow information at each other program point
     */
    @Override
    protected Set<Pair<String,Integer>> newInitialFlow() {
        return new HashSet<Pair<String,Integer>>(); // empty set
    }

    /**
     * Join at a program point lifted to sets
     */
    @Override
    protected void merge(Set<Pair<String,Integer>> in1, Set<Pair<String,Integer>> in2, Set<Pair<String,Integer>> out) {
        // out := in1 union in2
        out.addAll(in1);
        out.addAll(in2);
    }

    /**
     * Copy for sets
     */
    @Override
    protected void copy(Set<Pair<String,Integer>> source, Set<Pair<String,Integer>> dest) {
        // dest := source
        dest.addAll(source);
    }
}
