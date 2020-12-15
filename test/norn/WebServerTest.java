package norn;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

import org.junit.jupiter.api.Test;


/**
 * Tests for Norn mailing list web server
 */
public class WebServerTest {
    
    //Testing strategy 
    //     valid vs invalid inputs
    //     expression-email, listname,list definition, union, intersection, difference, sequence, parallel
    //          include subexpression /does not include subexpresSion
    //     storedListsNames is empty, non empty 
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    @Test
   public void testEvalValid() throws IOException {
        final WebServer server = new WebServer(new StoredListNames(),5005);
        server.start();

        final URL valid = new URL("http://localhost:" + server.port() + "/eval/wizards=gandalf@cosmos,saruman@cosmos");
        final InputStream input = valid.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String resultString = reader.readLine();
        assertEquals("Your expression was parsed as: (wizards = (gandalf@cosmos , saruman@cosmos))", resultString);
        assertEquals("The resulting email list is: gandalf@cosmos,saruman@cosmos", reader.readLine());
        assertEquals(" ", reader.readLine());
        assertEquals("This is how you got this email list: ", reader.readLine());
        assertEquals("[(wizards = (gandalf@cosmos , saruman@cosmos))] evaluates to --> {gandalf@cosmos,saruman@cosmos}", reader.readLine());
        assertEquals("            [gandalf@cosmos (and) saruman@cosmos] evaluates to --> {gandalf@cosmos,saruman@cosmos} ", reader.readLine());
        assertEquals("                            [gandalf@cosmos] evaluates to --> {gandalf@cosmos}", reader.readLine());
        reader.readLine();
        assertEquals(null, reader.readLine(), "end of stream");
        server.stop();
    }
    
    //covers listname no subexpression
    @Test
    public void testEvalSingleListname() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/a");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: a", resultString);
         assertEquals("The resulting email list is: ", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
        reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    //covers invalid input 
    @Test
    public void testEvalinValid() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL invalid = new URL("http://localhost:" + server.port() + "/eval/wizards=ganda/lf@cosmos,saruman@cosmos");
       
         
         final HttpURLConnection connection = (HttpURLConnection) invalid.openConnection();
         assertEquals(404, connection.getResponseCode(), "response code");
         server.stop();
     }
    
    //covers single email
    @Test
    public void testEvalSingleEmail() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/sample@mit.edu");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: sample@mit.edu", resultString);
         assertEquals("The resulting email list is: sample@mit.edu", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         
        reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    
    //covers empty input 
    @Test
    public void testEvalEmptyInput() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: ", resultString);
         assertEquals("The resulting email list is: ", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         
        reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
   
    
    //covers listname definition
    @Test
    public void testEvalListNameDefinition() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/a=sample@mit.edu");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: (a = sample@mit.edu)", resultString);
         assertEquals("The resulting email list is: sample@mit.edu", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         
        reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    //covers union 
    
    @Test
    public void testEvalUnion() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/myEmail@gmail.com,yours@mit");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: (myemail@gmail.com , yours@mit)", resultString);
         assertEquals("The resulting email list is: myemail@gmail.com,yours@mit", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         assertEquals("[myemail@gmail.com (and) yours@mit] evaluates to --> {myemail@gmail.com,yours@mit} ", reader.readLine());
        reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    //covers intersection
    @Test
    public void testEvalIntersection() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/myEmail@gmail.com*yours@mit");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: (myemail@gmail.com * yours@mit)", resultString);
         assertEquals("The resulting email list is: ", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         assertEquals("[myemail@gmail.com (intersection) yours@mit] evaluates to --> {} ", reader.readLine());
        reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    
    //covers difference
    @Test
    public void testEvalDifference() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/myEmail@gmail.com!yours@mit");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: (myemail@gmail.com ! yours@mit)", resultString);
         assertEquals("The resulting email list is: myemail@gmail.com", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         assertEquals("[myemail@gmail.com  (difference)  yours@mit] evaluates to --> {myemail@gmail.com}", reader.readLine());
        reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    
    //covers sequence 
    @Test
    public void testEvalSequence() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/myEmail@gmail.com;yours@mit");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: (myemail@gmail.com ; yours@mit)", resultString);
         assertEquals("The resulting email list is: yours@mit", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         assertEquals("[myemail@gmail.com] evaluates to --> {myemail@gmail.com}", reader.readLine());
        reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    
    //covers parallel
    @Test
    public void testEvalParallel() throws IOException {
         final WebServer server = new WebServer(new StoredListNames(),5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/myEmail@gmail.com%7Cyours@mit");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: (myemail@gmail.com | yours@mit)", resultString);
         assertEquals("The resulting email list is: ", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         assertEquals("[myemail@gmail.com (parallel) yours@mit] evaluates to --> {}", reader.readLine());
         assertEquals("            [myemail@gmail.com] evaluates to --> {myemail@gmail.com}", reader.readLine());
         assertEquals(" ------------------------ ", reader.readLine());
         assertEquals("            [yours@mit] evaluates to --> {yours@mit}", reader.readLine());
         
         reader.readLine();
         reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }

    //covers union includes subexpression  storelist names non empty
    @Test
    public void testEvalUnionSubSequence() throws IOException {
        StoredListNames tempListNames=new StoredListNames();
        ListExpression expression=ListExpression.parse("a=mit@edu.com");
        expression.evaluate(tempListNames, new HashSet<>());
        ListExpression expression1=ListExpression.parse("b=sam@edu.com");
        expression1.evaluate(tempListNames, new HashSet<>());
        
         final WebServer server = new WebServer(tempListNames,5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/d=(c=violetta@shariqah),a,b,c");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: (d = ((c = violetta@shariqah) , (a , (b , c))))", resultString);
         assertEquals("The resulting email list is: violetta@shariqah,mit@edu.com,sam@edu.com", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         assertEquals("[(d = ((c = violetta@shariqah) , (a , (b , c))))] evaluates to --> {violetta@shariqah,mit@edu.com,sam@edu.com}", reader.readLine());
         assertEquals("            [(c = violetta@shariqah) (and) (a , (b , c))] evaluates to --> {violetta@shariqah,mit@edu.com,sam@edu.com} ", reader.readLine());
         assertEquals("                            [(c = violetta@shariqah)] evaluates to --> {violetta@shariqah}", reader.readLine());
         assertEquals("                                            [violetta@shariqah] evaluates to --> {violetta@shariqah}", reader.readLine());
         assertEquals("                            [a (and) (b , c)] evaluates to --> {violetta@shariqah,mit@edu.com,sam@edu.com} ", reader.readLine());
         assertEquals("                                            [a] is equal to --> {mit@edu.com}", reader.readLine());
         assertEquals("                                            [b (and) c] evaluates to --> {violetta@shariqah,sam@edu.com} ", reader.readLine());
         assertEquals("                                                            [b] is equal to --> {sam@edu.com}", reader.readLine());
         assertEquals("                                                            [c] is equal to --> {violetta@shariqah}", reader.readLine());
         reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    //covers instersection includes subexpression storelist names non empty
  
    @Test
    public void testEvalIntersectionSubSequence() throws IOException {
        StoredListNames tempListNames=new StoredListNames();
        ListExpression expression=ListExpression.parse("a=mit@edu.com");
        expression.evaluate(tempListNames, new HashSet<>());
        ListExpression expression1=ListExpression.parse("b=sam@edu.com,mit@edu.com");
        expression1.evaluate(tempListNames, new HashSet<>());
        ListExpression expression2=ListExpression.parse("c=violetta@shariqah,mit@edu.com");
        expression2.evaluate(tempListNames, new HashSet<>());
         final WebServer server = new WebServer(tempListNames,5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/a*b*c");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: ((a * b) * c)", resultString);
         assertEquals("The resulting email list is: mit@edu.com", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         assertEquals("[(a * b) (intersection) c] evaluates to --> {mit@edu.com} ", reader.readLine());
         assertEquals("            [a (intersection) b] evaluates to --> {mit@edu.com} ", reader.readLine());
         assertEquals("                            [a] is equal to --> {mit@edu.com}", reader.readLine());
         assertEquals("                            [b] is equal to --> {sam@edu.com,mit@edu.com}", reader.readLine());
         assertEquals("            [c] is equal to --> {violetta@shariqah,mit@edu.com}", reader.readLine());
         
         reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    //covers difference includes subexpression storelist names non empty
    @Test
    public void testEvalDifferenceSubSequence() throws IOException {
        StoredListNames tempListNames=new StoredListNames();
        ListExpression expression=ListExpression.parse("a=mit@edu.com,sam@edu.com,violetta@shariqah");
        expression.evaluate(tempListNames, new HashSet<>());
        ListExpression expression1=ListExpression.parse("b=sam@edu.com,mit@edu.com");
        expression1.evaluate(tempListNames, new HashSet<>());
        ListExpression expression2=ListExpression.parse("c=violetta@shariqah");
        expression2.evaluate(tempListNames, new HashSet<>());
         final WebServer server = new WebServer(tempListNames,5005);
         server.start();

         final URL valid = new URL("http://localhost:" + server.port() + "/eval/a!b!c");
         final InputStream input = valid.openStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
         String resultString = reader.readLine();
         assertEquals("Your expression was parsed as: ((a ! b) ! c)", resultString);
         assertEquals("The resulting email list is: ", reader.readLine());
         assertEquals(" ", reader.readLine());
         assertEquals("This is how you got this email list: ", reader.readLine());
         assertEquals("[(a ! b)  (difference)  c] evaluates to --> {}", reader.readLine());
         assertEquals("            [a  (difference)  b] evaluates to --> {violetta@shariqah}", reader.readLine());
         assertEquals("                            [a] is equal to --> {violetta@shariqah,mit@edu.com,sam@edu.com}", reader.readLine());
         assertEquals("                            [b] is equal to --> {sam@edu.com,mit@edu.com}", reader.readLine());
         assertEquals("            [c] is equal to --> {violetta@shariqah}", reader.readLine());
         reader.readLine();
        reader.readLine();
         assertEquals(null, reader.readLine(), "end of stream");
         server.stop();
     }
    
    
    
}
