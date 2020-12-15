/* Copyright (c) 2018 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package norn;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import norn.web.ExceptionsFilter;
import norn.web.LogFilter;

/**
 * HTTP web mailing list server.
 */
public class WebServer {
    
    private final HttpServer server;
    private static final int SERVER_OK = 200;
    private static final int SEVER_ERROR = 404;
    
    // Abstraction function:
    //   AF(server) = mailing list server
    // Representation invariant:
    //   true
    // Safety from rep exposure:
    //   All fields are final and private and return types are void
    // Thread safety argument:
    //   all list expressions are thread safe and immutable
    //   expressions are stored in a thread safe data type StoredListNames
    //      - only one mutator (and non-observer) being used, which is protected by thread safe data type
    
    /**
     * Make a new mailing list server that listens for connections on port.
     * @param storedLists where shared list expressions are stored
     * @param port server port number
     * @throws IOException if an error occurs starting the server
     */
    public WebServer(StoredListNames storedLists, int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // handle concurrent requests with multiple threads
        server.setExecutor(Executors.newCachedThreadPool());
        
        List<Filter> logging = List.of(new ExceptionsFilter(), new LogFilter());
        
        HttpContext eval = server.createContext("/eval/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                handleEval(exchange, storedLists);
            }
        });
        eval.getFilters().addAll(logging);
    }
    
    /**
     * @return the port on which this server is listening for connections
     */
    public int port() {
        return server.getAddress().getPort();
    }
    
    /**
     * Start this server in a new background thread.
     */
    public void start() {
        System.err.println("Server will listen on " + server.getAddress());
        server.start();
    }
    
    /**
     * Stop this server. Once stopped, this server cannot be restarted.
     */
    public void stop() {
        System.err.println("Server will stop");
        server.stop(0);
    }
    
    /**
     * Handle a request for /eval/list-expression
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @param storedLists where list expressions are stored
     */
    private static void handleEval(HttpExchange exchange, StoredListNames storedLists) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String request = path.substring(base.length());
        String response;
         
         try {
             response = Commands.evaluateExpressionWithStructure(request, storedLists);
             exchange.sendResponseHeaders(SERVER_OK, 0);
         } catch (Exception e) {
             exchange.sendResponseHeaders(SEVER_ERROR, 0);
             response = e.getMessage();
         }
         
         // write the response to the output stream using UTF-8 character encoding
         OutputStream body = exchange.getResponseBody();
         PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);
         
         // println(..) will append a newline and auto-flush
         // - to write without a newline, use e.g. print(..) and flush()
         out.println(response);
         
         // if you do not close the exchange, the response will not be sent!
         exchange.close();
    }
}
