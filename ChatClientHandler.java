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
                }
                else if(command[0].equalsIgnoreCase("whoami")){
                    whoami();
                }
                else if(command[0].equalsIgnoreCase("users")){
                    users();
                }
		else if(command[0].equalsIgnoreCase("post")){
		    if(command.length == 2){
			post(command[1]);
		    }else{
			send("Post Command : post message");
		    }
		}
                else{
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

    /*---  ここからコマンド ---*/
    //人に送るコマンドは最初に"\r\n"を送る事で、相手に改行してからメッセージの表示を行い、
    //そのあとに空の文字列を送る事で、"> "の表示を行っている
    //これらはsendコマンドに

    public void whoami() throws IOException{ send(getClientName()); }

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

    public void users() throws IOException{
        List nameList = new ArrayList();
        String sendName = "";
        for(int i = 0; i < ClientList.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)ClientList.get(i);
            nameList.add(handler.getClientName());
        }
        Collections.sort(nameList);
        for(int i = 0; i < ClientList.size(); i++){
            sendName += nameList.get(i); //文字の結合
            if(i != ClientList.size()-1) { 
                sendName += "," ;
            } //最後以外に","をつける
        }
        send(sendName);
    }

    public void post(String message) throws IOException{
	List nameList = new ArrayList(); //送るhandlerの格納用配列
	String sendName = "";
	if(ClientList.size() > 1){ //自分以外にも人がいるとき
	    for(int i = 0; i < ClientList.size(); i++){
		ChatClientHandler handler = (ChatClientHandler)ClientList.get(i);
		if(handler != this){ //自分以外にメッセージを送る
		    handler.send("\r\n"+getClientName()+" : "+message);
		    handler.send("");
		    nameList.add(handler.getClientName());
		}
	    }
	    if(nameList.size() > 0){//ユーザにrejectされてもpostできる人がいるとき
		Collections.sort(nameList);
		for(int i = 0; i < nameList.size(); i++){
		    sendName = sendName + nameList.get(i); //送った人の名前を連結して行く
		    if(nameList.size()-1 != i){ sendName =  sendName + ","; } //最後以外に","をつける処理
		}
		send(sendName);
	    }else{ 
		send("no one receive message");
	    }
	}else{
	    send("no one receive message");
	}
    }
}
