package extras;

import edu.wpi.rail.jrosbridge.messages.geometry.Quaternion;

import static java.lang.Math.*;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan and Verhoeven Peter.
 *
 * Conversion functions from Gazebo API source code
 **/
// Conversion functions from Gazebo API source code
public class Quat
{
    /**
     *
     * @param roll
     * @param pitch
     * @param yaw
     * @return
     */
    public static Quaternion toQuaternion(double roll, double pitch, double yaw)
    {
        double phi = roll / 2.0;
        double the = pitch / 2.0;
        double psi = yaw / 2.0;

        double w = cos(phi) * cos(the) * cos(psi) + sin(phi) * sin(the) * sin(psi);
        double x = sin(phi) * cos(the) * cos(psi) - cos(phi) * sin(the) * sin(psi);
        double y = cos(phi) * sin(the) * cos(psi) + sin(phi) * cos(the) * sin(psi);
        double z = cos(phi) * cos(the) * sin(psi) - sin(phi) * sin(the) * cos(psi);

        double[] norm = Normalize(w,x,y,z);

        return new Quaternion(norm[1],norm[2],norm[3],norm[0]);
    }

    /**
     *
     * @param q
     * @return
     */
    public static double[] toEulerianAngle(Quaternion q)
    {
        double[] norm = Normalize(q.getW(),q.getX(),q.getY(),q.getZ());
        double w = norm[0];
        double x = norm[1];
        double y = norm[2];
        double z = norm[3];
        double squ = w * w;
        double sqx = x * x;
        double sqy = y * y;
        double sqz = z * z;

        // Roll (x-axis rotation)
        double roll = atan2(2 * (y*z + w*x), squ - sqx - sqy + sqz);

        // Pitch (y-axis rotation)
        double sarg = -2 * (x*z - w * y);
        double pitch = sarg <= -1.0 ? -0.5*Math.PI : (sarg >= 1.0 ? 0.5*Math.PI : asin(sarg));

        // Yaw (z-axis rotation)
        double yaw = atan2(2 * (x*y + w*z), squ + sqx - sqy - sqz);

        return new double[]{roll, pitch, yaw};
    }

    /**
     *     return a new Quaternion whose value is (this * b)
      */
    public static Quaternion times(Quaternion a, Quaternion b) {
        double y0 = a.getX()*b.getX() - a.getY()*b.getY() - a.getZ()*b.getZ() - a.getW()*b.getW();
        double y1 = a.getX()*b.getY() + a.getY()*b.getX() + a.getZ()*b.getW() - a.getW()*b.getZ();
        double y2 = a.getX()*b.getZ() - a.getY()*b.getW() + a.getZ()*b.getX() + a.getW()*b.getY();
        double y3 = a.getX()*b.getW() + a.getY()*b.getZ() - a.getZ()*b.getY() + a.getW()*b.getX();
        return new Quaternion(y0, y1, y2, y3);
    }

    /**
     *
     * @param w
     * @param x
     * @param y
     * @param z
     * @return
     */
    private static double[] Normalize(double w, double x, double y, double z)
    {
        double s = sqrt(w * w + x * x + y * y + z * z);

        if (Equal(s, 0.0, 1e-6))
        {
            w = 1.0;
            x = 0.0;
            y = 0.0;
            z = 0.0;
        }
        else
        {
            w /= s;
            x /= s;
            y /= s;
            z /= s;
        }

        return new double[]{w,x,y,z};
    }

    private static boolean Equal(double a, double b, double eps)
    {
        return Math.abs(a-b) <= eps;
    }
}
