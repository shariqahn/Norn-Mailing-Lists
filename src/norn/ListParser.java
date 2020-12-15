package norn;

import java.io.File;
import java.io.IOException;

import java.util.List;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;

public class ListParser {

    /**
     * Main method. Parses and then reprints an example expression.
     * 
     * @param args command line arguments, not used
     * @throws UnableToParseException if example expression can't be parsed
     */
    public static void main(final String[] args) throws UnableToParseException {
        final String input = "a=";
        System.out.println(input);
        final ListExpression expression = ListParser.parse(input);
        System.out.println(expression);
    }

    // non terminals in grammar
    private static enum ExpressionGrammar {
        LISTEXPRESSION, EMAIL, INTERSECTION, DIFFERENCE, PRIMITIVE, WHITESPACE, USERNAME, DOMAIN, LISTNAME, LISTDEFINITION, UNION, SEQUENCE,EMPTY
    }

    private static Parser<ExpressionGrammar> parser = makeParser();

    /**
     * Compile the grammar for a mailing list expression into a parser object.
     * 
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<ExpressionGrammar> makeParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/norn/ListExpression.g");
            return Parser.compile(grammarFile, ExpressionGrammar.LISTEXPRESSION);

            // Parser.compile() throws two checked exceptions.
            // Translate these checked exceptions into unchecked RuntimeExceptions,
            // because these failures indicate internal bugs rather than client errors
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }

    /**
     * Parse a string into a mailing list expression.
     * 
     * @param string string to parse
     * @return Expression parsed from the string
     * @throws UnableToParseException if the string doesn't match the Expression
     *                                grammar
     */
    public static ListExpression parse(final String string) throws UnableToParseException {
        if(string.equals("")) {
            return new EmptyList();
        }
        // parse the example into a parse tree
        final ParseTree<ExpressionGrammar> parseTree = parser.parse(string);

        // display the parse tree in various ways, for debugging only
        // System.out.println("parse tree " + parseTree);
        // Visualizer.showInBrowser(parseTree);

        // make an AST from the parse tree
        final ListExpression expression = makeAbstractSyntaxTree(parseTree);
        // System.out.println("AST " + expression);

        return expression;
    }

    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in ListExression.g
     * @return abstract syntax tree corresponding to parseTree
     */
    private static ListExpression makeAbstractSyntaxTree(final ParseTree<ExpressionGrammar> parseTree) {
        switch (parseTree.name()) {

        case LISTEXPRESSION: {
            
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            ListExpression expression = makeAbstractSyntaxTree(children.get(0));
            for (int i = 1; i < children.size(); i += 1) {
                expression = new Parallel(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;

        }
        case SEQUENCE: {
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            ListExpression expression = makeAbstractSyntaxTree(children.get(0));
            for (int i = 1; i < children.size(); i += 1) {
                expression = new Sequence(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;

        }
        case UNION: {
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            ListExpression expression = makeAbstractSyntaxTree(children.get(0));
            
            for (int i = 1; i < children.size(); i += 1) {
                expression = new Union(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;
        }
        case DIFFERENCE: {
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            ListExpression expression = makeAbstractSyntaxTree(children.get(0));
            for (int i = 1; i < children.size(); i += 1) {
                expression = new Difference(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;
        }
        case INTERSECTION:

        {
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            ListExpression expression = makeAbstractSyntaxTree(children.get(0));
            for (int i = 1; i < children.size(); i += 1) {
                expression = new Intersection(expression, makeAbstractSyntaxTree(children.get(i)));
            }
            return expression;
        }
        case PRIMITIVE: {
            final ParseTree<ExpressionGrammar> child = parseTree.children().get(0);

            switch (child.name()) {
            case EMAIL:
                return makeAbstractSyntaxTree(child);
            case LISTNAME:
                return makeAbstractSyntaxTree(child);
            case EMPTY:
                return makeAbstractSyntaxTree(child);
            case LISTEXPRESSION:
                return makeAbstractSyntaxTree(child);

            default:
                throw new AssertionError("should never get here");
            }
        }
        case EMPTY:
        {
            return new EmptyList();
        }
        case EMAIL:

        {
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            return new Email(children.get(0).text().toLowerCase(), children.get(1).text().toLowerCase());
        }

        case LISTNAME:

        {
            return new ListName(parseTree.text().toLowerCase());
        }
        case LISTDEFINITION:{
            final List<ParseTree<ExpressionGrammar>> children = parseTree.children();
            
            if (children.size()==1) {
                return makeAbstractSyntaxTree(children.get(0));
            }
            
            ListExpression expression = makeAbstractSyntaxTree(children.get(children.size()-1));
            
            for (int i = children.size()-2; i >= 0; i -= 1) {
                expression = new ListNameDefinition(children.get(i).text().toLowerCase(),expression);
            }
            return expression;
        }
        default:
            throw new IllegalArgumentException("Unexpected value: " + parseTree.name());
        }
    }

}
