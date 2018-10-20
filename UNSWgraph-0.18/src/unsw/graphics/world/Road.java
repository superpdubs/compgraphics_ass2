package unsw.graphics.world;

import java.util.ArrayList;
import java.util.List;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * Class Road using the extrusion of the specific set of splines, and produces a triangle mesh to be drawn
 * with.
 */
public class Road {

    private List<Point2D> points;
    private float width;
    private float altitude;
    private TriangleMesh roadMesh;
    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(float width, List<Point2D> spine) {
        this.width = width;
        this.points = spine;
        this.altitude = 0;


    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    /**
     * Makes the road's mesh based on extruding the road to the left and right of the given bezier line.
     */
    public void makeRoadMesh() {
        ArrayList<Point3D> points = new ArrayList<Point3D>();
        ArrayList<Point2D> texCoord = new ArrayList<Point2D>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        float segments = 32;


        float dt = 1.0f/segments;
        //Get each control segment, and calculate parts of each segment
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < segments; j++) {
                //Here we first divide each control segment into 32 smaller segments
                float t = j * dt;

                //Get current point for the specific small segment, then further find the next point, and previous point
                //The next point and previous point is used to calculate the tanget at current point.
                Point3D currPoint = new Point3D(point(t + i).getX(), (altitude), point(t + i).getY());
                Point3D nextPoint = null;
                if (t + dt >= 1)  nextPoint = new Point3D(point(t + i).getX(), (altitude), point(t + i).getY());
                if (t + dt < 1) nextPoint = new Point3D(point(t + dt + i).getX(), (altitude), point(t + dt + i).getY());
                Point3D prevPoint = null;
                if (t - dt < 0) prevPoint = new Point3D(point(0 + i).getX(), (altitude), point(0 + i).getY());
                if (t - dt >= 0) prevPoint = new Point3D(point(t - dt + i).getX(), (altitude), point(t - dt + i).getY());

                //Tangent and normal to Tangent towards the x/z position vectors
                Vector3 tangentVec = (nextPoint.minus(prevPoint).normalize());
                Vector3 normalVec = new Vector3(-tangentVec.getZ(), 0, tangentVec.getX());

                //Extrude the shape of the points by width/2 and -width/2
                Point3D left = currPoint.translate(normalVec.scale(width/2));
                Point3D right = currPoint.translate(normalVec.scale(-width/2));

                points.add(left);
                points.add(right);

                //Add tex coordinates
                texCoord.add(new Point2D(left.getX(), left.getZ()));
                texCoord.add(new Point2D(right.getX(), right.getZ()));
            }
        }

        //Add a triangles to relevant mesh, with correspondence to points list
        for (int i = 0; i < points.size(); i++) {
            if (i + 3 >= points.size()) continue;
            //Calculate the top-left triangle
            indices.add(i);
            indices.add(i + 1);
            indices.add(i + 2);

            //Calculate the bottm-right traingle
            indices.add(i + 2);
            indices.add(i + 1);
            indices.add(i + 3);
        }
        roadMesh = new TriangleMesh(points, indices, true, texCoord);
        return;
    }

    public TriangleMesh getRoadMesh() {
        return roadMesh;
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return width;
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return points.size() / 3;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public Point2D controlPoint(int i) {
        return points.get(i);
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public Point2D point(float t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        
        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);
        

        float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
        float y = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();        
        
        return new Point2D(x, y);
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private float b(int i, float t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }


}
