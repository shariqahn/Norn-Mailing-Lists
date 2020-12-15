package norn;

import java.util.HashSet;
import java.util.Set;

/**
 * immutable datatype that represents an empty list definition (no EmptyLists)
 * 
 */
public class EmptyList implements ListExpression {
    
    private final String name;
    
    // Abstraction function
    //   AF(name) = empty string list expression with name empty string
    // Rep invariant
    //    true
    // Safety from rep exposure
    //    all fields are private, final, and immutable
    //    return types are all immutable
    //      - evaluate() returns a mutable set, but rep not shared with any other object or exposed to a
    //        client
    // Thread safety argument
    //    EmptyLists are immutable
    //    use of StoredListNames is synchronized and thread safe
    
    /** Make an empty string list expression */
    public EmptyList() {
        name = "";
    }

    @Override 
    public String toString() {
        return "";
    }
   
    @Override
    public boolean equals(Object that) {
        return that instanceof EmptyList && this.sameValue((EmptyList) that);
    }
    
    // checks whether the fields of EmptyList objects are the same
    private boolean sameValue(EmptyList that) { 
        return this.name.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return name.length();
    }
    
    @Override
    public Set<ListExpression> evaluate(StoredListNames storedLists,Set<String> seenNames) {
        return new HashSet<>();
    }
    
    @Override
    public String structureAsString(StoredListNames storedLists, int depth) {
        return "";
    }
    
    @Override
    public Set<String> getAllListNames() {
        return new HashSet<String>();
    }
}
