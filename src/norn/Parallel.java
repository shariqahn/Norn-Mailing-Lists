package norn;

import java.util.HashSet;
import java.util.Set;

/**
 * immutable, threadsafe datatype that represents a parallelization of list expressions
 *
 */
class Parallel implements ListExpression {
    
    private final ListExpression left, right;
    
    // Abstraction function
    //   AF(first, second) = parallelization of left | right
    // Rep invariant
    //    left and right are not null
    // Safety from rep exposure
    //    all fields are private, final, and immutable
    //    return types are all immutable
    //      - evaluate() returns a mutable set, but rep not shared with any other object or exposed to a
    //        client
    // Thread safety argument
    //    parallel list expressions are immutable
    //    use of StoredListNames is synchronized and thread safe

    /** Make a parallel operation */
    /**
     * 
     * @param left subexpression on left 
     * @param right subexpression on right 
     */
   
    public Parallel(ListExpression left, ListExpression right) {
        this.left = left;
        this.right = right;
        checkRep();
    }
    
    // Check that the rep invariant is true
    private void checkRep() {
        assert left != null;
        assert right != null;
    }
    
    @Override 
    public String toString() {
        return "(" + left.toString() + " | " + right.toString() + ")";
    }
    
    @Override
    public boolean equals(Object that) {
        return that instanceof Parallel && this.sameValue((Parallel) that);
    }
    
    // checks whether the fields of Union objects are the same
    private boolean sameValue(Parallel that) { 
        return (this.left.equals(that.left) && this.right.equals(that.right)) || 
                (this.left.equals(that.right) && this.right.equals(that.left));
    }
    
    @Override
    public int hashCode() {
        return this.left.hashCode() + this.right.hashCode();
    }
    
    @Override
    public Set<ListExpression> evaluate(StoredListNames storedLists,Set<String> seenNames) { //is this threadsafe
        Set<String> commonListNames = left.getAllListNames();
        Set<String> rightNameSet=right.getAllListNames();
        commonListNames.retainAll(rightNameSet);
        if (commonListNames.size() > 0) {
            //need throws clause?
            throw new ListDefinitionConflictException("subexpressions are defining the same list name(s)");
        }
        
        Thread rightThread = new Thread(() -> right.evaluate(storedLists,new HashSet<>(seenNames)));
        rightThread.start();
        left.evaluate(storedLists,new HashSet<>(seenNames));
        try {
            rightThread.join();
        } catch (InterruptedException e) { //need throws in javadocs comment?
            System.out.println("evaluation of right side was interrupted");
            e.printStackTrace();
        }
        
        checkRep();
        return new HashSet<>();
    }
    
    @Override
    public String structureAsString(StoredListNames storedLists, int depth) {
        String indent = "";
        for(int i = 0; i < depth; i++) {
            indent += "    ";
        }
        
        String structure = indent + "[" + left.toString() + " (parallel) " + right.toString() + "]"
                            + " evaluates to --> {" + ListExpression.stringOfExpressions(this.evaluate(storedLists,new HashSet<>())) + "}" + "\n";
        structure += indent + "    " + this.left.structureAsString(storedLists, depth + 2) + "\n ------------------------ \n";
        structure += indent + "    " + this.right.structureAsString(storedLists, depth + 2) + "\n";
        
        checkRep();
        return structure;
    }
    
    @Override
    public Set<String> getAllListNames() {
        Set<String> names = new HashSet<>();
        names.addAll(left.getAllListNames());
        names.addAll(right.getAllListNames());
        
        checkRep();
        return names;
    }
}
