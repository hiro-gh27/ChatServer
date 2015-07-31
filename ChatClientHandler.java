import java.net.*;
import java.util.*;
import java.io.*;

public class ChatClientHandler extends Thread{

    private Socket socket ;
    private BufferedReader in;
    private BufferedWriter out;
    private List ClientList;
    private String name;
    private List rejectNameList = new LinkedList();

    public ChatClientHandler(Socket socket, List ClientList, String name){
        this.socket = socket;
        this.ClientList = ClientList;
        this.name = name;
    }
    public ChatClientHandler(){

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
                else if(command[0].equals("post")){
                    if(command.length == 2){
                        post(command[1]);
                    }else{
                        send("Post Command : post message");
                    }
                }
                else if(command[0].equalsIgnoreCase("reject")){ 
                    //rejectだけ2パターンあるのでオーバーロード
                    if(command.length == 1){
                        reject();
                    }else if(command.length == 2){
                        reject(command[1]);
                    }else{
                        send("Reject Command : reject name or reject");
                    }
                }
                else if(command[0].equalsIgnoreCase("tell")){
                    if(command.length == 3){
                        tell(command[1], command[2]);
                    }else{
                        send("Tell Command : tell name message");
                    }
                }
                else if(command[0].equalsIgnoreCase("help")){
                    help();
                }
                else if(command[0].equalsIgnoreCase("whoami")){
                    whoami();
                }
                else if(command[0].equalsIgnoreCase("users")){
                    users();
                }
                else if(command[0].equalsIgnoreCase("bye")){
                    bye();
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
    public List getRejectNameList(){ return rejectNameList; }

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

    public void post(String message) throws IOException{
        List nameList = new ArrayList(); //送るhandlerの格納用配列
        String sendName = "";
        if(ClientList.size() > 1){ //自分以外にも人がいるとき
            for(int i = 0; i < ClientList.size(); i++){
                ChatClientHandler handler = (ChatClientHandler)ClientList.get(i);
                boolean rejectFlag = false;
                //rejectNameListの中に自分がいないかの確認、いるとrejectFlagがあがる
                for(int j = 0; j < (handler.getRejectNameList()).size(); j++){
                    if(this == (ChatClientHandler)(handler.getRejectNameList()).get(j)){
                        rejectFlag = true;
                        break;
                    }
                }
                if(handler != this && (!(rejectFlag))){ //自分とrejectされていた人以外にメッセージを送る
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

    public void tell(String userName, String message) throws IOException{
        boolean rejectFlag = false;
        boolean sameNameFlag = false;
        ChatClientHandler handler = new ChatClientHandler(); //送り先のhandlerを記憶するためのもの
        for(int i = 0; i < ClientList.size(); i++){
            handler = (ChatClientHandler)ClientList.get(i);
            //送りたい人がみつかるとsameNameFlagをあげ、handlerを記憶したままループをぬける
            if(userName.equals(handler.getClientName())) { 
                sameNameFlag = true; //見つかりました
                break; 
            }
        }
        //rejectNameListに入っていないかの確認
        for(int i = 0; i < (handler.getRejectNameList()).size(); i++){
            if(this == (ChatClientHandler)(handler.getRejectNameList()).get(i)){
                rejectFlag = true; //rejectNameListに入っていました
                break;
            }
        }
        //rejectNameFlagが上がっていなくて、sameNameFlagが上がっている場合送る
        if(!(rejectFlag) && sameNameFlag){
            handler.send("\r\n"+this.getClientName()+" -> "+handler.getClientName()+" : "+message);
            handler.send("");
            send(handler.getClientName());
        }else{
            this.send("no one receive message");
        }
    }
    
    //rejectは二パターンあるのでオーバーロードしている
    //引数に名前がある場合
    public void reject(String rejectName) throws IOException{
        boolean rejectFlag = true;
        //すでにrejectNameListにrejectNameが存在する場合
        for(int i = 0; i < rejectNameList.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)rejectNameList.get(i);
            if((handler.getClientName()).equals(rejectName)){
                rejectNameList.remove(i);
                rejectFlag = false;
                break;
            }
        }
        if(rejectFlag){
            boolean nameFlag = true;
            //rejectNameと他のclientNameが一致する場合、rejectNameListにhandlerを加える
            for(int i = 0; i < ClientList.size(); i++){
                ChatClientHandler handler = (ChatClientHandler)ClientList.get(i);
                if(rejectName.equals(handler.getClientName())){
                    rejectNameList.add(handler);
                    nameFlag = false;
                    break;
                }
            }
            if(nameFlag){
                send("no name");
            }
        }
        reject(); //引数なしのrejectの呼び出し
    }
    //引数が無ければrejectNameListを表示するだけ
    public void reject() throws IOException{
        for(int i = 0; i < rejectNameList.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)rejectNameList.get(i);
            send("reject name : "+handler.getClientName());
        }
    }


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
    
    public void help() throws IOException{send("post, whoami, bye, name, help, users, tell, reject");}

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

    public void bye() throws IOException{
        send("bye "+name);
        int index = 0;
        for(int i = 0; i < ClientList.size(); i++){
            //ClientListの自分を探して、何番目かをindexに代入している
            ChatClientHandler handler = (ChatClientHandler)ClientList.get(i);
            if(handler.getClientName() == this.name){
                index = i;
                break;
            }
        }
        //ClientNameListからの削除
        ClientList.remove(index);
        close();
    }

}
