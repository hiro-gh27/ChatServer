import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer{
    private ServerSocket server;
    private List ClientList = new LinkedList();
    
    public void listen(){	
	try{
	    int count = 1; //クライアントのカウント用変数
	    server = new ServerSocket(18080);
	    System.out.println("ChatServerをポート18080で起動しました");
	    while(true){
		Socket socket = server.accept(); //接続要求の待機
		System.out.println("クライアント"+count+"が接続してきました"); //接続されるとサーバー側に表示される
		ChatClientHandler handler = new ChatClientHandler(socket, ClientList, "undefined"+count); //コンストラクタの呼び出し
		ClientList.add(handler);//handlerをクライアントリストに加える
		handler.start(); //スレッド処理
		count++;
	    }
	}catch(IOException e){
	    e.printStackTrace();
	}
    }
    
    public static void main(String[] args){
        ChatServer chat = new ChatServer();
	chat.listen();
    }
}
