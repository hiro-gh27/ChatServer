import java.net.*;
import java.io.*;

public class ChatServer{
    private ServerSocket server;
    public void listen(){	
	try{
	    server = new ServerSocket(18080);
	    System.out.println("ChatServerをポート18080で起動しました");
	}catch(IOException e){
	    e.printStackTrace();
	}
    }
    
    public static void main(String[] args){
        ChatServer chat = new ChatServer();
	chat.listen();
    }
}
