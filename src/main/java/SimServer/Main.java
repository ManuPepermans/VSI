package SimServer;

import clients.RealClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;
import extras.Quat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Manu Pepermans
 **/

public class Main {

    public static boolean debug = true;

    public static AgentHandler agentHandler;

    public static String serverHost = "localhost";
    public static String rosHost = "172.16.108.138";
    public static String rtf = "time";

    final int port = 6660;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public static int clientID;
    public static int agentID;


    // arguments: "172.16.0.26 172.16.108.138"

    /**
     * Starting the agent
     * @param args containing the IP address of the host where ROS is running the ip address if the VSMS
     */
    public Main(String[] args) throws IOException, InterruptedException {
        if (args.length == 3)
        {
            serverHost = args[0];
            System.out.println("Server ip: "+serverHost);
            rosHost = args[1];
            System.out.println("ROS ip :"+rosHost);
            rtf = args[2];
            System.out.println("RTF check true");

        }

        if (args.length == 2)
        {
            serverHost = args[0];
            System.out.println("Server ip: "+serverHost);
            rosHost = args[1];
            System.out.println("ROS ip:"+rosHost);
        }

        System.out.println("Starting agent raytracer...without optimalisation V0.1");


        Thread robotUpdater = new Thread(agentHandler);

        robotUpdater.start();

        init();

        stopConnection();

        RealClient client = new RealClient(rosHost, 9090, "ThisAgent");
        client.ownedAgents.add(agentHandler.newAgent("main", new Pose(new Point(0, 0, 0), Quat.toQuaternion(0,0,0)), new Twist()));
        //client.externalAgents.add(agentHandler.newRobot("inTheWay", new Pose(new Point(3, 0, 0), Quat.toQuaternion(0,0,90)), new Twist()));

while(true) {
}

    }

    public void whoAmI()
    {
        Gson details = new Gson();
        String clientInfo = "{\"model\":\"F1\",\"robot\":\"true\"}\n";
        JsonObject detailsJ = details.fromJson(clientInfo, JsonObject.class);
        out.println(detailsJ);
    }


    /**
     * Set-up VSMS parameters
     * @param ip IP address where socket has to goe to
     * @param port Port number for socket
     * @throws IOException Failed to create socket
     */
    public void createSocket(String ip, int port) throws IOException
    {
        System.out.println("Creating socket to '" + serverHost + "' on port " + port);
        socket = new Socket(ip,port);
       out =
                new PrintWriter(socket.getOutputStream(), true);
        in =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        whoAmI();
    }



    public void stopConnection() {
        out.close();
        try{socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }


    /**
     * Init Services
     * Create socket to VSMS
     * Start threads
     */
    private void init() throws IOException {
        try{
            createSocket(serverHost,port);
        }catch(IOException e){
            e.printStackTrace();
        }

        clientID = Integer.parseInt(in.readLine());
        System.out.println(clientID);
        agentID =  Integer.parseInt(in.readLine());
        System.out.println(agentID);

        agentHandler = new AgentHandler();
    }

    public static int getAgentID() {
        return agentID;
    }

    public static int getClientID(){
        return clientID;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        new Main(args);
    }
}


