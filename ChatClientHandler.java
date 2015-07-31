import java.net.*;
import java.util.*;

public class ChatClientHandler{
    
    private Socket socket ;
    private List ClientList;
    private String name;
    
    public ChatClientHandler(Socket socket, List ClientList, String name){
	this.socket = socket;
	this.ClientList = ClientList;
	this.name = name;
    }
}
