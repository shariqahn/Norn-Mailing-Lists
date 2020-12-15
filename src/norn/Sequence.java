package norn;

import java.util.HashSet;
import java.util.Set;

/**
 * immutable datatype that represents a sequence of list expressions
 *
 */
class Sequence implements ListExpression {
    
    private final ListExpression first, second;
    
    // Abstraction function
    //   AF(first, second) = sequence of first ; second
    // Rep invariant
    //    first and second are not null
    // Safety from rep exposure
    //    all fields are private, final, and immutable
    //    return types are all immutable
    //      - evaluate() returns a mutable set, but rep not shared with any other object or exposed to a
    //        client
    // Thread safety argument
    //    sequence of list expressions is immutable
    //    use of StoredListNames is synchronized and thread safe

     /** Make a sequence operation */
     /**
      * 
      * @param first subexpression on first 
      * @param second subexpression on second 
      */
    
     public Sequence(ListExpression first, ListExpression second) {
         this.first = first;
         this.second = second;
         checkRep();
     }
     
     // Check that the rep invariant is true
     private void checkRep() {
         assert first != null;
         assert second != null;
     }
         
     @Override 
     public String toString() {
         return "(" + first.toString() + " ; " + second.toString() + ")";
     }
    
     @Override
     public boolean equals(Object that) {
         return that instanceof Sequence && this.sameValue((Sequence) that);
     }
     
     // checks whether the fields of Union objects are the same
     private boolean sameValue(Sequence that) { 
         return this.first.equals(that.first) && this.second.equals(that.second);
     }
     
     @Override
     public int hashCode() {
         return this.first.hashCode() + this.second.hashCode();
     }
     
     @Override
     public Set<ListExpression> evaluate(StoredListNames storedLists,Set<String> seenNames) {
         first.evaluate(storedLists,new HashSet<>(seenNames));
         checkRep();
         return second.evaluate(storedLists,new HashSet<>(seenNames));
     }
     
     @Override
     public String structureAsString(StoredListNames storedLists, int depth) {
         String indent = "";
         for(int i = 0; i < depth; i++) {
             indent += "    ";
         }
         
         String structure = indent + this.first.structureAsString(storedLists, depth) + "\n" + indent + this.second.structureAsString(storedLists, depth);
         
         checkRep();
         return structure;
     }
     
     @Override
     public Set<String> getAllListNames() {
         Set<String> names = new HashSet<>();
         names.addAll(first.getAllListNames());
         names.addAll(second.getAllListNames());
         
         checkRep();
         return names;
     }
}
