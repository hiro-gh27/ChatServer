import java.net.*;
import java.util.*;
import java.io.*;

public class ChatClientHandler extends Thread{
    
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

    public void run(){
	try{
	    open();
	    while(true){
		send("> "); 
		String message = receive();
		String command[] = message.split(" ");

		if(command[0].equals("name")){
		    if(command.length == 2){
			name(command[1]);
		    }else{
			send("Name Command : name username");
		    }
		}else{
		    send("Not Command");
		}
	    }
	} catch(IOException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
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

    public void name(String name) throws IOException{
	boolean sameNameFlag = false;
	for(int i = 0; i < ClientList.size(); i++){
	    ChatClientHandler handler = (ChatClientHandler)ClientList.get(i);
	    if(handler != this){ //変える名前と同じ名前がいるとsameNameFlagをあげる
		if(name.equals(handler.getClientName())){ 
		    sameNameFlag = true;
		}
	    }
	}
	if(!(sameNameFlag)){ 
	    this.name = name; 
	}else{
	    send(name+" is used");
	}
    }
}
