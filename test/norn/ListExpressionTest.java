package norn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.mit.eecs.parserlib.UnableToParseException;

/**
 * Tests for ListExpression abstract data type
 */
public class ListExpressionTest {

    // Objects currently implementing ListExpression:
    // email, email list, difference, intersection, union, parallel, sequence

    // parse
    // partitions: ListExpression is: with whitespace, without whitespace, 
    // a valid email, invalid email, union, intersection, difference, empty list name, 
    // invalid list name, valid list name defined by emails, valid list name defined by
    // subexpression, sequence, parallel, 
    // where for each of these that can be constructed of a sub expression, the sub
    // expressions are:
    // emails, empty list names, defined list names, other sub expression where precedence matters,
    // other sub expression where precedence doesn't matter

    // toString strategy
    // expression is: empty, email, or difference, intersection, union with: empty,
    // primitive, nested values

    // equals
    // this and that are:
    // the same expression,
    // different but equal expressions,
    // not equal expressions
    // this and that are: email, or difference, intersection, union with: empty,
    // primitive, nested values

    // hashCode
    // compare hashcodes of this and that such that
    // between two equivalent: email, email list, or difference, intersection,
    // union, parallel, sequence with:
    // empty, primitive, nested values

    // evaluate
    // partition: ListExpression is: an email, union, intersection, difference,
    // empty list name, list name with mail loop, list name without mail loop,listnamedefinition already defined  
    // list name defined by emails, list name defined by
    // subexpression,
    // sequence, parallel, parallel with conflicting list name definitions
    // where for each of these that can be constructed of a sub expression, the sub
    // expressions are:
    // emails, empty list names, defined list names, other sub expression
    
    // getAllListNames
    // partitions: ListExpression is: an email, union, intersection, difference,
    // empty list name, list name defined by emails, list name defined by
    // subexpression,
    // sequence, parallel, parallel with conflicting list name definitions,
    // where for each of these that can be constructed of a sub expression, the sub
    // expressions are:
    // emails, empty list names, defined list names, other sub expression

    // structureAsString
    // partition: ListExpression is: an email, union, intersection, difference,
    // empty list name, list name defined by emails, list name defined by
    // subexpression,
    // sequence, parallel
    // where for each of these that can be constructed of a sub expression, the sub
    // expressions are:
    // emails, empty list names, defined list names, other sub expression
    
    // stringOfExpressions
    // list expression contains 0,1,>1 objects
    
    // covers: parse invalid email
    @Test public void testInvalidEmails() {
        assertThrows(UnableToParseException.class, () -> {
            ListParser.parse("@");
        }, "expected conflict from overlapping interval");
        
        assertThrows(UnableToParseException.class, () -> {
            ListParser.parse("%@&.com");
        }, "expected conflict from overlapping interval");
        
        assertThrows(UnableToParseException.class, () -> {
            ListParser.parse("  (/@+)   ");
        }, "expected conflict from overlapping interval");
    }
    
    // covers: parse invalid list name
    @Test public void testInvalidListNames() {
        assertThrows(UnableToParseException.class, () -> {
            ListParser.parse("= a");
        }, "expected conflict from overlapping interval");
        
        assertThrows(UnableToParseException.class, () -> {
            ListParser.parse("+=");
        }, "expected conflict from overlapping interval");
        
        assertThrows(UnableToParseException.class, () -> {
            ListParser.parse(" /& ");
        }, "expected conflict from overlapping interval");
    }

    // covers: hashCode, parse for emails
    @Test public void testHashCodeEmails() throws UnableToParseException {
        ListExpression expression1 = ListParser.parse("email@gmail.com");
        ListExpression expression2 = ListParser.parse("  (email@gmail.com)   ");
        assertEquals(expression1.hashCode(), expression2.hashCode(), "expected equal hashcodes");

        ListExpression expression3 = ListParser.parse("email@gmail.com");
        assertEquals(expression1.hashCode(), expression3.hashCode(), "expected equal hashcodes");

        ListExpression expression4 = ListParser.parse("EMaIL@gmail.com");
        assertEquals(expression1.hashCode(), expression4.hashCode(), "expected equal hashcodes");
    }

    // covers: hashCode, parse for parallel, parse with whitespace
    @Test public void testHashCodeParallel() throws UnableToParseException {
        ListExpression expression1 = ListParser.parse("email@gmail.com | listName");
        ListExpression expression2 = ListParser.parse("  (email@gmail.com) | (LIStname)  ");
        assertEquals(expression1.hashCode(), expression2.hashCode(), "expected equal hashcodes");

        ListExpression expression3 = ListParser.parse("email@gmail.com | listName");
        assertEquals(expression1.hashCode(), expression3.hashCode(), "expected equal hashcodes");

        ListExpression expression7 = ListParser.parse("m1@g.com ! m2@g.com | listName");
        ListExpression expression8 = ListParser.parse("  (m1@g.com ! m2@g.com) | (LIStname)  ");
        assertEquals(expression7.hashCode(), expression8.hashCode(), "expected equal hashcodes");

        ListExpression expression4 = ListParser.parse("email@gmail.com | listName");
        ListExpression expression5 = ListParser.parse(" listName | email@gmail.com  ");
        assertEquals(expression4.hashCode(), expression5.hashCode(), "expected equal hashcodes");
    }

    // covers: hashCode, parse for sequence
    @Test public void testHashCodeSequence() throws UnableToParseException {
        ListExpression expression1 = ListParser.parse("email@gmail.com ; listName");
        ListExpression expression2 = ListParser.parse("  (email@gmail.com) ; (LIStname)  ");
        assertEquals(expression1.hashCode(), expression2.hashCode(), "expected equal hashcodes");

        ListExpression expression3 = ListParser.parse("email@gmail.com ; listName");
        assertEquals(expression1.hashCode(), expression3.hashCode(), "expected equal hashcodes");

        ListExpression expression4 = ListParser.parse("m1@g.com ! m2@g.com ; listName");
        ListExpression expression5 = ListParser.parse("  (m1@g.com ! m2@g.com) ; (LIStname)  ");
        assertEquals(expression4.hashCode(), expression5.hashCode(), "expected equal hashcodes");
    }

    // covers: hashCode, parse for list names
    @Test public void testHashCodeListName() throws UnableToParseException {
        ListExpression expression1 = ListParser.parse("ListName");
        ListExpression expression2 = ListParser.parse("  (ListName)   ");
        assertEquals(expression1.hashCode(), expression2.hashCode(), "expected equal hashcodes");

        ListExpression expression3 = ListParser.parse("ListName");
        assertEquals(expression1.hashCode(), expression3.hashCode(), "expected equal hashcodes");

        ListExpression expression4 = ListParser.parse("LISTNAME");
        assertEquals(expression1.hashCode(), expression4.hashCode(), "expected equal hashcodes");
    }

    // covers: hashCode, parse for Difference
    @Test public void testHashCodeDifference() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("m1@g.com ! m2@g.com");
        ListExpression expression2 = ListParser.parse("m1@g.com ! m2@g.com");
        assertEquals(expression1.hashCode(), expression2.hashCode(), "expected equal hashcodes");

        ListExpression expression3 = ListParser.parse("( m1@g.com    ! m2@g.com )");
        assertEquals(expression1.hashCode(), expression3.hashCode(), "expected equal hashcodes");

        ListExpression expression4 = ListParser.parse("m2@g.com ! m1@g.com");
        assertEquals(expression1.hashCode(), expression4.hashCode(), "expected equal hashcodes");

        ListExpression expression5 = ListParser.parse("m1@g.com , m2@g.com ! m3@g.com ! m4@g.com");
        ListExpression expression6 = ListParser.parse("m1@g.com , ((m2@g.com ! m3@g.com) ! m4@g.com)");
        assertEquals(expression5.hashCode(), expression6.hashCode(), "expected equal hashcodes");

    }

    // covers: hashCode, parse for Intersection, parse without whitespace
    @Test public void testHashCodeIntersection() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("m1@g.com*m2@g.com");
        ListExpression expression2 = ListParser.parse("m1@g.com * m2@g.com");
        assertEquals(expression1.hashCode(), expression2.hashCode(), "expected equivalent expressions");

        ListExpression expression3 = ListParser.parse("(m1@g.com  *   m2@g.com)");
        assertEquals(expression1.hashCode(), expression3.hashCode(), "expected equivalent expressions");

        ListExpression expression4 = ListParser.parse("m2@g.com * m1@g.com");
        assertEquals(expression1.hashCode(), expression4.hashCode(), "expected equivalent expressions");

        ListExpression expression5 = ListParser.parse("(m3@g.com * m5@g.com) * (m2@g.com * m1@g.com)");
        ListExpression expression6 = ListParser.parse("(m2@g.com * m1@g.com) * (m3@g.com * m5@g.com)");
        assertEquals(expression5.hashCode(), expression6.hashCode(), "expected equivalent expressions");

    }

    // covers: hashCode, parse for Union
    @Test public void testHashCodeUnion() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("m1@g.com , m2@g.com");
        ListExpression expression2 = ListParser.parse("m1@g.com , m2@g.com");
        assertEquals(expression1.hashCode(), expression2.hashCode(), "expected equivalent expressions");

        ListExpression expression3 = ListParser.parse("(m1@g.com  ,   m2@g.com)");
        assertEquals(expression1.hashCode(), expression3.hashCode(), "expected equivalent expressions");

        ListExpression expression4 = ListParser.parse("m2@g.com , m1@g.com");
        assertEquals(expression1.hashCode(), expression4.hashCode(), "expected equivalent expressions");

        ListExpression expression5 = ListParser.parse("(m3@g.com * m1@g.com) , m2@g.com");
        ListExpression expression6 = ListParser.parse("m3@g.com * m1@g.com , m2@g.com");
        assertEquals(expression5.hashCode(), expression6.hashCode(), "expected equivalent expressions");

    }

    // covers: equals of emails, same expression, diff but equal, and not equal
    // expressions
    @Test public void testEqualsEmail() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("email@gmail.com");
        ListExpression expression2 = ListParser.parse("  (email@gmail.com)   ");
        assertEquals(expression1, expression2, "expected equivalent expressions");

        ListExpression expression3 = ListParser.parse("email@gmail.com");
        assertEquals(expression1, expression3, "expected equivalent expressions");

        ListExpression expression4 = ListParser.parse("EMaIL@gmail.com");
        assertEquals(expression1, expression4, "username should be case insensitive");

        ListExpression expression5 = ListParser.parse("email@GMail.com");
        assertEquals(expression1, expression5, "domain should be case insensitive");

    }

    // covers: equals of Difference with the same expression, diff but equal, and
    // not equal expressions
    @Test public void testEqualsDifference() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("m1@g.com ! m2@g.com");
        ListExpression expression2 = ListParser.parse("m1@g.com ! m2@g.com");
        assertEquals(expression1, expression2, "expected equivalent expressions");

        ListExpression expression3 = ListParser.parse("( m1@g.com    ! m2@g.com )");
        assertEquals(expression1, expression3, "expected equivalent expressions");

        ListExpression expression5 = ListParser.parse("m1@g.com , m2@g.com ! m3@g.com ! m4@g.com");
        ListExpression expression6 = ListParser.parse("m1@g.com , ((m2@g.com ! m3@g.com) ! m4@g.com)");
        assertEquals(expression5, expression6, "expected equivalent expressions");

    }

    // covers: equals of Union with the same expression, diff but equal, and not
    // equal expressions
    @Test public void testEqualsUnion() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("m1@g.com , m2@g.com");
        ListExpression expression2 = ListParser.parse("m1@g.com , m2@g.com");
        assertEquals(expression1, expression2, "expected equivalent expressions");

        ListExpression expression3 = ListParser.parse("(m1@g.com  ,   m2@g.com)");
        assertEquals(expression1, expression3, "expected equivalent expressions");

        ListExpression expression4 = ListParser.parse("m2@g.com , m1@g.com");
        assertEquals(expression1, expression4, "expected equivalent expressions");

        ListExpression expression5 = ListParser.parse("(set3@gmail.com * m1@g.com) , m2@g.com");
        ListExpression expression6 = ListParser.parse("set3@gmail.com * m1@g.com , m2@g.com");
        assertEquals(expression5, expression6, "expected equivalent expressions");

    }

    // covers: equals of Intersection with the same expression, diff but equal, and
    // not equal expressions
    @Test public void testEqualsIntersection() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("m1@g.com * m2@g.com");
        ListExpression expression2 = ListParser.parse("m1@g.com * m2@g.com");
        assertEquals(expression1, expression2, "expected equivalent expressions");

        ListExpression expression3 = ListParser.parse("(m1@g.com  *   m2@g.com)");
        assertEquals(expression1, expression3, "expected equivalent expressions");

        ListExpression expression4 = ListParser.parse("m2@g.com * m1@g.com");
        assertEquals(expression1, expression4, "expected equivalent expressions");

        ListExpression expression5 = ListParser.parse("(m3@g.com * m5@g.com) * (m2@g.com * m1@g.com)");
        ListExpression expression6 = ListParser.parse("(m2@g.com * m1@g.com) * (m3@g.com * m5@g.com)");
        assertEquals(expression5, expression6, "expected equivalent expressions");

    }
    
    // covers: equals of sequence with the same expression, diff but equal, and
    // not equal expressions
    @Test public void testEqualsSequence() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("m1@g.com ; m2@g.com");
        ListExpression expression2 = ListParser.parse("m1@g.com ; m2@g.com");
        assertEquals(expression1, expression2, "expected equivalent expressions");

        ListExpression expression3 = ListParser.parse("( m1@g.com    ; m2@g.com )");
        assertEquals(expression1, expression3, "expected equivalent expressions");

        ListExpression expression5 = ListParser.parse("(m1@g.com , m2@g.com ); m3@g.com ; m4@g.com");
        ListExpression expression6 = ListParser.parse("m1@g.com , m2@g.com ; m3@g.com ; m4@g.com");
        assertEquals(expression5, expression6, "expected equivalent expressions");

    }
    
 // covers: equals of parallel with the same expression, diff but equal, and not
    // equal expressions
    @Test public void testEqualsParallel() throws UnableToParseException {

        ListExpression expression1 = ListParser.parse("m1@g.com | m2@g.com");
        ListExpression expression2 = ListParser.parse("m1@g.com | m2@g.com");
        assertEquals(expression1, expression2, "expected equivalent expressions");

        ListExpression expression3 = ListParser.parse("(m1@g.com  |   m2@g.com)");
        assertEquals(expression1, expression3, "expected equivalent expressions");

        ListExpression expression4 = ListParser.parse("m2@g.com | m1@g.com");
        assertEquals(expression1, expression4, "expected equivalent expressions");

        ListExpression expression5 = ListParser.parse("(set3@gmail.com * m1@g.com) | m2@g.com");
        ListExpression expression6 = ListParser.parse("set3@gmail.com * m1@g.com | m2@g.com");
        assertEquals(expression5, expression6, "expected equivalent expressions");

    }

    // covers: toString of empty input
    @Test public void testToStringEmpty() throws UnableToParseException {
        ListExpression expression;

        expression = ListParser.parse("");
        assertEquals("", expression.toString(), "expected correct toString value");

        expression = ListParser.parse(" ");
        assertEquals("", expression.toString(), "expected correct toString value");

    }

    // covers: toString of email
    @Test public void testToStringEmail() throws UnableToParseException {
        ListExpression expression;

        expression = ListParser.parse("sam@gmail.com");
        assertEquals("sam@gmail.com", expression.toString(), "expected correct toString value");

    }

    // covers: toString of union of emails
    @Test public void testToStringUnionEmails() throws UnableToParseException {
        ListExpression expression;

        expression = ListParser.parse("sam@gmail.com , vio@mit.edu");
        assertEquals("(sam@gmail.com , vio@mit.edu)", expression.toString(), "expected correct toString value");

        expression = ListParser.parse(" sam@gmail.com   ,  vio@mit.edu");
        assertEquals("(sam@gmail.com , vio@mit.edu)", expression.toString(), "expected correct toString value");

        expression = ListParser.parse("sam@gmail.com,vio@mit.edu");
        assertEquals("(sam@gmail.com , vio@mit.edu)", expression.toString(), "expected correct toString value");
    }

    // covers: toString of difference of emails
    @Test public void testToStringDifferenceEmails() throws UnableToParseException {
        ListExpression expression;

        expression = ListParser.parse("sam@gmail.com ! vio@mit.edu");
        assertEquals("(sam@gmail.com ! vio@mit.edu)", expression.toString(), "expected correct toString value");

        expression = ListParser.parse("sam@gmail.com  !   vio@mit.edu");
        assertEquals("(sam@gmail.com ! vio@mit.edu)", expression.toString(), "expected correct toString value");

        expression = ListParser.parse("sam@gmail.com!vio@mit.edu");
        assertEquals("(sam@gmail.com ! vio@mit.edu)", expression.toString(), "expected correct toString value");

    }

    // covers: toString of intersection of emails
    @Test public void testToStringIntersectionEmails() throws UnableToParseException {
        ListExpression expression;

        expression = ListParser.parse("sam@gmail.com * vio@mit.edu");
        assertEquals("(sam@gmail.com * vio@mit.edu)", expression.toString(), "expected correct toString value");

        expression = ListParser.parse(" sam@gmail.com   *  vio@mit.edu");
        assertEquals("(sam@gmail.com * vio@mit.edu)", expression.toString(), "expected correct toString value");

        expression = ListParser.parse("sam@gmail.com*vio@mit.edu");
        assertEquals("(sam@gmail.com * vio@mit.edu)", expression.toString(), "expected correct toString value");

    }

    // covers: toString, parse of difference with nested inputs
    @Test public void testToStringUnionNested() throws UnableToParseException {
        ListExpression expression;

        expression = ListParser.parse("(course6@gmail.com * course9@gmail.com) , (my@email.com ! course10@gmail.com)");
        assertEquals("((course6@gmail.com * course9@gmail.com) , (my@email.com ! course10@gmail.com))",
                expression.toString(), "expected correct parsing");
    }

    // covers: toString, parse of intersection with nested inputs
    @Test public void testToStringIntersectionNested() throws UnableToParseException {
        ListExpression expression1;
        ListExpression expression2;

        expression1 = ListParser.parse("course6@gmail.com , course9@gmail.com * my@email.com ! course10@gmail.com");
        expression2 = ListParser.parse("course6@gmail.com, ((course9@gmail.com * my@email.com) ! course10@gmail.com)");
        assertEquals(expression1.toString(), expression2.toString(), "expected correct parsing");

        expression1 = ListParser
                .parse("(course6@gmail.com , course9@gmail.com)  *    (   my@email.com  ! course10@gmail.com)");
        expression2 = ListParser.parse("(course6@gmail.com , course9@gmail.com) * (my@email.com ! course10@gmail.com)");
        assertEquals(expression1.toString(), expression2.toString(), "expected correct parsing");

    }

    // covers: toString, parse of difference with nested inputs
    @Test public void testToStringDifferenceNested() throws UnableToParseException {
        ListExpression expression = ListParser
                .parse("(course6@gmail.com * course9@gmail.com) ! (my@email.com * course10@gmail.com)");
        assertEquals("((course6@gmail.com * course9@gmail.com) ! (my@email.com * course10@gmail.com))",
                expression.toString(), "expected correct parsing");

        ListExpression expression1 = ListParser
                .parse("course6@gmail.com * (course9@gmail.com ! my@email.com , course10@gmail.com)");
        assertEquals("(course6@gmail.com * ((course9@gmail.com ! my@email.com) , course10@gmail.com))",
                expression1.toString(), "expected correct parsing");

        ListExpression expression2 = ListParser
                .parse("course6@gmail.com * (course9@gmail.com ! (my@email.com , course10@gmail.com))");
        assertEquals("(course6@gmail.com * (course9@gmail.com ! (my@email.com , course10@gmail.com)))",
                expression2.toString(), "expected correct parsing");

    }
    
 // covers: toString, parse of sequence with nested inputs
    @Test public void testToStringSequenceNested() throws UnableToParseException {
        ListExpression expression = ListParser
                .parse("(course6@gmail.com * course9@gmail.com) ; (my@email.com * course10@gmail.com)");
        assertEquals("((course6@gmail.com * course9@gmail.com) ; (my@email.com * course10@gmail.com))",
                expression.toString(), "expected correct parsing");

        ListExpression expression1 = ListParser
                .parse("course6@gmail.com * (course9@gmail.com ; my@email.com , course10@gmail.com)");
        assertEquals("(course6@gmail.com * (course9@gmail.com ; (my@email.com , course10@gmail.com)))",
                expression1.toString(), "expected correct parsing");

        ListExpression expression2 = ListParser
                .parse("course6@gmail.com * (course9@gmail.com ; (my@email.com , course10@gmail.com))");
        assertEquals("(course6@gmail.com * (course9@gmail.com ; (my@email.com , course10@gmail.com)))",
                expression2.toString(), "expected correct parsing");

    }

    // covers: toString, parse of parallel with nested inputs
    @Test public void testToStringParallelNested() throws UnableToParseException {
        ListExpression expression = ListParser
                .parse("(course6@gmail.com * course9@gmail.com) | (my@email.com * course10@gmail.com)");
        assertEquals("((course6@gmail.com * course9@gmail.com) | (my@email.com * course10@gmail.com))",
                expression.toString(), "expected correct parsing");

        ListExpression expression1 = ListParser
                .parse("course6@gmail.com * (course9@gmail.com | my@email.com , course10@gmail.com)");
        assertEquals("(course6@gmail.com * (course9@gmail.com | (my@email.com , course10@gmail.com)))",
                expression1.toString(), "expected correct parsing");

        ListExpression expression2 = ListParser
                .parse("course6@gmail.com * (course9@gmail.com | (my@email.com , course10@gmail.com))");
        assertEquals("(course6@gmail.com * (course9@gmail.com | (my@email.com , course10@gmail.com)))",
                expression2.toString(), "expected correct parsing");

    }
    //covers: listname and list definition
    @Test
    public void testToStringList() throws UnableToParseException {
        ListExpression expression = ListParser.parse("A");
        assertEquals(new ListName("a"), expression);
        assertEquals("a", expression.toString());
        ListExpression expression1 = ListParser.parse("a=");
        assertEquals(new ListNameDefinition("a",new EmptyList()), expression1);
        assertEquals("(a = )", expression1.toString());
        
        
    }

    // covers: precedence check
    @Test public void testPrecedenceToString() throws UnableToParseException {
        ListExpression expression = ListParser.parse("a=b=x");
        assertEquals("(a = (b = x))", expression.toString());
        ListExpression expression2 = ListParser.parse("a,b=x");
        assertEquals("(a , (b = x))", expression2.toString());
        ListExpression expression3 = ListParser.parse("x=a,b!c*d");
        assertEquals("(x = (a , (b ! (c * d))))", expression3.toString());
        ListExpression expression4 = ListParser.parse("a|b;c=d,e!g*h");
        assertEquals("(a | (b ; (c = (d , (e ! (g * h))))))", expression4.toString());

    }

    // EVALUATE TESTS

    // covers: evaluate on email, parse valid email
    @Test public void testEvaluateEmail() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("myEmail@gmail.com");
        assertEquals(Set.of(new Email("myemail", "gmail.com")), expression.evaluate(storedLists, new HashSet<>()),
                "expected to see one email in the set");
        assertEquals("", storedLists.allDefinitions(), "expected no definitions in global class");
    }

    // covers: evaluate on empty list name
    @Test public void testEvaluateEmptyListName() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a = ");
        assertEquals(Set.of(), expression.evaluate(storedLists, new HashSet<>()),
                "expected to see one email in the set");// do
        // empty?
        assertEquals("a =", storedLists.allDefinitions(), "expected correct stored definitions");
    }

    // covers: evaluate list name defined by an email, list name without mail loop,
    // parse valid list name defined by an email
    @Test public void testEvaluateListNameOfEmail() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a = m@gmail");
        Set<ListExpression> result = Set.of(new Email("m", "gmail"));

        assertEquals(result, expression.evaluate(storedLists, new HashSet<>()), "expected to see one email in the set");
        assertEquals(result, storedLists.getEmailList("a"), "expected correct storage");
        ListExpression expression2=ListParser.parse("a = t@mit");
        Set<ListExpression> result2 = Set.of(new Email("t", "mit"));
        assertEquals(result2, expression2.evaluate(storedLists, new HashSet<>()));
        ListExpression expression3=ListParser.parse("a");
        assertEquals(result2, expression3.evaluate(storedLists, new HashSet<>()));
    }

    // covers: evaluate list name defined by subexpression (including other list
    // names), parse valid list name defined by subexpression, parse union precedence over list name definition,
    // parse difference precedence over list name definition, parse difference precedence over union
    @Test public void testEvaluateListNameSubExpr() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a = myEmail@gmail.com, yours@mit");
        Set<ListExpression> result1 = Set.of(new Email("myemail", "gmail.com"), new Email("yours", "mit"));
        assertEquals(result1, expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("b = a ! yours@mit, yours@mit");
        assertEquals(Set.of(new Email("myemail", "gmail.com"), new Email("yours", "mit")),
                expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");

        Set<ListExpression> result2 = Set.of(new Email("myemail", "gmail.com"), new Email("yes", "u"));
        ListExpression expression3 = ListParser.parse("c = a ! yours@mit, yes@u");
        assertEquals(result2, expression3.evaluate(storedLists, new HashSet<>()), "expected correct set");

        assertEquals(result1, storedLists.getEmailList("a"), "expected correct storage");
        assertEquals(result1, storedLists.getEmailList("b"), "expected correct storage");
        assertEquals(result2, storedLists.getEmailList("c"), "expected correct storage");
    }

    // covers: parse, evaluate union of emails and emails with empty
    @Test public void testEvaluateUnionOfEmails() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("myEmail@gmail.com, yours@mit");
        // is it okay that emails become lowercase?
        assertEquals(Set.of(new Email("myemail", "gmail.com"), new Email("yours", "mit")),
                expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("myEmail@gmail.com,");
        assertEquals(Set.of(new Email("myemail", "gmail.com")), expression2.evaluate(storedLists, new HashSet<>()),
                "expected correct set");
    }

    // covers: parse, evaluate union of subexpressions and defined lists
    @Test public void testEvaluateUnionSubexpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = myEmail@gmail.com, yours@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = yes@mit, no@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a, c, b");
        // do we want that extra comma to be there for c?
        assertEquals(
                Set.of(new Email("myemail", "gmail.com"), new Email("yours", "mit"), new Email("yes", "mit"),
                        new Email("no", "mit")),
                expression.evaluate(storedLists, new HashSet<>()), "expected correct set");
    }

    // covers: parse, evaluate intersection of emails and email with empty
    @Test public void testEvaluateIntersectionWithEmails() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a@mit * b@mit");
        assertEquals(Set.of(), expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("a@mit * ");
        assertEquals(Set.of(), expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression3 = ListParser.parse("a@mit * a@mit");
        assertEquals(Set.of(new Email("a", "mit")), expression3.evaluate(storedLists, new HashSet<>()),
                "expected correct set");
    }

    // covers: parse, evaluate intersection of subexpressions and defined lists
    @Test public void testEvaluateIntersectionWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = x@mit, y@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a * x@mit");
        Set<ListExpression> result = Set.of(new Email("x", "mit"));
        assertEquals(result, expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("a * b");
        assertEquals(result, expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression3 = ListParser.parse("(x@mit, y@mit) * (x@mit, z@mit)");
        assertEquals(result, expression3.evaluate(storedLists, new HashSet<>()), "expected correct set");
    }

    // covers: parse, evaluate difference of emails and email with empty
    @Test public void testEvaluateDifferenceWithEmails() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a@mit ! b@mit");
        assertEquals(Set.of(new Email("a", "mit")), expression.evaluate(storedLists, new HashSet<>()),
                "expected correct set");

        ListExpression expression2 = ListParser.parse("a@mit ! ");
        assertEquals(Set.of(new Email("a", "mit")), expression2.evaluate(storedLists, new HashSet<>()),
                "expected correct set");

        ListExpression expression3 = ListParser.parse("a@mit ! a@mit");
        assertEquals(Set.of(), expression3.evaluate(storedLists, new HashSet<>()), "expected correct set");
    }

    // covers: parse, evaluate difference of subexpressions and defined lists
    @Test public void testEvaluateDifferenceWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = x@mit, y@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a ! x@mit");
        assertEquals(Set.of(new Email("y", "mit")), expression.evaluate(storedLists, new HashSet<>()),
                "expected correct set");

        ListExpression expression2 = ListParser.parse("a ! b");
        assertEquals(Set.of(new Email("y", "mit")), expression2.evaluate(storedLists, new HashSet<>()),
                "expected correct set");

        ListExpression expression3 = ListParser.parse("(x@mit, y@mit) ! (x@mit, z@mit)");
        assertEquals(Set.of(new Email("y", "mit")), expression3.evaluate(storedLists, new HashSet<>()),
                "expected correct set");
    }

    // covers: parse, evaluate sequence of emails and email with empty
    @Test public void testEvaluateSequenceWithEmails() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a@mit ; b@mit");
        assertEquals(Set.of(new Email("b", "mit")), expression.evaluate(storedLists, new HashSet<>()),
                "expected correct set");

        ListExpression expression2 = ListParser.parse("a@mit ; ");
        assertEquals(Set.of(), expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression3 = ListParser.parse("a@mit ; a@mit");
        assertEquals(Set.of(new Email("a", "mit")), expression3.evaluate(storedLists, new HashSet<>()),
                "expected correct set");
    }

    // covers: parse, evaluate sequence of subexpressions and defined lists
    // parse list name definition precedence over sequence
    @Test public void testEvaluateSequenceWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = x@mit, y@mit; b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a");
        assertEquals(Set.of(new Email("y", "mit"), new Email("x", "mit")),
                expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("b");
        assertEquals(Set.of(new Email("x", "mit"), new Email("z", "mit")),
                expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression3 = ListParser.parse("(x@mit, y@mit) ; (x@mit, z@mit)");
        assertEquals(Set.of(new Email("x", "mit"), new Email("z", "mit")),
                expression3.evaluate(storedLists, new HashSet<>()), "expected correct set");
    }

    // covers: parse, evaluate sequence ordering, parse precedence left to right
    @Test public void testEvaluateSequenceOrdering() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a = b,c ; c = m@mit; b= n@mit; a");

        assertEquals(Set.of(new Email("m", "mit"), new Email("n", "mit")),
                expression.evaluate(storedLists, new HashSet<>()), "expected correct set");
        ListExpression expression2 = ListParser.parse(" c = m@mit;a = b,c ; b= n@mit; a");

        assertEquals(Set.of(new Email("m", "mit"), new Email("n", "mit")),
                expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");
        ListExpression expression3 = ListParser.parse(" c = m@mit; b= n@mit;a = b,c ; a");

        assertEquals(Set.of(new Email("m", "mit"), new Email("n", "mit")),
                expression3.evaluate(storedLists, new HashSet<>()), "expected correct set");
        
    }
    // covers: sequence with empty object 
    @Test 
    public void testSequenceWithEmpty() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("m@mit;");

        assertEquals(Set.of(),
                expression.evaluate(storedLists, new HashSet<>()), "expected correct set");
        
    }
    // covers: empty objects creations

    @Test public void testEvaluateEmptyObjects() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a = b,c");
        assertEquals(Set.of(), expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        assertEquals(Set.of(), storedLists.getEmailList("b"), "Expected empty object");
        assertEquals(Set.of(), storedLists.getEmailList("c"), "Expected empty object");
        assertEquals(ListParser.parse("b,c").evaluate(storedLists, new HashSet<>()), storedLists.getEmailList("a"),
                "expected correct storage");
    }

    // covers: parse, evaluate parallel, parse parenthesis precedence over everything, parse list name defintion
    // precedence over parallel
    @Test public void testEvaluateParallel() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("(x = a@mit.edu | y = b@mit.edu) , x");
        assertEquals(Set.of(new Email("a", "mit.edu")), expression.evaluate(storedLists, new HashSet<>()),
                "expected correct set");
    }

    // covers evaluate checking for mail loop sequence
    @Test public void testSimpleSequenceMailLoop() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a=b;b=a");
        try {
            expression.evaluate(storedLists, new HashSet<>());
            fail("expected to throw NoSuchElementException ");
        } catch (ListDefinitionConflictException e) {
            return;
        }

    }

    // covers evaluate checking for mail loop sequence
    @Test public void testComplicatedSequenceMailLoop() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a=b;b=d;d=e,a");
        try {
            expression.evaluate(storedLists, new HashSet<>());
            fail("expected to throw NoSuchElementException ");
        } catch (ListDefinitionConflictException e) {
            return;
        }

    }

    // covers evaluate checking for mail loop union
    @Test public void testSimpleUnionMailLoop() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a=b,b=a");
        try {
            expression.evaluate(storedLists, new HashSet<>());
            fail("expected to throw NoSuchElementException ");
        } catch (ListDefinitionConflictException e) {
            return;
        }

    }

    // covers: parse, evaluate parallel of emails and email with empty
    @Test public void testEvaluateParallelWithEmails() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a@mit | b@mit");
        assertEquals(Set.of(), expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("a@mit | ");
        assertEquals(Set.of(), expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression3 = ListParser.parse("a@mit | a@mit");
        assertEquals(Set.of(), expression3.evaluate(storedLists, new HashSet<>()), "expected correct set");
    }

    // covers: parse, evaluate parallel of subexpressions and defined lists
    @Test public void testEvaluateParallelWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = x@mit, y@mit | b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a");
        assertEquals(Set.of(new Email("y", "mit"), new Email("x", "mit")),
                expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("b");
        assertEquals(Set.of(new Email("x", "mit"), new Email("z", "mit")),
                expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression3 = ListParser.parse("(x@mit, y@mit) | (x@mit, z@mit)");
        assertEquals(Set.of(), expression3.evaluate(storedLists, new HashSet<>()), "expected correct set");
    }

    // covers: parse, evaluate parallel ordering
    @Test public void testEvaluateParallelOrdering() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("(a = l@mit | c = m@mit| b= n@mit); (b,c)");
        assertEquals(Set.of(new Email("m", "mit"), new Email("n", "mit")),
                expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("a");
        assertEquals(Set.of(new Email("l", "mit")), expression2.evaluate(storedLists, new HashSet<>()),
                "expected correct set");

        ListExpression expression3 = ListParser.parse("b");
        assertEquals(Set.of(new Email("n", "mit")), expression3.evaluate(storedLists, new HashSet<>()),
                "expected correct set");

        ListExpression expression4 = ListParser.parse("c");
        assertEquals(Set.of(new Email("m", "mit")), expression4.evaluate(storedLists, new HashSet<>()),
                "expected correct set");
    }

    //covers: parse, evaluate parallel with union
    @Test public void testEvaluateParallelUnion() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("(l@mit | m@mit| n@mit)");
        assertEquals(Set.of(), expression.evaluate(storedLists, new HashSet<>()), "expected correct set");

        ListExpression expression2 = ListParser.parse("(l@mit , m@mit| n@mit)");
        assertEquals(Set.of(), expression2.evaluate(storedLists, new HashSet<>()), "expected correct set");
    }

    // covers: parallel with conflicting list name definitions
    @Test public void testEvaluateParallelWithConflict() throws UnableToParseException { // needs more partitions?
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("x = a@mit.edu | y = x,b@mit.edu");
        assertThrows(ListDefinitionConflictException.class, () -> {
            expression.evaluate(storedLists, new HashSet<>());
        }, "expected conflict from overlapping list definitions in right and left side of parallel");
    }
    
    //covers: structureAsString on email
    @Test public void testStructureAsStringEmail() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("myEmail@gmail.com");
        assertEquals("[myemail@gmail.com] evaluates to --> {myemail@gmail.com}",
                expression.structureAsString(storedLists, 0), "expected to see correct ");

    }

    // covers: structureAsString on empty list name
    @Test public void testStructureAsStringEmptyListName() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a = ");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[(a = )] evaluates to --> {}\n    ", expression.structureAsString(storedLists, 0),
                "expected to see correct structure for empty string");

        ListExpression expression2 = ListParser.parse("a");
        expression2.evaluate(storedLists, new HashSet<>());
        assertEquals("[a] is equal to --> {}", expression2.structureAsString(storedLists, 0),
                "expected to see correct structure for empty string\"");
    }

    // covers: structureAsString list name defined by an email
    @Test public void testStructureAsStringListNameOfEmail() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a = m@gmail");
        expression.evaluate(storedLists, new HashSet<>());

        assertEquals(
                "[(a = m@gmail)] evaluates to --> {m@gmail}\n" + "            [m@gmail] evaluates to --> {m@gmail}",
                expression.structureAsString(storedLists, 0),
                "expected to see correct structure for listname of email");
    }

    // covers: StructureAsString list name defined by subexpression (including other
    // list
    // names)
    @Test public void testStructureAsStringListNameSubExpr() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a = myEmail@gmail.com, yours@mit");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[(a = (myemail@gmail.com , yours@mit))] evaluates to --> {myemail@gmail.com,yours@mit}\n"
                + "            [myemail@gmail.com (and) yours@mit] evaluates to --> {myemail@gmail.com,yours@mit} \n"
                + "                            [myemail@gmail.com] evaluates to --> {myemail@gmail.com}\n"
                + "                            [yours@mit] evaluates to --> {yours@mit}",
                expression.structureAsString(storedLists, 0),
                "expected to see correct structure for list name defined by subexpression ");

        ListExpression expression2 = ListParser.parse("b = a ! yours@mit, yours@mit");
        expression2.evaluate(storedLists, new HashSet<>());

        assertEquals("[(b = ((a ! yours@mit) , yours@mit))] evaluates to --> {myemail@gmail.com,yours@mit}\n"
                + "            [(a ! yours@mit) (and) yours@mit] evaluates to --> {myemail@gmail.com,yours@mit} \n"
                + "                            [a  (difference)  yours@mit] evaluates to --> {myemail@gmail.com}\n"
                + "                                            [a] is equal to --> {myemail@gmail.com,yours@mit}\n"
                + "                                            [yours@mit] evaluates to --> {yours@mit}\n"
                + "                            [yours@mit] evaluates to --> {yours@mit}",
                expression2.structureAsString(storedLists, 0),
                "expected to see correct structure for list name defined by subexpression ");
        ListExpression expression3 = ListParser.parse("c = a ! yours@mit, yes@u");
        expression3.evaluate(storedLists, new HashSet<>());
        assertEquals("[(c = ((a ! yours@mit) , yes@u))] evaluates to --> {myemail@gmail.com,yes@u}\n"
                + "            [(a ! yours@mit) (and) yes@u] evaluates to --> {myemail@gmail.com,yes@u} \n"
                + "                            [a  (difference)  yours@mit] evaluates to --> {myemail@gmail.com}\n"
                + "                                            [a] is equal to --> {myemail@gmail.com,yours@mit}\n"
                + "                                            [yours@mit] evaluates to --> {yours@mit}\n"
                + "                            [yes@u] evaluates to --> {yes@u}",
                expression3.structureAsString(storedLists, 0),
                "expected to see correct structure for list name defined by subexpression ");

    }

    // covers: StructureAsString union of emails and emails with empty
    @Test public void testStructureAsStringUnionOfEmails() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("myEmail@gmail.com, yours@mit");

        expression.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[myemail@gmail.com (and) yours@mit] evaluates to --> {myemail@gmail.com,yours@mit} \n"
                        + "            [myemail@gmail.com] evaluates to --> {myemail@gmail.com}\n"
                        + "            [yours@mit] evaluates to --> {yours@mit}",
                expression.structureAsString(storedLists, 0));

        ListExpression expression2 = ListParser.parse("myEmail@gmail.com,");
        expression2.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[myemail@gmail.com (and) ] evaluates to --> {myemail@gmail.com} \n"
                        + "            [myemail@gmail.com] evaluates to --> {myemail@gmail.com}\n" + "    ",
                expression2.structureAsString(storedLists, 0));

    }

    // covers: StructureAsString union of subexpressions and defined lists
    @Test public void testStructureAsStringUnionSubexpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = myEmail@gmail.com, yours@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = yes@mit, no@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a, c, b");
        assertEquals(
                "[a (and) (c , b)] evaluates to --> {myemail@gmail.com,no@mit,yes@mit,yours@mit} \n"
                        + "            [a] is equal to --> {myemail@gmail.com,yours@mit}\n"
                        + "            [c (and) b] evaluates to --> {no@mit,yes@mit} \n"
                        + "                            [c] is equal to --> {}\n"
                        + "                            [b] is equal to --> {no@mit,yes@mit}",
                expression.structureAsString(storedLists, 0));
    }

    // covers: StructureAsString intersection of emails and email with empty
    @Test public void testStructureAsStringIntersectionWithEmails() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("a@mit * b@mit");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[a@mit (intersection) b@mit] evaluates to --> {} \n"
                + "            [a@mit] evaluates to --> {a@mit}\n" + "            [b@mit] evaluates to --> {b@mit}",
                expression.structureAsString(storedLists, 0));

        ListExpression expression2 = ListParser.parse("a@mit * ");
        assertEquals("[a@mit (intersection) ] evaluates to --> {} \n" + "            [a@mit] evaluates to --> {a@mit}\n"
                + "    ", expression2.structureAsString(storedLists, 0));

        ListExpression expression3 = ListParser.parse("a@mit * a@mit");
        assertEquals("[a@mit (intersection) a@mit] evaluates to --> {a@mit} \n"
                + "            [a@mit] evaluates to --> {a@mit}\n" + "            [a@mit] evaluates to --> {a@mit}",
                expression3.structureAsString(storedLists, 0));
    }

    // covers: StructureAsString intersection of subexpressions and defined lists
    @Test public void testStructureAsStringIntersectionWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = x@mit, y@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a * x@mit");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[a (intersection) x@mit] evaluates to --> {x@mit} \n"
                + "            [a] is equal to --> {x@mit,y@mit}\n" + "            [x@mit] evaluates to --> {x@mit}",
                expression.structureAsString(storedLists, 0));
        ListExpression expression2 = ListParser.parse("a * b");
        expression2.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[a (intersection) b] evaluates to --> {x@mit} \n" + "            [a] is equal to --> {x@mit,y@mit}\n"
                        + "            [b] is equal to --> {x@mit,z@mit}" + "",
                expression2.structureAsString(storedLists, 0));

        ListExpression expression3 = ListParser.parse("(x@mit, y@mit) * (x@mit, z@mit)");
        expression3.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[(x@mit , y@mit) (intersection) (x@mit , z@mit)] evaluates to --> {x@mit} \n"
                        + "            [x@mit (and) y@mit] evaluates to --> {x@mit,y@mit} \n"
                        + "                            [x@mit] evaluates to --> {x@mit}\n"
                        + "                            [y@mit] evaluates to --> {y@mit}\n"
                        + "            [x@mit (and) z@mit] evaluates to --> {x@mit,z@mit} \n"
                        + "                            [x@mit] evaluates to --> {x@mit}\n"
                        + "                            [z@mit] evaluates to --> {z@mit}",
                expression3.structureAsString(storedLists, 0));
    }

    // covers: StructureAsString difference of emails and email with empty
    @Test public void testStructureAsStringDifferenceWithEmails() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a@mit ! b@mit");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[a@mit  (difference)  b@mit] evaluates to --> {a@mit}\n"
                + "            [a@mit] evaluates to --> {a@mit}\n" + "            [b@mit] evaluates to --> {b@mit}",
                expression.structureAsString(storedLists, 0));

        ListExpression expression2 = ListParser.parse("a@mit ! ");
        assertEquals("[a@mit  (difference)  ] evaluates to --> {a@mit}\n"
                + "            [a@mit] evaluates to --> {a@mit}\n" + "    ",
                expression2.structureAsString(storedLists, 0));

        ListExpression expression3 = ListParser.parse("a@mit ! a@mit");
        assertEquals(
                "[a@mit  (difference)  a@mit] evaluates to --> {}\n" + "            [a@mit] evaluates to --> {a@mit}\n"
                        + "            [a@mit] evaluates to --> {a@mit}",
                expression3.structureAsString(storedLists, 0));
    }

    // covers: StructureAsString difference of subexpressions and defined lists
    @Test public void testStructureAsStringDifferenceWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = x@mit, y@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a ! x@mit");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[a  (difference)  x@mit] evaluates to --> {y@mit}\n            [a] is equal to --> {x@mit,y@mit}\n            [x@mit] evaluates to --> {x@mit}",
                expression.structureAsString(storedLists, 0));
        ListExpression expression2 = ListParser.parse("a ! b");
        expression2.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[a  (difference)  b] evaluates to --> {y@mit}\n            [a] is equal to --> {x@mit,y@mit}\n            [b] is equal to --> {x@mit,z@mit}",
                expression2.structureAsString(storedLists, 0));

        ListExpression expression3 = ListParser.parse("(x@mit, y@mit) ! (x@mit, z@mit)");
        expression3.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[(x@mit , y@mit)  (difference)  (x@mit , z@mit)] evaluates to --> {y@mit}"
                        + "\n            [x@mit (and) y@mit] evaluates to --> {x@mit,y@mit} \n"
                        + "                            [x@mit] evaluates to --> {x@mit}\n"
                        + "                            [y@mit] evaluates to --> {y@mit}"
                        + "\n            [x@mit (and) z@mit] evaluates to --> {x@mit,z@mit} \n"
                        + "                            [x@mit] evaluates to --> {x@mit}\n"
                        + "                            [z@mit] evaluates to --> {z@mit}",
                expression3.structureAsString(storedLists, 0));

    }

    // covers: StructureAsString sequence of emails and email with empty
    @Test public void testStructureAsStringSequenceWithEmails() throws UnableToParseException {

        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a@mit ; b@mit");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[a@mit] evaluates to --> {a@mit}\n" + "[b@mit] evaluates to --> {b@mit}",
                expression.structureAsString(storedLists, 0));

        ListExpression expression2 = ListParser.parse("a@mit ; ");
        assertEquals("[a@mit] evaluates to --> {a@mit}\n", expression2.structureAsString(storedLists, 0));

        ListExpression expression3 = ListParser.parse("a@mit ; a@mit");
        assertEquals("[a@mit] evaluates to --> {a@mit}\n" + "[a@mit] evaluates to --> {a@mit}",
                expression3.structureAsString(storedLists, 0));
    }

    // covers: StructureAsString sequence of subexpressions and defined lists
    @Test public void testStructureAsStringSequenceWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListParser.parse("a = x@mit, y@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a;b");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[a] is equal to --> {x@mit,y@mit}\n" + "[b] is equal to --> {x@mit,z@mit}",
                expression.structureAsString(storedLists, 0));
        ListExpression expression2 = ListParser.parse("a;b");
        expression2.evaluate(storedLists, new HashSet<>());
        assertEquals("[a] is equal to --> {x@mit,y@mit}\n" + "[b] is equal to --> {x@mit,z@mit}",
                expression2.structureAsString(storedLists, 0));
    }

    // covers: StructureAsString sequence ordering
    @Test public void testStructureAsStringSequenceOrdering() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a = b,c ; c = m@mit; b= n@mit; a");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[(a = (b , c))] evaluates to --> {n@mit,m@mit}\n"
                + "            [b (and) c] evaluates to --> {n@mit,m@mit} \n"
                + "                            [b] is equal to --> {n@mit}\n"
                + "                            [c] is equal to --> {m@mit}\n"
                + "[(c = m@mit)] evaluates to --> {m@mit}\n" + "            [m@mit] evaluates to --> {m@mit}\n"
                + "[(b = n@mit)] evaluates to --> {n@mit}\n" + "            [n@mit] evaluates to --> {n@mit}\n"
                + "[a] is equal to --> {n@mit,m@mit}", expression.structureAsString(storedLists, 0));
        ListExpression expression2 = ListParser.parse(" c = m@mit; a = b,c ;b= n@mit; a");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals("[(c = m@mit)] evaluates to --> {m@mit}\n" + "            [m@mit] evaluates to --> {m@mit}\n"
                + "[(a = (b , c))] evaluates to --> {n@mit,m@mit}\n"
                + "            [b (and) c] evaluates to --> {n@mit,m@mit} \n"
                + "                            [b] is equal to --> {n@mit}\n"
                + "                            [c] is equal to --> {m@mit}\n"
                + "[(b = n@mit)] evaluates to --> {n@mit}\n" + "            [n@mit] evaluates to --> {n@mit}\n"
                + "[a] is equal to --> {n@mit,m@mit}", expression2.structureAsString(storedLists, 0));
    }

    // covers: StructureAsString parallel of emails and email with empty

    @Test public void testStructureAsStringParallelWithEmails() throws UnableToParseException {

        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a@mit | b@mit");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[a@mit (parallel) b@mit] evaluates to --> {}\n" + "            [a@mit] evaluates to --> {a@mit}\n"
                        + " ------------------------ \n" + "            [b@mit] evaluates to --> {b@mit}\n",
                expression.structureAsString(storedLists, 0));

        ListExpression expression2 = ListParser.parse("a@mit | ");
        assertEquals("[a@mit (parallel) ] evaluates to --> {}\n" + "            [a@mit] evaluates to --> {a@mit}\n"
                + " ------------------------ \n" + "    \n", expression2.structureAsString(storedLists, 0));

        ListExpression expression3 = ListParser.parse("a@mit | a@mit");
        assertEquals(
                "[a@mit (parallel) a@mit] evaluates to --> {}\n" + "            [a@mit] evaluates to --> {a@mit}\n"
                        + " ------------------------ \n" + "            [a@mit] evaluates to --> {a@mit}\n",
                expression3.structureAsString(storedLists, 0));
    }

    // covers: StructureAsString parallel of subexpressions and defined lists
    @Test public void testStructureAsStringParallelWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();

        ListExpression expression = ListParser.parse("(a = (x@mit, y@mit)),(b = (x@mit, z@mit)), (a|b|c) ");

        expression.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[(a = (x@mit , y@mit)) (and) ((b = (x@mit , z@mit)) , ((a | b) | c))] evaluates to --> {x@mit,y@mit,z@mit} \n"
                        + "            [(a = (x@mit , y@mit))] evaluates to --> {x@mit,y@mit}\n"
                        + "                            [x@mit (and) y@mit] evaluates to --> {x@mit,y@mit} \n"
                        + "                                            [x@mit] evaluates to --> {x@mit}\n"
                        + "                                            [y@mit] evaluates to --> {y@mit}\n"
                        + "            [(b = (x@mit , z@mit)) (and) ((a | b) | c)] evaluates to --> {x@mit,z@mit} \n"
                        + "                            [(b = (x@mit , z@mit))] evaluates to --> {x@mit,z@mit}\n"
                        + "                                            [x@mit (and) z@mit] evaluates to --> {x@mit,z@mit} \n"
                        + "                                                            [x@mit] evaluates to --> {x@mit}\n"
                        + "                                                            [z@mit] evaluates to --> {z@mit}\n"
                        + "                            [(a | b) (parallel) c] evaluates to --> {}\n"
                        + "                                            [a (parallel) b] evaluates to --> {}\n"
                        + "                                                            [a] is equal to --> {x@mit,y@mit}\n"
                        + " ------------------------ \n"
                        + "                                                            [b] is equal to --> {x@mit,z@mit}\n"
                        + "\n" + " ------------------------ \n"
                        + "                                            [c] is equal to --> {}\n" + "",
                expression.structureAsString(storedLists, 0));

    }

    // covers: StructureAsString parallel ordering
    @Test public void testStructureAsParallelSubexpression() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("(c = m@mit|b= n@mit), a = b,c ");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals(
                "[((c = m@mit) | (b = n@mit)) (and) (a = (b , c))] evaluates to --> {n@mit,m@mit} \n"
                        + "            [(c = m@mit) (parallel) (b = n@mit)] evaluates to --> {}\n"
                        + "                            [(c = m@mit)] evaluates to --> {m@mit}\n"
                        + "                                            [m@mit] evaluates to --> {m@mit}\n"
                        + " ------------------------ \n"
                        + "                            [(b = n@mit)] evaluates to --> {n@mit}\n"
                        + "                                            [n@mit] evaluates to --> {n@mit}\n" + "\n"
                        + "            [(a = (b , c))] evaluates to --> {n@mit,m@mit}\n"
                        + "                            [b (and) c] evaluates to --> {n@mit,m@mit} \n"
                        + "                                            [b] is equal to --> {n@mit}\n"
                        + "                                            [c] is equal to --> {m@mit}",
                expression.structureAsString(storedLists, 0));
    }

    // covers: empty objects creations

    @Test public void testStructureAsStringEmptyObjects() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListExpression expression = ListParser.parse("a = b,c");
        expression.evaluate(storedLists, new HashSet<>());

        assertEquals(
                "[(a = (b , c))] evaluates to --> {}\n            [b (and) c] evaluates to --> {} \n                            [b] is equal to --> {}\n                            [c] is equal to --> {}",
                expression.structureAsString(storedLists, 0));
    }

    @Test public void testGetAllListNamesEmail() throws UnableToParseException {
        ListExpression expression = ListParser.parse("myEmail@gmail.com");
        assertEquals(Set.of(), expression.getAllListNames(), "expected to see correct set");

    }

    // covers: getAllListNames on empty list name
    @Test public void testGetAllListNamesEmptyListName() throws UnableToParseException {
        ListExpression expression1 = ListParser.parse("a = ");
        assertEquals(Set.of("a"), expression1.getAllListNames(), "expected to see correct set");

        ListExpression expression2 = ListParser.parse("a");
        assertEquals(Set.of("a"), expression2.getAllListNames(), "expected to see correct set");
    }

    // covers: getAllListNames list name defined by an email
    @Test public void testGetAllListNamesListNameOfEmail() throws UnableToParseException {
        ListExpression expression = ListParser.parse("a = m@gmail");
        assertEquals(Set.of("a"), expression.getAllListNames(), "expected to see correct set");

    }

    // covers: getAllListNames list name defined by subexpression (including other
    // list names)
    @Test public void testGetAllListNamesListNameSubExpr() throws UnableToParseException {
        ListExpression expression = ListParser.parse("a = myEmail@gmail.com, yours@mit");
        assertEquals(Set.of("a"), expression.getAllListNames(), "expected to see correct set");

        ListExpression expression2 = ListParser.parse("b = a ! yours@mit, yours@mit");
        assertEquals(Set.of("a", "b"), expression2.getAllListNames(), "expected to see correct set");

        ListExpression expression3 = ListParser.parse("c = a ! yours@mit, yes@u");
        assertEquals(Set.of("a", "c"), expression3.getAllListNames(), "expected to see correct set");

    }

    // covers: getAllListNames union of emails and emails with empty
    @Test public void testGetAllListNamesUnionOfEmails() throws UnableToParseException {
        ListExpression expression = ListParser.parse("myEmail@gmail.com, yours@mit");
        assertEquals(Set.of(), expression.getAllListNames(), "expected to see correct set");

        ListExpression expression2 = ListParser.parse("myEmail@gmail.com,");
        assertEquals(Set.of(), expression2.getAllListNames(), "expected to see correct set");

    }

    // covers: getAllListNames union of subexpressions
    @Test public void testGetAllListNamesUnionSubexpressions() throws UnableToParseException {
        ListExpression expression = ListParser.parse("a, c, b");
        assertEquals(Set.of("a", "c", "b"), expression.getAllListNames(), "expected to see correct set");
    }

    // covers: getAllListNames intersection of subexpressions
    @Test public void testGetAllListNamesIntersectionWithSubExpressions() throws UnableToParseException {
        ListExpression expression = ListParser.parse("a * x@mit");
        assertEquals(Set.of("a"), expression.getAllListNames(), "expected to see correct set");
        ListExpression expression2 = ListParser.parse("a * b");
        assertEquals(Set.of("a", "b"), expression2.getAllListNames(), "expected to see correct set");
    }

    // covers: getAllListNames difference of subexpressions and defined lists
    @Test public void testGetAllListNamesDifferenceWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListParser.parse("a = x@mit, y@mit").evaluate(storedLists, new HashSet<>());
        ListParser.parse("b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a ! x@mit");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals(Set.of("a"), expression.getAllListNames(), "expected to see correct set");
        ListExpression expression2 = ListParser.parse("a ! b");
        expression2.evaluate(storedLists, new HashSet<>());
        assertEquals(Set.of("a", "b"), expression2.getAllListNames(), "expected to see correct set");
    }

    // covers: getAllListNames sequence of subexpressions and defined lists
    @Test public void testGetAllListNamesSequenceWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListParser.parse("a = x@mit, y@mit; b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals(Set.of("a"), expression.getAllListNames(), "expected to see correct set");
        ListExpression expression2 = ListParser.parse("b");
        expression2.evaluate(storedLists, new HashSet<>());
        assertEquals(Set.of("b"), expression2.getAllListNames(), "expected to see correct set");

    }

    // covers: getAllListNames sequence ordering
    @Test public void testGetAllListNamesSequenceOrdering() throws UnableToParseException {
        ListExpression expression = ListParser.parse("a = b,c ; c = m@mit; b= n@mit; a");
        assertEquals(Set.of("a", "b", "c"), expression.getAllListNames(), "expected to see correct set");
    }

    // covers: getAllListNames parallel of subexpressions and defined lists
    @Test public void testGetAllListNamesParallelWithSubExpressions() throws UnableToParseException {
        StoredListNames storedLists = new StoredListNames();
        ListParser.parse("a = x@mit, y@mit| b = x@mit, z@mit").evaluate(storedLists, new HashSet<>());

        ListExpression expression = ListParser.parse("a");
        expression.evaluate(storedLists, new HashSet<>());
        assertEquals(Set.of("a"), expression.getAllListNames(), "expected to see correct set");
        ListExpression expression2 = ListParser.parse("b");
        expression2.evaluate(storedLists, new HashSet<>());
        assertEquals(Set.of("b"), expression2.getAllListNames(), "expected to see correct set");

    }

    // covers: getAllListNames parallel ordering
    @Test public void testGetAllListNamesParallelOrdering() throws UnableToParseException {
        ListExpression expression = ListParser.parse("(a = l@mit | c = m@mit| b= n@mit); (b,c)");
        assertEquals(Set.of("a", "b", "c"), expression.getAllListNames(), "expected to see correct set");
    }


    // covers: empty objects creations
    @Test public void testGetAllListNamesEmptyObjects() throws UnableToParseException {
        ListExpression expression = ListParser.parse("a = b,c");
        assertEquals(Set.of("a", "b", "c"), expression.getAllListNames(), "expected to see correct set");
    }
    //covers stringOfExpressions 0
    @Test 
    public void testStringOfExpressionsNone() {
        assertEquals("", ListExpression.stringOfExpressions(Set.of()));
        assertEquals("", ListExpression.stringOfExpressions(Set.of(new EmptyList())));
    }
    //covers stringOfExpression 1
    @Test 
    public void testStringOfExpressionsOne() {
        assertEquals("sam@mit.edu", ListExpression.stringOfExpressions(Set.of(new Email("sam", "mit.edu"))));
        assertEquals("shariqah@mit.edu", ListExpression.stringOfExpressions(Set.of(new Email("shariqah", "mit.edu"))));
        assertEquals("violetta@mit.edu", ListExpression.stringOfExpressions(Set.of(new Email("violetta", "mit.edu"))));
    }
  //covers stringOfExpression >1
    @Test 
    public void testStringOfExpressionsMultiple() {
        String result=ListExpression.stringOfExpressions(Set.of(new Email("sam", "mit.edu"),new Email("shariqah", "mit.edu"),new Email("violetta", "mit.edu")));
        assertTrue(result.contains("sam@mit.edu"));
        assertTrue(result.contains("shariqah@mit.edu"));
        assertTrue(result.contains("violetta@mit.edu"));
       }
}
