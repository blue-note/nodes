import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import javax.swing.JOptionPane;
import java.util.*;
 
public class WebService
{
	public static void main(String[] args)
	{
        // default port and delay
        int port = 8888;
		
		// parse command line arguments to override defaults
        if (args.length > 0)
        {
            try
            {
                port = Integer.parseInt(args[0]);

            }
			catch (NumberFormatException ex)
            {
                System.err.println("USAGE: java YahtzeeService [port]");
                System.exit(1);
            }
		}
		
		// set up an HTTP server to listen on the selected port
		try
		{
			InetSocketAddress addr = new InetSocketAddress(port);
			HttpServer server = HttpServer.create(addr, 1);
       
			server.createContext("/move.json", new MoveHandler());
        
			server.start();
			System.out.println("server started");
		}
		catch (IOException ex)
		{
			ex.printStackTrace(System.err);
			System.err.println("Could not start server");
		}
	}

	public static class MoveHandler implements HttpHandler {
		 @Override
        public void handle(HttpExchange ex) throws IOException
        {
        	
        	//System.err.println(ex.getRequestURI());
            String q = ex.getRequestURI().getQuery();
           // System.out.println(q);
            String[] split = q.split(",");
            State s = new State(0);
            //add nodes to the state
            int nodeCap = Integer.parseInt(split[split.length-1]);
            for (int i = 0; i < split.length-1; i++) {
            	String str = split[i];
            	String[] sep = str.split(" ");
            	String color = sep[0];
            	int c;
            	if (color.equals("green")) c = 0;
            	else c = 1;
            	s.state.add(new Node(c));
            }
            //add neighbors to each node
            for (int i = 0; i < split.length-1; i++) {
            	String str = split[i];
            	Node node = s.state.get(i);
            	String[] sep = str.split(" ");
            	if (sep.length > 1) {
            		//node has neighbors
            		for (int j = 1; j < sep.length; j++) {
            			int n = Integer.parseInt(sep[j]); //index of the neighbor
            			Node neighbor = s.state.get(n);
            			if (!node.hasNeighbor(neighbor)) {
            				node.initNeighbor(neighbor);
            			}

            		}
            	}

            }


            MCTree tree = new MCTree(nodeCap);
            LinkedList<Integer> result = tree.getResponse(s);
            //response section: "response" should instead get output from MCTree	
            StringBuilder response = new StringBuilder("{");
            for (int i = 0; i < result.size(); i++) {
            	response.append("\""+i+"\":"+"\""+result.get(i)+"\",");
            }
            response = response.deleteCharAt(response.length()-1);
            response.append("}");
			ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            byte[] responseBytes = response.toString().getBytes();
            ex.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
            ex.getResponseBody().write(responseBytes);
            ex.close();
        }



	}

  
}