package norn;

import java.util.Set;

/**
 * 
 * An immutable date type representing a mailing list expression 
 *
 */
public interface ListExpression {
    
    //Datatype Definition
    //   List=Email(username:String,domain:String)
    //          + Union(left:ListExpression,Right:ListExpression)
    //          + Difference(left:ListExpression,right:ListExpression)
    //          + Intersection(left:ListExpression,right:ListExpression)
    //          + Sequence(first:ListExpression,second:ListExpression)
    //          + Parallel(left:ListExpression,right:ListExpression)
    //          + ListName(name:String)
    //          + ListNameDefinition(name:String,expression:ListExpression
    
    /**
     * Parse a string into a ListExpression object.
     * 
     * @param input list expression to parse
     * @return list expression AST for the input
     * @throws IllegalArgumentException if the list is syntactically invalid.
     */
    public static ListExpression parse(String input) {
        try {
            return ListParser.parse(input);
         
     } catch (Exception e) {
          throw new IllegalArgumentException("The expression could not be parsed");
     }
    }
    
    /**
     * List all of the given list expressions in a readable manner
     * @param listExpressions to be listed
     * @return list of list expressions
     */
    public static String stringOfExpressions(Set<ListExpression> listExpressions) {
        String resultString = "";
        for (ListExpression expr : listExpressions) {
            resultString += expr.toString() + ",";
        }
        if (!resultString.equals(""))
            resultString = resultString.substring(0, resultString.length() - 1); 
        
        return resultString;
    }
    
    /**
     * @return a parsable representation of this list expression, such that
     *         for all e:ListExpression, e.equals(ListExpression.parse(e.toString()))
     */
    @Override 
    public String toString();

    /**
     * @param that any object
     * @return true if and only if this and that are structurally-equal
     *         ListExpressions
     */
    @Override
    public boolean equals(Object that);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     *         equality, such that for all e1,e2:ListExpression,
     *             e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
   
    /**
     * @param storedLists where shared list expressions are stored
     * @param seenNames a set of strings that have been "seen" in an expression, to prevent email loops
     * @return recipients of mailing list list expression represents
     */
    public Set<ListExpression> evaluate(StoredListNames storedLists,Set<String> seenNames); 
    
    /**
     * Create a string representation of the current list expression structure and evaluation results
     * 
     * @param storedLists where shared list expressions are stored
     * @param depth int that keeps track of the depth of the subexpression
     * @return a string representation of the current list expression structure and evaluation results
     */
    public String structureAsString(StoredListNames storedLists, int depth);

    /**
     * @return all the names of the list names in the current list expression
     */
    public Set<String> getAllListNames();
}
