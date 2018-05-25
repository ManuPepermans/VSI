package raytrace;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan and Verhoeven Peter.
 **/
public class Segment {

    public double start[];
    public double end[];

    public double position[];
    public double direction[];

    /**
     *
     * @param start
     * @param end
     */
    public Segment(double[] start, double[] end){
        this.start = start;
        this.end = end;

        this.position = start;
        this.direction = new double[]{end[0]-start[0], end[1]-start[1]};
    }

}
