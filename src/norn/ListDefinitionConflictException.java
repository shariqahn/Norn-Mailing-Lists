package norn;

/**
 * Thrown to indicate a list name definition conflict.
 */
class ListDefinitionConflictException extends RuntimeException { //need extends?
    
    /**
     * Make a new list name definition conflict exception with the given detail message.
     * 
     * @param message the detail message
     */
    public ListDefinitionConflictException(String message) {
        super(message);
    }
}
