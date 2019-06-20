package SocketTools;

import Data.DataOperation;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Septet;
import org.javatuples.Triplet;
import sun.security.util.Password;
import teamUI.INathlete;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;


public class ProServer {

    ArrayList<Thread> proline = new ArrayList<>();

    public static void main(String[] args) {
        ProServer test = new ProServer();
        test.start();
    }

    public void start() {
        // 为了简单起见，所有的异常信息都往外抛
        try {
            TServer loginServer = new TServer();
            new Thread(loginServer).start();
            int port = ServerData.PORT_Chief;
            ServerSocket server = new ServerSocket(port);
            System.out.println("等待与客户端建立连接.....");
            while (true) {
                // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
                Socket socket = server.accept();
                // 每接收到一个Socket就建立一个新的线程来处理它
                Thread master = new Thread(new TProMaster(socket));
                proline.add(master);
                master.start();
            }
//            server.close();
        } catch (Exception e) {

        }
    }
}

//    处理Socket请求的线程类
class TProMaster implements Runnable {
    private Socket socket;

    public TProMaster(Socket socket) {
        this.socket = socket;
    }/*构造函数*/

    @Override
    public void run() {
        try {
            handlerSocket();
        } catch (Exception e) {
            e.toString();
        }
    }

    //处理与总裁判通信
    private void handlerSocket() throws Exception {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), "UTF-8"));
        BufferedWriter MessageToClient = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream()));
        DataOperation dbo=new DataOperation();
        String proName;//项目名
        int group;//年龄组别
        // 读取客户端发过来的信息了
        System.out.println("服务器链接" + socket.isConnected() + socket.getPort());

        proName = br.readLine();//项目名
        group = Integer.parseInt(br.readLine());//组别
        //boolean MOrF=Boolean.parseBoolean(br.readLine());//男生组String='true'
        System.out.println("Form Client：" + proName + "开始");
//                创建项目信息处理线程是否进行

        int indexj=proName.indexOf("决赛");
        int indexc=proName.indexOf("预赛");
        //未进行的初赛、已完成相应初赛且尚未进行的决赛则进入后流程
        if ((indexj==-1&&dbo.SearchMatch(proName.substring(0,indexc),group)==0) ||
                (indexj!=-1&&dbo.SearchMatch(proName.substring(0,indexj),group)==3)) {
            Thread prohandle = new Thread(new TProHandle(proName, group, socket));

            //开始处理比赛信息
            prohandle.start();
            //输出流回应一下客户端
            MessageToClient.write("Start" + '\n');
            MessageToClient.flush();
            System.out.println(proName+"Start");

            //刷新流
            MessageToClient.flush();
            //等待比赛结束
            prohandle.join();


            MessageToClient.write("End" + '\n');
            MessageToClient.flush();
            //TODO:修改比赛状态
            System.out.println("End");
//            System.out.println(br.readLine());
        } else {
            System.out.println("Wrong");
            MessageToClient.write("WrongRequest");
        }
        //System.out.println("To Client:" + "copy:" + proName + "开始");
//        br.close();
//        MessageToClient.close();
    }


}

/*处理单一项目小组比赛信息的线程*/
class TProHandle implements Runnable {
    //项目信息
    private String ProName;
    private String ProNameRaw;
    private Boolean isFinal;
    private String ProID;
    private int group;
    //private int MOrF;

    //Socket连接
    public Socket Chief;
    private Socket Group;
    private ArrayList<Socket> Judges;
    String IPOfGroup;
    ArrayList<String> IPOfJudges = new ArrayList<String>();

    //数据库工具
    Data.DataOperation myConn;




    //        构造函数
    public TProHandle(String Project, int group, Socket c) {
        myConn = new DataOperation();
        ProNameRaw = Project;
        this.group = group;
        if(ProNameRaw.indexOf("决赛")!=-1){
            isFinal=true;
            ProName=ProNameRaw.substring(0,ProNameRaw.indexOf("决赛"));
        }else {
            isFinal=false;
            ProName=ProNameRaw.substring(0,ProNameRaw.indexOf("预赛"));
        }
        ProID=myConn.SearchPID(ProName);
        Chief = c;
    }

    @Override
    public void run() {
//        try {
//            Thread.sleep(6000);
//        }catch (Exception e){
//            e.toString();
//        }
        handlerSocket();
    }

    private void handlerSocket() {
        ArrayList<Pair<String, String>> Aths=new ArrayList<>();

        if(!isFinal)
            Aths= myConn.SearchPeopleList(ProID,group);
        else
            Aths=myConn.SearchFinalPeopleList(ProID,group);

        int count=Aths.size();
        //        TODO:init读取各个裁判的ip
        IPOfGroup = myConn.SearchProject_IP(ProID,2).get(0);
        IPOfJudges=myConn.SearchProject_IP(ProID,1);
        try {

            Group = new Socket(IPOfGroup, ServerData.PORT_Judge);
            for (String ip : IPOfJudges) {

                Socket Stemp = new Socket(ip, ServerData.PORT_Judge);
                Judges.add(Stemp);
                BufferedWriter bwtemp=new BufferedWriter(new OutputStreamWriter(Stemp.getOutputStream()));
                bwtemp.write(ProName+'\n');
                bwtemp.flush();
            }
            new BufferedWriter(new OutputStreamWriter(Group.getOutputStream())).write(String.valueOf(Judges.size())+"\n");
        } catch (UnknownHostException uhe) {

        } catch (IOException ioe) {

        }
        int tag = 0;
        while (tag < Aths.size()) {
            ArrayList<Pair<String, String>> OneGroup = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                if (tag < Aths.size()) {
                    OneGroup.add(Aths.get(tag));
                    tag++;
                } else {
                    break;
                }

            }
            SendMessageOfAthletes(OneGroup);
            if (OneGroup.size()!=0) {

            } else {

                //TODO:结束进程

            }
        }
        //TODO:结束进程
    }

    private void SendMessageOfAthletes(ArrayList<Pair<String, String>> Aths) {

        Thread GroupMessage;
        //记录run类与线程的数组
        ArrayList<TSendAthletesMessage> ListOfJudges = new ArrayList<>();
        ArrayList<Thread> ListOfThread = new ArrayList<>();
        //ArrayList<TSendAthletesMessage> ListOfMarkTable=new ArrayList<>();
        HashMap<String,ArrayList<Float>> marktable=new HashMap<String,ArrayList<Float>>();

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(Group.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Group.getOutputStream()));

            GroupMessage = new Thread(new TSendAthletesMessage(Group, Aths));
            GroupMessage.start();

            for (Socket Stemp : Judges) {
                if (Stemp.isConnected()) {
                    TSendAthletesMessage TSMtemp = new TSendAthletesMessage(Stemp, Aths);
                    Thread Ttemp = new Thread(TSMtemp);
                    ListOfJudges.add(TSMtemp);
                    ListOfThread.add(Ttemp);
                    Ttemp.start();
                }
            }

            GroupMessage.join();
            //等待裁判完成打分

            for (Pair<String, String> ath:Aths
                 ) {
                marktable.put(ath.getValue1(),new ArrayList<Float>());
            }

            for (Thread t : ListOfThread) {
                t.join();
            }

            bw.write("Send Start\n");
            for (TSendAthletesMessage mark : ListOfJudges) {
//                for (Pair<String, Float>  markpair : mark.MarkTable
//                     ) {
//                    ((ArrayList<Float>)marktable.get(markpair.getValue0())).add(markpair.getValue1());
//                }
                SendMarkTable(Group, mark);
            }
            bw.write("FinishSendMarks\n");
            bw.flush();

            String feedback = br.readLine();//小组裁判的确认
//            br.close();
//            bw.close();
            if (Boolean.valueOf(feedback)) {
                //若确认通过则继续


            } else {
//                SendMessageOfAthletes(Aths);
                //TODO:某个裁判重新打分
                ReMark(Group,ListOfJudges,Aths);

            }

            //处理成绩 在确认为TRUE后
            ArrayList<Triplet<String,Float,Float>> bpMarkTable=new ArrayList<>();
            String athNum;
            while (!(athNum=br.readLine()).equals("Finished")){
                //读取奖励分惩罚分
                float bmark= Float.parseFloat(br.readLine());
                float pmark= Float.parseFloat(br.readLine());
                bpMarkTable.add(new Triplet<>(athNum,bmark,pmark));
                //传送全部裁判的打分 并存入哈希表 marktable
                ArrayList<Float> marks=new ArrayList<>();
                int amount=Integer.parseInt(br.readLine());//裁判数目
                for(int i=0;i<amount;i++){
                    marks.add(Float.parseFloat(br.readLine()));
                }
                marktable.put(athNum,marks);
            }

            for (Triplet<String,Float,Float> bpmark:bpMarkTable
                    ) {
                ArrayList<Float> marks= marktable.get(bpmark.getValue0());
                if(marks.size()>2){
                    float mark_sum=0f;
                    float mark_max=marks.get(0);
                    float mark_min=marks.get(0);
                    for(float mark:marks){
                        mark_sum+=mark;
                        if(mark>mark_max){
                            mark_max=mark;
                            break;
                        }

                        if(mark<mark_min){
                            mark_min=mark;
                            break;
                        }

                    }
                    mark_sum=(mark_sum-mark_min-mark_max)*marks.size()/(marks.size()-2)
                            +bpmark.getValue1()-bpmark.getValue2();
                    if(isFinal){
                        myConn.ModifyJScore(ProID,group,bpmark.getValue0(),mark_sum);
                    }else {
                        myConn.ModifyCScore(ProID,group,bpmark.getValue0(),mark_sum);
                    }
                }else {
                    System.out.println("运动员评分少于两个,为"+marks.size());
                }
            }


        } catch (Exception x) {
//      TODO 异常处理
        }

    }

    //重新打分
    private void ReMark(Socket groupJudge,ArrayList<TSendAthletesMessage> ListOfJudges,ArrayList<Pair<String, String>> Aths){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(groupJudge.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(groupJudge.getOutputStream()));
            String SID=br.readLine();
            TSendAthletesMessage target=null;
            for (TSendAthletesMessage mess:ListOfJudges) {
                if(mess.IDOfJudge.equals(SID)){
                    target=mess;
                    break;
                }
            }
            if(target!=null){
                Thread tSender=new Thread(target);
                tSender.start();
                tSender.join();
                bw.write("Send Start\n");
                SendMarkTable(groupJudge,target);
                bw.write("FinishSendMarks\n");

                String feedback = br.readLine();//小组裁判的确认
                if (Boolean.valueOf(feedback)) {
                    //若确认通过则返回
                    return;
                } else {
                    //递归 某个裁判重新打分
                    ReMark(groupJudge,ListOfJudges,Aths);

                }
            }else {
                System.out.println("信号错，未找到目标裁判");
            }
        }catch (IOException ioe){

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //发送从裁判获得的打分
    private void SendMarkTable(Socket target, TSendAthletesMessage message) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(target.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream()));
        /*bw.write(ProName+"marks");
        if(br.readLine().equals("copy")){

        }*/
        //输出成绩表中所有姓名+成绩
        bw.write("SendMarkTable\n");
        bw.write(message.IDOfJudge+"\n");
        while (!br.readLine().equals("ready")) ;//等待直到ready
        for (Pair<String, Float> mark : message.MarkTable     //Pair<AthNum,Mark>
                ) {
            bw.write(mark.getValue0().toString() + '\n');
            bw.write(mark.getValue1().toString() + '\n');
        }
        bw.write("Done+\n");
//        bw.close();
//        br.close();
    }
}

//发送信息线程类 系统发送小组队员名单给裁判 暂存信息
class TSendAthletesMessage implements Runnable {
    Socket target;
    String IDOfJudge;
    ArrayList<Pair<String, String>> Message;
    ArrayList<Pair<String, Float>> MarkTable = new ArrayList<>();

    public TSendAthletesMessage(Socket s, ArrayList<Pair<String, String>> a) {
        target = s;
        Message = a;
    }

    public void run() {
        try {
            SendMessage(target, Message);
        } catch (Exception e) {

        }//TODO：异常处理
    }

    //    发送运动员名单
    public void SendMessage(Socket s, ArrayList<Pair<String, String>> Aths) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        int size = Aths.size();
        if (size == 0) {
            bw.write("Over");
        }
        bw.write(Aths.size() + '\n');
        IDOfJudge = br.readLine();
        if (br.readLine().equals("ready")) {//裁判准备好
            for (Pair<String, String> Ath : Aths) {
                bw.write(Ath.getValue0() + '\n');
                bw.write(Ath.getValue1() + '\n');
            }
            bw.write("Finished");
            if (br.readLine().equals("GroupJudge")) {
                return;//目标是小组裁判时结束
            } else {
                //TODO:获取分数
                IDOfJudge=br.readLine();//等待客户端反馈命令
                String command;
                String AthNum;
                float Marks;
                while ((command = br.readLine()) != "Finished") {
                    AthNum = command;
                    Marks = Float.parseFloat(br.readLine());
                    MarkTable.add(new Pair(AthNum, Marks));
                }
                //读取名字+分数直到null;
            }
        }else {
            String Sid=br.readLine();
            DataOperation dbo=new DataOperation();
            dbo.ModifySLogin(Sid);
        }

    }

}

//处理登陆连接的线程
class TServer implements Runnable {
    ServerSocket loginserver;

    @Override
    public void run() {
        try {
            int port = ServerData.PORT_Login;
            loginserver = new ServerSocket(port);
            System.out.println("等待连接请求...");
            while (true) {
                // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
                Socket socket = loginserver.accept();
                // 每接收到一个Socket就建立一个新的线程来处理它
                Thread login = new Thread(new THandle(socket));
                login.start();
            }

        } catch (IOException e) {

        }
    }

    public void stopLoginServer() {
        //TODO:终止服务器
    }
}

class THandle implements Runnable {
    Socket User;
    private BufferedReader br;
    private BufferedWriter bw;
    THandle(Socket user) {
        this.User = user;
        System.out.println("处理信息");
//        try{
//            br = new BufferedReader(new InputStreamReader(User.getInputStream()));
//            bw = new BufferedWriter(new OutputStreamWriter(User.getOutputStream()));
//        }catch (IOException ioe){
//            System.out.println(ioe);
//        }

    }

    @Override
    public void run() {

        try {
            DataOperation dbo=new DataOperation();
            br = new BufferedReader(new InputStreamReader(User.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(User.getOutputStream()));
            String command=br.readLine();//"login request"
            switch (command){
                case "login request":
                    String SID = br.readLine();
                    String Password=br.readLine();
                    if (checkIDOfJudge(SID,Password)){
                        bw.write(Boolean.TRUE.toString() + '\n');
                        //状态检索以及相应的修改
                        if(dbo.Search_SLogin(SID)){

                            bw.write(String.valueOf(-1)+ "\n");
                        }else {
                            String IP_target = User.getInetAddress().getHostAddress();
                            dbo.ModifyIP(SID, IP_target);
                            dbo.ModifySLogin(SID);//切换状态
                            System.out.println(dbo.Search_SLogin(SID));
                            bw.write(dbo.SearchStype(SID)+ "\n");//裁判状态（总裁判/小组裁判/裁判）
                        }
                    }
                    else
                        if(checkIDofGroup(SID,Password)){
                            bw.write(Boolean.TRUE.toString() + '\n');
                            bw.write(String.valueOf(ServerData.NumOfGroup)+ '\n');
                        }else
                            bw.write(Boolean.FALSE.toString() + '\n');

                    bw.flush();
                    break;
                case "exit request":
                    SID = br.readLine();
                    if (dbo.Search_SLogin(SID)) {
                        dbo.ModifySLogin(SID);
                    }
                    break;
                case "change password":
                    String TID = br.readLine();
                    String TPassword=br.readLine();
                    String newpassword=br.readLine();
                    if(changePassword(TID,TPassword,newpassword)){
                        bw.write(Boolean.TRUE.toString() + '\n');
                    }else {
                        bw.write(Boolean.FALSE.toString() + '\n');
                    }
                    bw.flush();
                    break;

                case "Search by name":
                    String athName=br.readLine();
                    break;

                case "get team name":
                    ArrayList<Pair<String,String>> message=dbo.SearchAllteam();

                    bw.write(message.size()+"\n");
                    for (Pair m:message
                         ) {
                        bw.write(m.getValue1()+"\n");
                        bw.write(m.getValue0()+"\n");
                    }
                    bw.write("Finished\n");
                    bw.flush();
                    break;
                case "Search ath by athnum":
                    String athnum=br.readLine();
                    ArrayList<Septet<String,String,String,Float,Integer,Float,Integer>> aRes = dbo.SearchAthleteGrade(athnum);
                    for (Septet m: aRes ) {
                        bw.write(m.getValue0()+"\n");
                        bw.write(m.getValue1()+"\n");
                        bw.write(m.getValue2()+"\n");
                        bw.write(m.getValue3()+"\n");
                        bw.write(m.getValue4()+"\n");
                        bw.write(m.getValue5()+"\n");
                        bw.write(m.getValue6()+"\n");
                    }
                    bw.write("Finished\n");
                    bw.flush();
                    break;
                case "Search ath by proname":
                    String proname=br.readLine();
                    //TODO:按照项目查找成绩
//                    for (Septet m: res ) {
//                        bw.write(m.getValue0()+"\n");
//                        bw.write(m.getValue1()+"\n");
//                        bw.write(m.getValue2()+"\n");
//                        bw.write(m.getValue3()+"\n");
//                        bw.write(m.getValue4()+"\n");
//                        bw.write(m.getValue5()+"\n");
//                        bw.write(m.getValue6()+"\n");
//                    }
                    bw.write("Finished\n");
                    bw.flush();
                    break;
                case "Search team by tid":
                    String tid=br.readLine();
                    ArrayList<Quartet<String,String,Float,Integer>> tRes=dbo.SearchTeamAll(tid);
                    for (Quartet m:tRes){
                        bw.write(m.getValue0()+"\n");
                        bw.write(m.getValue1()+"\n");
                        bw.write(m.getValue2()+"\n");
                        bw.write(m.getValue3()+"\n");
                    }
                    bw.write("Finished\n");
                    bw.flush();
                    break;
                case "Search team by pro info":
//                    String pName=br.readLine();
//                    int group=Integer.parseInt(br.readLine());
//                    tRes=dbo.SearchTeamAll(pName);
//                    for (Quartet m:tRes){
//                        bw.write(m.getValue0()+"\n");
//                        bw.write(m.getValue1()+"\n");
//                        bw.write(m.getValue2()+"\n");
//                        bw.write(m.getValue3()+"\n");
//                    }
//                    bw.write("Finished\n");
//                    bw.flush();
//                    break;

                default:
                    System.out.println("wrong command");

            }

        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        System.out.println("交互结束");
    }

    public boolean checkIDOfJudge(String sid,String passward) {
        //验证身份，通过则返回true 并调整裁判的IP以及状态
        Data.DataOperation dbo = new DataOperation();
        if (dbo.Stuff_Verify_password(sid,passward)) {
            System.out.println("验证成功");
            return true;
        } else {
            return false;
        }
    }
    public  boolean checkIDofGroup(String tid,String passward){
        Data.DataOperation dbo = new DataOperation();
        ArrayList<Quartet<String,String,String,String>> team=dbo.SearchTeam(tid);
        if(!team.isEmpty()){
            if (team.size()==1&&team.get(0).getValue2().equals(passward))
                return true;
        }
        return false;
    }
    public boolean changePassword(String id,String password,String newPassword){
        if(checkIDOfJudge(id,password)){
            Data.DataOperation dbo = new DataOperation();
            return dbo.ModifySPassword(id,newPassword);
        }else {
            if(checkIDofGroup(id,password)){
                Data.DataOperation dbo = new DataOperation();
                return dbo.ModifyTPassword(id,newPassword);
            }
        }
        return false;
    }
    public  ArrayList<Septet<String,String,String,Float,Integer,Float,Integer>> searchByName(String athName){
        ArrayList<Septet<String,String,String,Float,Integer,Float,Integer>>res=new ArrayList<>();
        Data.DataOperation dbo = new DataOperation();

        return res;
    }
}


