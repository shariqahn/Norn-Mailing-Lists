package norn;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.HashSet;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for commands provided by the Norn mailing list system
 */
@Tag("no-didit")
public class CommandTest {
    //Testing Strategy 
    //  saveFile(String input, StoredListNames storedLists)
    //      storedlists has listname that contains EmptyList,Email,ListName,Intersection,Difference,Union,Sequence,Parallel
    //      stored list has 0,1,>1 listnames
    //     input includes invalid path name
    //
    //  loadFile(String input, StoredListNames storedLists)
    //      storedlists has listname that  contains EmptyList,Email,ListName,Intersection,Difference,Union,Sequence,Parallel
   //       input includes path name that does not exist / is invalid 

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    /********************save and load******************/
    //covers invalid input save
    @Test
    public void testInvalidSave() {
        StoredListNames tempListNames = new StoredListNames();
        ListExpression tempExpression= ListExpression.parse("test=b@mit.edu");
        try {
            Commands.saveFile("/save ../",tempListNames);
            fail("expected to throw IOException");
        } catch (IOException e) {
            
            return;
        }}
    
     //covers invalid input load
        @Test 
        public void testInvalidLoad() {
            StoredListNames tempListNames = new StoredListNames();
            ListExpression tempExpression= ListExpression.parse("test=b@mit.edu");
            try {
                Commands.loadFile("/load ../",tempListNames);
                fail("expected to throw IOException");
            } catch (IOException e) {
                return;
            }
        
        
    }
      //covers file doesnt exist
        @Test 
        public void testNonexistentPathLoad() {
            StoredListNames tempListNames = new StoredListNames();
            ListExpression tempExpression= ListExpression.parse("test=b@mit.edu");
            try {
                Commands.loadFile("/load test/TestInvalidLoad",tempListNames);
                fail("expected to throw IOException");
            } catch (IOException e) {
                return;
            }
        
        
    }
    //covers 0 listnames
    @Test 
    public void testNone() throws IOException {
        StoredListNames tempListNames = new StoredListNames();
        Commands.saveFile("/save TestEmpty.txt",tempListNames);
        ListExpression tempExpression1 = ListExpression.parse("test = ");
        tempExpression1.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (test = ))", tempListNames.createSingleExpression()); //do we want these extra parenthesis?
        Commands.loadFile("/load TestEmpty.txt",tempListNames);
        assertEquals("( (test = ))", tempListNames.createSingleExpression());
    }
    
    //covers emptylist, email, intersection,listname
    //      1 listnames
    @Test 
    public void testIntersection() throws IOException {
        StoredListNames tempListNames =new StoredListNames();
        ListExpression tempExpression= ListExpression.parse("test = (m3@g.com , m5@g.com) * (m3@g.com , m1@g.com),");
        tempExpression.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (test = (((m3@g.com , m5@g.com) * (m3@g.com , m1@g.com)) , )))", tempListNames.createSingleExpression());
        Commands.saveFile("/save TestIntersection.txt",tempListNames);
        ListExpression tempExpression1= ListExpression.parse("test = ");
        tempExpression1.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (test = ))", tempListNames.createSingleExpression());
        Commands.loadFile("/load TestIntersection.txt",tempListNames);
        assertEquals("( (test = (((m3@g.com , m5@g.com) * (m3@g.com , m1@g.com)) , )))", tempListNames.createSingleExpression());
    }
    
    //covers email, union, sequence intersection listname
    // 1 listname
    @Test 
    public void testSequence() throws IOException {
        StoredListNames tempListNames =new StoredListNames();
        ListExpression tempExpression= ListExpression.parse("test=(x = a@mit.edu,b@mit.edu ; x * b@mit.edu)");
        tempExpression.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (test = ((x = (a@mit.edu , b@mit.edu)) ; (x * b@mit.edu))));( (x = (a@mit.edu , b@mit.edu)))", //might be wrong
                tempListNames.createSingleExpression());
        Commands.saveFile("/save TestSequence.txt",tempListNames);
        ListExpression tempExpression1= ListExpression.parse("test = a@mit.edu");
       tempExpression1.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (test = a@mit.edu));( (x = (a@mit.edu , b@mit.edu)))", tempListNames.createSingleExpression());
        Commands.loadFile("/load TestSequence.txt",tempListNames);
        assertEquals("( (test = ((x = (a@mit.edu , b@mit.edu)) ; (x * b@mit.edu))));( (x = (a@mit.edu , b@mit.edu)))", 
                tempListNames.createSingleExpression());
    }
    
    //covers email, parallel, listname
    // >1
    @Test 
    public void testParallel() throws IOException {
        StoredListNames tempListNames = new StoredListNames();
        ListExpression tempExpression = ListExpression.parse("(x = a@mit.edu | y = b@mit.edu) , x");
        tempExpression.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (x = a@mit.edu));( (y = b@mit.edu))", tempListNames.createSingleExpression());
        Commands.saveFile("/save TestParallel.txt",tempListNames);
        ListExpression tempExpression1= ListExpression.parse("x = something@mit.edu");
        tempExpression1.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (x = something@mit.edu));( (y = b@mit.edu))", tempListNames.createSingleExpression());
        Commands.loadFile("/load TestParallel.txt",tempListNames);
        assertEquals("( (x = a@mit.edu));( (y = b@mit.edu))", tempListNames.createSingleExpression());
    }

    // covers email, difference, union
    // covers more than 1
    @Test 
    public void testDifference() throws IOException {
        StoredListNames tempListNames =new StoredListNames();
        ListExpression tempExpression= ListExpression.parse("test = (m3@g.com , m5@g.com) ! (m3@g.com , m1@g.com, sam)");
        tempExpression.evaluate(tempListNames,new HashSet<>());
        ListExpression tempExpression1= ListExpression.parse("x = something@mit.edu");
        tempExpression1.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (test = ((m3@g.com , m5@g.com) ! (m3@g.com , (m1@g.com , sam)))));( (x = something@mit.edu));( (sam = ))", 
                tempListNames.createSingleExpression());
        Commands.saveFile("/save TestDifference1.txt",tempListNames);
        ListExpression tempExpression2= ListExpression.parse("x = somethingelse@mit.edu");
        tempExpression2.evaluate(tempListNames,new HashSet<>());
        assertEquals("( (test = ((m3@g.com , m5@g.com) ! (m3@g.com , (m1@g.com , sam)))));( (x = somethingelse@mit.edu));( (sam = ))", 
                tempListNames.createSingleExpression());
        Commands.loadFile("/load TestDifference1.txt",tempListNames);
        assertEquals("( (test = ((m3@g.com , m5@g.com) ! (m3@g.com , (m1@g.com , sam)))));( (x = something@mit.edu));( (sam = ))", 
                tempListNames.createSingleExpression());
    }
    
    
}
