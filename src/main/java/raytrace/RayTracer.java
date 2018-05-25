package raytrace;

import SimServer.Agent;
import clients.RealClient;
import extras.Quat;
import javafx.geometry.Point3D;
import msgs.LaserScan;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static SimServer.Main.rtf;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan and Verhoeven Peter.
 *
 * The Raytracer raytraces a scene by creating and managing threads performing the raytracing
 *
 * Only works with real clients
 **/
public class RayTracer {

    //This value is supposed to be divided by -pi/4, but it generates an offset if you don't substract 0.1
    private final static double angleStartRad = Math.toRadians(-135); //-0.28;
    //private final static double angleEndRad = Math.PI-angleStartRad;
    //todo: variable for passing # rays
    private final static double angleDiffRad = Math.toRadians(270) / (300);

    /**
     * Start a new Raytrace
     * First it will get the position of the agent itself and the external agents.
     * Than it will create bounding boxes around the agents
     * Ratracing threads are made and raytracing starts
     *
     * @param client the client of the VSI
     * @param laserScan scan of the client
     * @param length number of beams
     * @return
     */
    public static float[] rayTrace(RealClient client, LaserScan laserScan, int length) throws FileNotFoundException, UnsupportedEncodingException {
        long time = 0;

        long startTime = 0;
        if(!rtf.equals("time"))
        {
            startTime = System.nanoTime();

        }

        float[] data = new float[length];
        //Allow for one core to be idle
        int cores = Runtime.getRuntime().availableProcessors()-1;
        //int cores = 7;
        ArrayList<RayTraceThread> rayTraceThreads = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<Hit> hits = new ArrayList<>();

        //Get the agent to be traced from
        Agent agent = client.ownedAgents.get(0);
        double position[] =  new double[]{agent.pose.getPosition().getX(), agent.pose.getPosition().getY(), agent.pose.getPosition().getZ()};
        float[] ranges = laserScan.getRanges();

        //get external robots
        List<Agent> externalAgents;
        synchronized (client.externalAgents)
        {
            externalAgents = client.externalAgents;
            double current = angleStartRad;
            double currentCarAngleRad = Quat.toEulerianAngle(agent.pose.getOrientation())[2];

            int i = 0;

            ArrayList<Segment[]> segments = new ArrayList<>();
            for(Agent r : externalAgents)
            {
                segments.add(r.getSegments());
            }

            //Generate Threads
            for(int m = 0; m<cores; m++)
            {
                rayTraceThreads.add(new RayTraceThread(new Point3D(position[0],position[1],position[2]), current, currentCarAngleRad+angleDiffRad*m, segments));
                threads.add(new Thread(rayTraceThreads.get(m)));
                threads.get(m).start();
            }

            //rayTrace
            while (i < length-1) // length = amount of rays (1080)
            {
                for(int m = 0; m<cores; m++)
                {
                    rayTraceThreads.set(m, new RayTraceThread(new Point3D(position[0],position[1],position[2]), current, currentCarAngleRad+angleDiffRad*m, segments));
                    threads.set(m, new Thread(rayTraceThreads.get(m)));
                    threads.get(m).start();
                }

                //Wait for threads
                try
                {
                    for(int j = 0; j< cores; j++){
                        threads.get(j).join();
                        hits.add(rayTraceThreads.get(j).hit);
                    }

                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                //Update hits
                for(int k = 0; k<cores; k++){
                    Hit hit = rayTraceThreads.get(k).hit;
                    if (hit != null)
                    {
                        if (hit.getTime() < ranges[i+k])
                            data[i+k] = (float) hit.getTime();
                    }
                    else
                    {
                        if(i+k+1 <= length)
                            data[i+k] = ranges[i+k];
                    }
                }

                current += angleDiffRad*cores;
                i += cores;
            }
        }

        //time stamp

        if(!rtf.equals("time"))
        {
            long endTime = System.nanoTime();
            time = endTime-startTime;
            try(FileWriter fw = new FileWriter(rtf+"_ray.xls", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.print(time+"\t");
                out.println(client.externalAgents.size());

            } catch (IOException e) {
                System.out.println("cannot create file");
                //exception handling left as an exercise for the reader
            }

        }

        //return modified array
        return data;

    }

    // Conversion Angles to X-th ray
    private static int mappingAngle(double angle)
    {
        angle += 3/4*Math.PI;
        angle = angle*180/Math.PI;
        return (int) Math.ceil(angle * 4);
    }
}


