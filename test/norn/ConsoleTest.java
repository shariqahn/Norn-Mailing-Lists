package norn;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for Norn mailing list console user interface
 */
public class ConsoleTest {
    //Testing Strategy 
    //  input = empty, single email, union, intersection, difference, combo of set operators 
    //          0,1,2,>2 emails 
   // input valid/ invalid 
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    //failing if you just press enter or "" - is that okay?
    /**
     * Manual Test: inputing empty 
     * 1.run main 
     * 2.input empty string =>assert returns new line 
     * 
     */
    
    /**
     * Manual Test: single
     * 1.run main 
     * 2.input "something@place" =>assert returns  "something@place" 
     * 
     */
    
    /**
     * Manual Test: union 2 emails
     * 1.run main 
     * 2.input "something@place,place@something" =>assert returns"something@place,place@something"
     * 
     */
    /**
     * Manual Test: intersection union 3 emails 
     * 1.run main 
     * 2.input "bitdiddle@mit.edu*Bitdiddle@mit.edu,alyssap@mit.edu" =>assert returns"bitdiddle@mit.edu, alyssap@mit.edu"
     * 
     */
    /**
     * Manual Test: difference union 3 emails 
     * 1.run main 
     * 2.input "(bitdiddle@mit.edu,same@mit.edu)!(same@mit.edu,alyssap@mit.edu)" =>assert returns"bitdiddle@mit.edu"
     * 
     */
    /**
     * Manual Test: name and empty 
     * 1.run main 
     * 2.input "fellowship = frodo@shire, sam@shire, merry@shire, pippin@shire" =>assert returns"frodo@shire, sam@shire, merry@shire, pippin@shire"
     * 3. input "fellowship = " =>assert returns new line 
     * 4. input "fellowship" =>assert returns new line 
     * 
     */
    /**
     * Manual Test: sequence 
     * 1.run main 
     * 2.input "suite=room1,room2; room1=alice@mit.edu; room2=bob@mit.edu; suite" =>assert returns"alice@mit.edu,bob@mit.edu"
     * 
     */
    /**
     * Manual Test: invalid input invalid expression
     * 1.run main
     * 2.input " x = a@mit.edu | y = x,b@mit.edu"=>assert return "the given expression could not be evaluated"
     */
    /**
     * Manual Test: invalid input invalid filepath
     * 1.run main
     * 2.input " "/save ../""=>assert return "the given filename could not be written to"
     */  
    /**
     * Manual Test: invalid input invalid filepath
     * 1.run main
     * 2.input  ""/load ../""=>assert return "the given filename could not be read from"
     */  
    /**
     * Manual Test: save and load
     * 1.run main 
     * 2. input "hobbits=bilbo@shire, frodo@shire, sam@shire, merry@shire, pippin@shire"
     * 3. input "/save LOTR.txt"=> assert create files with "hobbits=bilbo@shire, frodo@shire, sam@shire, merry@shire, pippin@shire" in it
     * 
     * 
     */
    /**
     * Manual Test: load
     * 1.run main
     * 2. input "/load LOTR.txt"
     * 3. input hobbit=> assert output bilbo@shire, frodo@shire, sam@shire, merry@shire, pippin@shire
     * 
     */
    
}

