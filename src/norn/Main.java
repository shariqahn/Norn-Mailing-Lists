/* Copyright (c) 2018 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package norn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

/**
 * Start the Norn mailing list system console interface and web server.
 */
public class Main {
    
    // Thread safety argument
    //    all list expressions are thread safe and immutable
    //    expressions are stored in a thread safe data type StoredListNames
    //      - only one mutator (and non-observer) being used, which is protected by thread safe data type
    //    web server is thread safe
    //    only one client can use the console at a time so only one thread
    
    
    private static final String SAVE_COMMAND = "/save";
    private static final String LOAD_COMMAND = "/load";
    
    /**
     * Read expression and command inputs from the console and output results,
     * and start a web server to handle requests from remote clients.
     * An empty console input terminates the program.
     * @param args unused
     * @throws IOException if there is an error reading the input
     */
    public static void main(String[] args) throws IOException {
        
        // "global" object that has all of the defined lists
        // must be made to handle threading
        StoredListNames storedLists = new StoredListNames();
        
        final WebServer web = new WebServer(storedLists, 8080); //magic number?
        web.start();
        
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        // i honestly don't know how to use this variable so I commented it out for now
        // Optional<ListExpression> currentExpression = Optional.empty();
        
        while (true) {
            System.out.print("> ");
            final String input = in.readLine();
            
            try {
                if (input.startsWith(SAVE_COMMAND) || input.startsWith(LOAD_COMMAND)) {
                    // SAVE
                    if(input.startsWith(SAVE_COMMAND)) {
                        try {
                            Commands.saveFile(input, storedLists);
                            String filename = input.substring(SAVE_COMMAND.length()).strip();
                            System.out.println("your file " + filename + " has been saved");
                        }
                        catch (IOException e) {
                            System.out.println("the given filename could not be written to");
                        }
                    }
                    // LOAD
                    else { 
                        try {
                            Commands.loadFile(input, storedLists);
                            String filename = input.substring(LOAD_COMMAND.length()).strip();
                            
                            System.out.println("You successfully loaded " + filename
                                    + " \n You now have the following email lists defined: \n" + 
                                    storedLists.allDefinitions());
                            
                        }
                        catch (IOException e) {
                            System.out.println("the given filename could not be read from");
                        } 
                    }
                }
                else {
                    try {
                        String resultString = Commands.evaluateExpression(input, storedLists);
                        System.out.println("The resulting mailing list is: " + resultString);
                    } catch (Exception e) {
                       System.out.println("the given expression could not be evaluated");
                    }
                    
                }
            } catch (NoSuchElementException nse) {
                // currentExpression was empty
                System.out.println("must enter an expression before using this command");
            } catch (RuntimeException re) {
                System.out.println(re.getClass().getName() + ": " + re.getMessage());
            }
        }
    }

}
