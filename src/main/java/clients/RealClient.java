package clients;

//import com.google.gson.JsonObject;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import javax.json.JsonObject;


/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Pepermans Manu
 **/
public class RealClient extends Client{

    String agentName;

    public RealClient(String ip, int port, String robotName){
        super(ip, port);
        this.agentName = robotName;
        init();
        System.out.println("Client created");
    }

    /**
     * Get robots from client and update robots already tracked
     */
    public void updateOwnedRobots() {
        Topic echoBack = new Topic(ros, "/amcl_pose", "geometry_msgs/PoseWithCovarianceStamped",300);
        echoBack.subscribe(new MyPoseCallback(this));

        //

    }

    @Override
    public void lidarScan() {
        Topic laserScan = new Topic(ros, "/simrobot1/hokuyoScan", "sensor_msgs/LaserScan", 300);
        laserScan.subscribe(new MyLaserCallback(this));

    }

    @Override
    public void  agentPose() {
        Topic updateClock = new Topic(ros, "/clock", "rosgraph_msgs/Clock");
        updateClock.subscribe(new TopicCallback() {
            @Override
            public void handleMessage(Message message) {
                JsonObject jsonObject = message.toJsonObject();

                clientTime = jsonObject.getJsonObject("clock").toString();
            }
        });

    }

    //public void drawExternalRobots() {
      //  return;
    //}
}
