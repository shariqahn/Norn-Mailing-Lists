package norn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *  responses to String-based commands provided by the Norn mailing list system.
 */
class Commands {
    
    private static final String SAVE_COMMAND = "/save";
    private static final String LOAD_COMMAND = "/load";
    
    //  Thread safety argument: 
    //      saveFile() and loadFile require access to the storedLists lock before executing to prevent
    //          interleaving
    //      StoredListNames is thread safe
    //      all other inputs/outputs are immutable types which are also thread safe.
    //      emails is never mutated after it is passed into stringOfEmails()
    
    
    /**
     * Method to be used when saving the current list definitions in the mailing system
     * 
     * @param input console input that starts with the save command "/save"
     * @param storedLists a pointer to the current mailing systems stored lists
     * @throws IOException when the file cannot be written to
     */
    public static void saveFile(String input, StoredListNames storedLists) throws IOException {
        synchronized(storedLists) {
            String filename = input.substring(SAVE_COMMAND.length());
            filename = filename.strip(); 
            
            String singleExpression = storedLists.createSingleExpression();
            File file = new File(filename);
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(singleExpression);
            out.close();   
        }
    }
    
    /**
     * Method to be used when loading a text file containing a single list expression to be saved into the mailing system
     * 
     * @param input console input that starts with the save command "/load"
     * @param storedLists a pointer to the current mailing systems stored lists
     * @throws IOException when the file cannot be read or the file's expression cannot be parsed
     */
    public static void loadFile(String input, StoredListNames storedLists) throws IOException {
        synchronized(storedLists) {
            String filename = input.substring(LOAD_COMMAND.length());
            filename = filename.strip(); 
            
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file)); 
            String expression = reader.readLine();
            
            
            // parse contents and evaluate, which will save the results in stordLists
            ListExpression listExpression = ListExpression.parse(expression);
            listExpression.evaluate(storedLists, new HashSet<>());
            
            reader.close();
        }
    }
    
    /**
     * Method to be used to evaluate an input list expression and return the corresponding set of emails
     * 
     * @param input String input that is assumed to be an expression that can be parsed into a mailing list expression
     * @param storedLists a pointer to the current mailing systems stored lists
     * @throws IOException when the input cannot be parsed as a list expression
     * @return a String representation of the set of emails that are created by the parsed list expression
     */
    public static String evaluateExpression(String input, StoredListNames storedLists) throws IOException {
        final ListExpression expr = ListExpression.parse(input);
        final Set<ListExpression> emails = expr.evaluate(storedLists, new HashSet<>());
        
        String resultString = stringOfEmails(emails);
        
        return resultString;
    }
    
    /**
     * Method to be used to evaluate an input list expression and return the corresponding set of emails
     *      along with the structure of the the list expression that derived the results
     * 
     * @param input String input that is assumed to be an expression that can be parsed into a mailing list expression
     * @param storedLists a pointer to the current mailing systems stored lists
     * @throws IOException when the input cannot be parsed as a list expression
     * @return a String representation of the set of emails that are created by the parsed list expression 
     * along with structure of how they were derived
     */
    public static String evaluateExpressionWithStructure(String input, StoredListNames storedLists) throws IOException {
        final ListExpression expr = ListExpression.parse(input);
        final Set<ListExpression> finalEmails = expr.evaluate(storedLists, new HashSet<>());//need this to check for loops structure as string does not check
        
        String outString = "Your expression was parsed as: " + expr.toString() + "\n";
        outString += "The resulting email list is: " + stringOfEmails(finalEmails) + "\n \n";
        outString += "This is how you got this email list: \n";
        outString += expr.structureAsString(storedLists, 0);
        
        return outString; 
    }
    
    /**
     * Given a set of ListExpression objects return a String representation of the set of emails that those ListExpression
     *      objects represent. This method assumes that input is already only a set of Email objects
     * 
     * @param emails Set of ListExpression objects 
     * @return a String rep of the emails in the input
     */
    private static String stringOfEmails(Set<ListExpression> emails) {
        String resultString = "";
        for (ListExpression email : emails) {//race conditions here
            resultString += email.toString() + ",";
        }
        
        if(resultString.length() > 0) {
            resultString = resultString.substring(0, resultString.length() - 1); 
        }
        
        return resultString;
    }
    

}
