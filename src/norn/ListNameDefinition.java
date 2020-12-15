package norn;

import java.util.HashSet;
import java.util.Set;

/**
 * immutable datatype that represents the definition of a list name list expression
 *
 */
public class ListNameDefinition implements ListExpression {

    private final String name;
    private final ListExpression listExpression; 
    
    // Abstraction function
    //   AF(name, listExpression) = list name list expression with the given name and recipients listExpression (once it is evaluated) 
    // Rep invariant
    //    name should not be an empty string (length > 0)
    //    listExpression should not be null
    // Safety from rep exposure
    //    all fields are private, final, and immutable
    //    return types are all immutable
    //      - evaluate() returns a mutable set, but rep not shared with any other object or exposed to a
    //        client
    // Thread safety argument
    //    list names are immutable
    //    use of StoredListNames is synchronized and thread safe

    /** Make a list name definition */
    /**
     * 
     * @param name name of subexpression
     * @param newList subexpression represented by name
     */
    public ListNameDefinition(String name, ListExpression newList) {
        this.name = name;
        this.listExpression = newList;
        checkRep();
    }
    
    // Check that the rep invariant is true
    private void checkRep() {
        assert name.length() >= 1;
        assert listExpression != null;
    }
    
    @Override 
    public String toString() {
        return "(" + name + " = " + listExpression.toString() + ")";
    }
   
    @Override
    public boolean equals(Object that) {
        return that instanceof ListNameDefinition && this.sameValue((ListNameDefinition) that);
    }
    
    // checks whether the fields of Email objects are the same
    private boolean sameValue(ListNameDefinition that) { 
        return this.name.equals(that.name) && this.listExpression.equals(that.listExpression);
    }
    
    @Override
    public int hashCode() {
        return name.length() + listExpression.hashCode();
    }
    
    @Override
    public Set<ListExpression> evaluate(StoredListNames storedLists,Set<String> seenNames) {
       
        seenNames.add(this.name);
        Set<ListExpression> evaluated = listExpression.evaluate(storedLists, new HashSet<>(seenNames));
        storedLists.updateList(name, listExpression);
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
        
        String structure = indent + "[" + this.toString() + "] evaluates to --> {" + ListExpression.stringOfExpressions(storedLists.getEmailList(this.name)) + "}\n";
        structure += indent + "    " + storedLists.getListExpression(this.name).structureAsString(storedLists, depth + 2);
        return structure; 
    }
    
    @Override
    public Set<String> getAllListNames() {
        HashSet<String> resultHashSet=new HashSet<>();
        resultHashSet.add(name);
        resultHashSet.addAll(listExpression.getAllListNames());
        return resultHashSet;
    }
}
