import java.net.*;
import java.util.*;
import java.io.*;

public class ChatClientHandler{
    
    private Socket socket ;
    private BufferedReader in;
    private BufferedWriter out;
    private List ClientList;
    private String name;
    
    public ChatClientHandler(Socket socket, List ClientList, String name){
	this.socket = socket;
	this.ClientList = ClientList;
	this.name = name;
    }

    public void open() throws IOException{
	InputStream socketIn = socket.getInputStream();
	OutputStream socketOut = socket.getOutputStream();
	
	this.in = new BufferedReader(new InputStreamReader(socketIn));
	this.out = new BufferedWriter(new OutputStreamWriter(socketOut));
    }
}
