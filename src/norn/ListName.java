package norn;

import java.util.HashSet;
import java.util.Set;

/**
 * immutable datatype that represents a list name list expression
 *
 */
class ListName implements ListExpression {
    
    private final String name;
    
    // Abstraction function
    //   AF(name, list) = list name list expression with the given name and recipients list (once it is evaluated) <-- is this okay???
    // Rep invariant
    //    name should not be an empty string (length > 0)
    // Safety from rep exposure
    //    all fields are private, final, and immutable
    //    return types are all immutable
    //      - evaluate() returns a mutable set, but rep not shared with any other object or exposed to a
    //        client
    // Thread safety argument
    //    list names are immutable
    //    use of StoredListNames is synchronized and thread safe

    /** Make a list name list expression */
    /**
     * 
     * @param name name of subexpression
     */
    public ListName(String name) {
        this.name = name;
        checkRep();
    }
    
    // Check that the rep invariant is true
    private void checkRep() {
        assert name.length() >= 1;
    }
    
    @Override 
    public String toString() {
        return name;
    }
   
    @Override
    public boolean equals(Object that) {
        return that instanceof ListName && this.sameValue((ListName) that);
    }
    
    // checks whether the fields of Email objects are the same
    private boolean sameValue(ListName that) { 
        return this.name.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return name.length();
    }
    
    @Override
    public Set<ListExpression> evaluate(StoredListNames storedLists,Set<String> seenNames) {
        if(seenNames.contains(this.name)) {
            throw new ListDefinitionConflictException("Mail loop found");
        }
        seenNames.add(this.name);
        ListExpression listexpression = storedLists.getListExpression(name);
        Set<ListExpression> evaluated = listexpression.evaluate(storedLists,new HashSet<>(seenNames));
        checkRep();
        return evaluated;
    }
    
    /**
     * 
     * @return the name of the list
     */
    public String getName() {
        return this.name;
    }
    
    @Override
    public String structureAsString(StoredListNames storedLists, int depth) {
        String indent = "";
        for(int i = 0; i < depth; i++) {
            indent += "    ";
        }
        
        String structure = indent + "[" + this.name + "] is equal to --> {" + ListExpression.stringOfExpressions(storedLists.getEmailList(this.name)) + "}";
        // Decided to only show the list associated with this name when it is called as a list, assumming structure of how
        //      it is made has been previously defined
        // structure += "    " + storedLists.getListExpressionFromName(this.name).structureAsString(storedLists);
        return structure; 
    }
    
    @Override
    public Set<String> getAllListNames() {
        HashSet<String> resultHashSet=new HashSet<>();
        resultHashSet.add(name);
        return resultHashSet;
    }
}
