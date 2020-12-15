package norn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 *  Mutable and thread-safe data type that acts as a global storage unit for all clients accessing / mutating defined email lists.
 *
 */
public class StoredListNames {
    
    /*
     * Order of things happening:
     *      Parser.parse() creates a ListExpression, StoredLists does not yet have list definitions update
     *      ListExpression.evaluate() starts to evaluate shit and when it runs into a listName it looks for it here
     *      If evaluating ListName, then the expression here is updated with ListName expression
     */
    
    private final Map<String, ListExpression> storedLists; 
    
    // Abstraction function
    //    AF(storedLists): a storage unit for all defined list names, where a key in storedLists is the name of an email-list and the
    //                   value associated with that key is the ListExpression object representing it, such that the structure of
    //                   how the list is defined can be remembered.
    // Rep invariant
    //    true
    // Safety from rep exposure
    //    all fields are private and final
    //    
    //    all other return types are immutable String or void
    // Thread safety argument
    //    monitor pattern - all accesses to storedLists happen within StoredListNames methods,
    //    which are all guarded by StoredListNames' lock
    

    /** Make a StoredList object */
    public StoredListNames() {
        this.storedLists = new HashMap<String, ListExpression>();
    }
    
    /**
     * @param listName name of the list
     * @param listExpression being stored
     */
    public synchronized void updateList(String listName, ListExpression listExpression) {
        
        this.storedLists.put(listName, listExpression);
    }
    
    /**
     * 
     * @param listName being retrieved
     * @return corresponding list expressions
     */
    public synchronized Set<ListExpression> getEmailList(String listName) {
        if (!storedLists.containsKey(listName)) {
            storedLists.put(listName, new EmptyList());
        }
        
        return storedLists.get(listName).evaluate(this,new HashSet<>()); //add to RI
    }
    
    /**
     * 
     * @param listName being retrieved
     * @return corresponding list expression
     */
    public synchronized ListExpression getListExpression(String listName) {
        if (!storedLists.containsKey(listName)) {
            storedLists.put(listName, new EmptyList());
        }
        
        return storedLists.get(listName); //add to RI
    }
    
    /**
     * used by the console to output all defined lists when a file has been loaded
     * @return all stored list definitions
     */
    public synchronized String allDefinitions() {
        String out = "";
        for(String name : this.storedLists.keySet()) {
            out += (name + " = ");
            Set<ListExpression> emailSet = this.storedLists.get(name).evaluate(this,new HashSet<>());
            String emailString = "";
            for (ListExpression email : emailSet) {
                emailString += email.toString() + ",";
            }
            if (emailString.endsWith(","))
                emailString = emailString.substring(0, emailString.length() - 1); 
            out += (emailString + "\n");
        }
        out = out.trim();
        return out; 
    }
    
    //used by the console for the save function
    /**
     * create a string that can be parsed as a list expression that will be parsed such that 
     * all of the stored lists are recreated..
     * 
     * @return a String that can be parsed into a ListExpression representing all currently defined lists
     */
    public synchronized String createSingleExpression() {
        String singleExpression = "";
        for(String name : this.storedLists.keySet()) {
            singleExpression += "( " + new ListNameDefinition(name, this.storedLists.get(name)).toString() + ");";
        }
        
        // remove the extra semicolon
        if (singleExpression.length() > 0) {
            singleExpression = singleExpression.substring(0, singleExpression.length() - 1);   
        }
        else {
            singleExpression = " ";
        }
        
        return singleExpression;
    }
    
}
