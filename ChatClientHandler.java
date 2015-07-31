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

    //ゲッター
    public String getClientName(){ return name; }

    public void open() throws IOException{
	InputStream socketIn = socket.getInputStream();
	OutputStream socketOut = socket.getOutputStream();
	
	this.in = new BufferedReader(new InputStreamReader(socketIn));
	this.out = new BufferedWriter(new OutputStreamWriter(socketOut));
    }

    public void close(){
	if(this.in != null) {try{in.close();} catch(IOException e){ }}
	if(this.out != null) {try{out.close();} catch(IOException e){ }}
	if(this.socket != null) {try{socket.close();} catch(IOException e){}}
    }

    public String receive() throws IOException{
	String line = in.readLine();
	System.out.println(getClientName()+" : "+line);
	return line;
    }
    //文字をクライアントに表示するメソッド
    public void send(String message) throws IOException{
	out.write(message);
	if(message.equals("")){
	    out.write("> ");
	}else if(!(message.equals("> "))){
	    out.write("\r\n");
	}
	out.flush();
    }
}
