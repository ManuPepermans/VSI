package clients;

import SimServer.Main;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseWithCovarianceStamped;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static SimServer.Main.*;


//import static SimServer.SimServer.agentHandler;

/**
 * Created by Manu Pepermans at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 *
 * Subscribed to the /pose_amcl of the ROS. When a new message is published it will be handled.
 * The pose will be parsed and send to the VSMS in a JSON format.
 * The VSMS will return a list of poses of agents in the same environment.
 * These agents will be added to a list ready for raytracing.
 **/

public class MyPoseCallback implements TopicCallback{
    RealClient client;
    private Socket socket;
    private PrintWriter outAgent;
    private BufferedReader inAgent;
    final String host = "172.16.0.26";
    final int agentUpdaterPort = 6666;
    private List<Client> clients;
    public long time = 0;
    public long startTime = 0;




    public MyPoseCallback(RealClient client){
        this.client = client;
    }

    //Handling the message
    @Override
    public void handleMessage(Message message) {
        System.out.println("New location");
        client.externalAgents.clear();
        PoseWithCovarianceStamped pose = PoseWithCovarianceStamped.fromMessage(message);

        //update the pose of the agent
        client.ownedAgents.get(0).updateAgent(pose.getPose().getPose(),new Twist());

        //parsing the pose of the agent to a string
        Gson location = new Gson();
        String clientInfo = "{\"pose\":" + pose.getPose().getPose() + ",\"clientID\":"+ getClientID() + ",\"agentID\":"+ getAgentID() +"}\n";
        JsonObject detailsJSON = location.fromJson(clientInfo, JsonObject.class);

        //create a socket to the VSMS
        try {
            createUpdatSocket(Main.serverHost,agentUpdaterPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start test when needed
        if(!rtf.equals("time"))
        {
            startTime = System.nanoTime();
        }
        // Sending the pose of the agents to the VSMS in JSON format
        outAgent.println(detailsJSON);
        try {
            String line;
            Gson gson = new Gson();
            while ((line = inAgent.readLine()) != "END") {
                JsonObject jsonObject  = gson.fromJson(line, JsonObject.class);
                JsonObject poseDetails = jsonObject.getAsJsonObject("pose");
                String test = poseDetails.toString();
                Pose poseTest = Pose.fromJsonString(test);

                // Create a list of external agents received from the VSMS
                client.externalAgents.add(agentHandler.newAgent("external",poseTest,new Twist()));

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            // stop test if needed
            if(!rtf.equals("time")){
                long endTime = System.nanoTime();
                time = endTime - startTime;
                try (FileWriter fw = new FileWriter(rtf + "_rtt.csv", true);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    out.println(time);
                    //more code
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }
            }

            if (inAgent != null) {
                try {
                    inAgent.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // close socket
            try{socket.close();
            }catch(IOException e){
                e.printStackTrace();
                System.out.println("Failed to close socket");
            }                }


    }


    /**
     * Creates a TCP connection to the VSMS
     * @param ip is the IP address of the VSMS
     * @param port is the portnumber of the VSMS
     * @throws IOException
     */
    public void createUpdatSocket(String ip, int port) throws IOException
    {
        socket = new Socket(ip,port);
        outAgent =
                new PrintWriter(socket.getOutputStream(), true);
        inAgent =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
    }

}

