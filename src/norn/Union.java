package norn;

import java.util.HashSet;
import java.util.Set;

/**
 *  immutable datatype that represents a set union of list expressions
 *
 */
public class Union implements ListExpression {
    
    private final ListExpression left, right;
    
    // Abstraction function
    //   AF(left, right) = set union of left,right
    // Rep invariant
    //    left and right are not null
    // Safety from rep exposure
    //    all fields are private, final, and immutable
    //    return types are all immutable
    //      - evaluate() returns a mutable set, but rep not shared with any other object or exposed to a
    //        client
    // Thread safety argument
    //    union of list expressions are immutable
    //    use of StoredListNames is synchronized and thread safe
    
    /** Make a union operation */
    /**
     * 
     * @param left subexpression on left 
     * @param right subexpression on right 
     */
    public Union(ListExpression left, ListExpression right) {
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
        return "(" + left.toString() + " , " + right.toString() + ")"; //should this be changed to the {} notation from the spec
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Union && this.sameValue((Union) that);
    }
    
    // checks whether the fields of Union objects are the same
    private boolean sameValue(Union that) { 
        return (this.left.equals(that.left) && this.right.equals(that.right)) || 
                (this.left.equals(that.right) && this.right.equals(that.left));
    }
    
    @Override
    public int hashCode() {
        return this.left.hashCode() + this.right.hashCode();
    }
    
    @Override
    public Set<ListExpression> evaluate(StoredListNames storedLists, Set<String> seenName) {
        Set<ListExpression> evaluated = new HashSet<>();
        evaluated.addAll(left.evaluate(storedLists,new HashSet<>(seenName)));
        evaluated.addAll(right.evaluate(storedLists,new HashSet<>(seenName)));
        
        checkRep();
        return evaluated;
    }
    
    @Override
    public String structureAsString(StoredListNames storedLists, int depth) {
        String indent = "";
        for(int i = 0; i < depth; i++) {
            indent += "    ";
        }
        
        String structure = indent + "[" + left.toString() + " (and) " + right.toString() + "]"
                            + " evaluates to --> {" + ListExpression.stringOfExpressions(this.evaluate(storedLists,new HashSet<>())) + "} \n";
        structure += indent + "    " + this.left.structureAsString(storedLists, depth + 2) + "\n";
        structure += indent + "    " + this.right.structureAsString(storedLists, depth + 2);   
        
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
