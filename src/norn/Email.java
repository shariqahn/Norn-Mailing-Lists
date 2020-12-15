package norn;

import java.util.HashSet;
import java.util.Set;

/**
 * immutable datatype that represents an email address list expression
 */
class Email implements ListExpression {
    
    private final String username;
    private final String domain; 
    
    // Abstraction function
    //   AF(username, domain) = email address username@domain
    // Rep invariant
    //    username and domain should each have length >= 1
    // Safety from rep exposure
    //    all fields are private, final, and immutable
    //    return types are all immutable
    //      - evaluate() returns a mutable set, but defensive copies are made
    // Thread safety argument
    //    emails are immutable
    //    use of StoredListNames is synchronized and thread safe
    
    /** Make an email address */
    /**
     * 
     * @param username  nonempty username of email 
     * @param domain    nonempty domain of email 
     */
    public Email(String username, String domain) {
        this.username = username;
        this.domain = domain;
        checkRep();
    }
    
    // Check that the rep invariant is true
    private void checkRep() {
        assert username.length() >= 1;
        assert domain.length() >= 1;
    }
    
    @Override 
    public String toString() {
        return username + "@" + domain;
    }
   
    @Override
    public boolean equals(Object that) {
        return that instanceof Email && this.sameValue((Email) that);
    }
    
    // checks whether the fields of Email objects are the same
    private boolean sameValue(Email that) { 
        return this.username.equals(that.username) && this.domain.equals(that.domain);
    }
    
    @Override
    public int hashCode() {
        return this.username.length() + this.domain.length();
    }
    
    @Override
    public Set<ListExpression> evaluate(StoredListNames storedLists,Set<String> seenNames) {
        Set<ListExpression> evaluated = new HashSet<>();
        evaluated.add(new Email(username, domain));
        checkRep();
        return evaluated;
    }
    
    @Override
    public String structureAsString(StoredListNames storedLists, int depth) {
        String indent = "";
        for(int i = 0; i < depth; i++) {
            indent += "    ";
        }
        
        checkRep();
        return indent + "[" + this.toString() + "] evaluates to --> {" + this.toString() + "}";
    }
    
    @Override
    public Set<String> getAllListNames() {
        return new HashSet<>();
    }
}
