package edu.nmsu.cs.webserver;

//// CS 468 -- Software Development P1
//// Program: WebWorker.java
//// Name: Rob Armendariz
//// Date: 02-17-2023
//// Description: 

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 *
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 *
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 *
 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;


public class WebWorker implements Runnable
{

	private Socket socket;

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s)
	{
		socket = s;
	}

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/
	public void run()
	{
		// starting connection with client
		System.err.println("Handling connection...");
		try
		{
			
			// define and initialize InputStream and OutputStream
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			// read request and store output (path) of method into string 'path'
			String path = readHTTPRequest(is);
			
			// write the HTTP Header to output stream, give 'path' as parameter to determine
			// whether file trying to be served is valid or not, then output the correct 
			// HTTP header based on validity
			writeHTTPHeader(os, "text/html", path);
			
			// write data content to client network connection, give 'path' as parameter to determine
			// whether file trying to be served is valid or not, then output the correct content
			// based on validity
			writeContent(os, path);
			
			// flush the output stream
			os.flush();
			
			// close socket (end of data stream for the moment)
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		
		// connection with client is done
		System.err.println("Done handling connection.");
		
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
	private String readHTTPRequest(InputStream is)
	{
		// used to store each line
		String line;
		
		// buffer taking in stream
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		
		// new StringBuilder to be used to store the GET request
		StringBuilder request = new StringBuilder();
		
		// used to store the actual path from GET request
		String path = "";
		
		while (true)
		{
			try
			{
				// no line is currently in buffer
				while (!r.ready())
					Thread.sleep(1);
				
				// read current line
				line = r.readLine();
				
				// while line is not of length 0
				while(!(line.isEmpty())) {
					
					// append each line to 'request'
					request.append(line + "\n");
					
					// fetch new line
					line = r.readLine();
				}
				
				// this is the first line of input stream (which contains the directory we must attempt to serve)
				String[] tokens = request.toString().split(" ");
				
				// 'path' is the directory where the file is that we must serve
				path = tokens[1];
				
				System.out.println(path);
				
				// return path as string
				return path;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		// return path as string
		return path;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String filePath) throws Exception
	{
		
		// File object contains possible path
		File checkPath = new File(System.getProperty("user.dir") + filePath);
		
		// if path is not valid
		if(!checkPath.exists()) {
		
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
			os.write("Date: ".getBytes());
			os.write((df.format(d)).getBytes());
			os.write("\n".getBytes());
			os.write("Server: Rob's very own server\n".getBytes());
			// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
			// os.write("Content-Length: 438\n".getBytes());
			os.write("Connection: close\n".getBytes());
			os.write("Content-Type: ".getBytes());
			os.write(contentType.getBytes());
			os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
			return;
		}
		// path is valid
		else {
			
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			os.write("HTTP/1.1 200 OK\n".getBytes());
			os.write("Date: ".getBytes());
			os.write((df.format(d)).getBytes());
			os.write("\n".getBytes());
			os.write("Server: Rob's very own server\n".getBytes());
			// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
			// os.write("Content-Length: 438\n".getBytes());
			os.write("Connection: close\n".getBytes());
			os.write("Content-Type: ".getBytes());
			os.write(contentType.getBytes());
			os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
			return;
		}
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, String filePath) throws Exception
	{	
		// File object contains possible path
		File checkPath = new File(System.getProperty("user.dir") + filePath);
		
		//System.out.println(checkPath.toString() + "\n\n\n\n");
		
		// if path is not valid
		if(!checkPath.exists()) {
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 Not Found</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
		}
		// path is valid
		else {
			
			//String htmlFile = checkPath.toString();
		
			// scanner gets the proper path
			Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + filePath));
			
			// path as string
			String htmlFile = checkPath.toString();
			
			// get path
			Path path = Paths.get(htmlFile);

			// store HTML file contents into "content"
			String content = new String(Files.readAllBytes(path));
			
			// set date and time format
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			
			// get current date and time
			LocalDateTime now = LocalDateTime.now();
			
			// get date and time as a formatted string
			String dateTime = dtf.format(now);
			
			// replace tag "<cs371date>" with current date and time
			content = content.replaceAll("<cs371date>", dateTime);
			
			// replace tag "<cs371server>" with name of server
			content = content.replaceAll("<cs371server>", "Rob's Server");
			
			// overwrite file contents with replaced tags
			Files.write(path, content.getBytes());
			
			// 'htmlPathFile' contains the html file contents
			String htmlPathFile = scanner.useDelimiter("\\Z").next();
			
			// close scanner instance
			scanner.close();
			
			// write the data content to the client network connection
			os.write(htmlPathFile.getBytes());
		}
//		os.write("<html><head></head><body>\n".getBytes());
//		os.write("<h3>My web server works!</h3>\n".getBytes());
//		os.write("</body></html>\n".getBytes());
		
	}

} // end class
