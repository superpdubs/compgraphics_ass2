package unsw.graphics.world;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * The Terrain for producing roads, trees, sunlight vector and calculating the altitude at a specific point
 *
 */
public class Terrain {

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;
    private Texture textureRoad;
    private TriangleMesh terrainMesh;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;

    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }

    public Vector3 getSunlight() {
        return sunlight;
    }

    public void destroy(GL3 gl) {
        for (Tree t: trees) {
            t.getTreeModel().destroy(gl);
        }

        for (Road r: roads) {
            r.getRoadMesh().destroy(gl);
        }
        terrainMesh.destroy(gl);
        textureRoad.destroy(gl);
    }
    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
    	if (x < 0 || x > (width - 1) || z < 0 || z > (depth - 1)) {
    		return 0;
    	}

        float altitudeZ = 0;
        float lowerX = (float) Math.floor(x);
        float upperX = (float) Math.ceil(x);
        float lowerZ= (float) Math.floor(z);
        float upperZ = (float) Math.ceil(z);

        //Using (x−x1)(y2−y1)−(y−y1)(x2−x1) to calculate if a certain point lies to the left or right of a line
        //Using that formula, I calculated a line from bottom left to top-right of a grid square, and checked which
        //Point that lied on.
        float diff = ((x - lowerX) * (lowerZ - upperZ)) - ((z - upperZ)* (upperX - lowerX));

        //Case when its perfectly on a grid point
        if (upperX - lowerX == 0 && upperZ - lowerZ == 0) return altitudes[(int) lowerX][(int) upperZ];

        //Case where we are on perfectly on X line
        if (upperX - lowerX == 0) {
            return ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ) * altitudes[(int) lowerX][(int) lowerZ]);
        }

        //Case where we are perfectly on Z line
        if (upperZ - lowerZ == 0) {
            return (((x - lowerX)/(upperX - lowerX)) * altitudes[(int) upperX][(int) lowerZ])  + (((upperX - x)/(upperX - lowerX)) * altitudes[(int) lowerX][(int) lowerZ]);
        }

        //Here this calculates the top-left triangle of a grid.
        if (diff >= 0) {
            //Here we want to calculate the values of the top-left triangle
            //LowerX/LowerY calculates with the line of top-left point and bottom-left point
            //UpperX/UpperY calculates with the line of bottom-left point to top-right point
            //PolatedLowerX calculates with polated X
            float polatedLowerX =  ((z - lowerZ)/(upperZ - lowerZ)) * lowerX + ((upperZ - z)/(upperZ - lowerZ)) * lowerX;
            //PolatedUpperX calculates with polated X
            float polatedUpperx = ((z - lowerZ)/(upperZ - lowerZ)) * lowerX + ((upperZ - z)/(upperZ - lowerZ)) * upperX;

            //Now we calculate the y value of the triangle
            float polatedLowerY = ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) lowerZ];
            float polatedUpperY = ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ)) * altitudes[(int) upperX][(int) lowerZ];

            //Now using the calculated polatedX and polatedY we calculate the to-be altitude
            altitudeZ = (((x - polatedLowerX)/(polatedUpperx - polatedLowerX)) * polatedUpperY)  + (((polatedUpperx - x)/(polatedUpperx - polatedLowerX)) * polatedLowerY);
        //Here this calculate the bottom-right triangle of a grid
        } else {
            //Here we want to calculate the values of the bottom-right triangle
            //LowerX/LowerY calculates with the line of bottom-left point and top-right point
            //UpperX/UpperY calculates with the line of bottom-left point to bottom-right point
            //Calculate the polated X values
            float polatedUpperX =  ((z - lowerZ)/(upperZ - lowerZ)) * upperX + ((upperZ - z)/(upperZ - lowerZ)) * upperX;
            float polatedLowerX = ((z - lowerZ)/(upperZ - lowerZ)) * lowerX + ((upperZ - z)/(upperZ - lowerZ)) * upperX;

            //Calculate the polated Z values
            float polatedUpperY = ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) upperX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ)) * altitudes[(int) upperX][(int) lowerZ];
            float polatedLowerY = ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ)) * altitudes[(int) upperX][(int) lowerZ];

            //Now using the calculated polatedX and polatedY we calculate the to-be altitude
            altitudeZ = (((x - polatedLowerX)/(polatedUpperX - polatedLowerX)) * polatedUpperY)  + (((polatedUpperX - x)/(polatedUpperX - polatedLowerX)) * polatedLowerY);
        }

        return altitudeZ;
        
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     *
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);


        float altitude = this.altitude(road.point(0).getX(), road.point(0).getY());
        road.setAltitude(altitude);
        road.makeRoadMesh();

        roads.add(road);
    }

    /**
     * Makes a Triangle Mesh to draw the terrain.
     * This uses the given points list, and forms a list of triangles, with corresponding indices and texCoord
     */
    public void makeTerrainMesh() {
        ArrayList<Point3D> points = new ArrayList<Point3D>();
        ArrayList<Point2D> texCoord = new ArrayList<Point2D>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < width; j++) {
                //Add towards points list the corresponding altitude and texCoord
                points.add(new Point3D(i, altitudes[i][j], j));
                texCoord.add(new Point2D(i, j));

                //Since we are trying to calculate triangle indices we want to stop at last row/col
                if (i == depth - 1 || j == width - 1) continue;

                //Add top-left triangle
                indices.add(i * width + j);
                indices.add(i * width + 1 + j);
                indices.add(j + ((i + 1) * width));

                //Add top-right triangle
                indices.add(j + ((i + 1) * width));
                indices.add(i * width + 1 + j);
                indices.add((j + 1) + (i + 1) * width);

            }
        }
        terrainMesh = new TriangleMesh(points, indices, true, texCoord);
    }
    
    public void initTerrain(GL3 gl) {
        makeTerrainMesh();
        terrainMesh.init(gl);

        this.textureRoad = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
        for (Tree t: trees) {
            t.getTreeModel().init(gl);
        }

        for (Road r: roads) {
            r.getRoadMesh().init(gl);
        }

    }

    /**
     * Draw terrain, used in display of World
     * @param frame draw on current frame
     */
    public void drawTerrain(GL3 gl, CoordFrame3D frame) {
        //Draw green terrain
        Shader.setPenColor(gl, Color.GREEN);
        this.terrainMesh.draw(gl, frame);

        //Translate the trees with their respective positions, and downscale.
        for (Tree t: trees) {
            Shader.setPenColor(gl, Color.RED);
            t.getTreeModel().draw(gl, frame.translate(t.getPosition().getX(), t.getPosition().getY(), t.getPosition().getZ()).scale(0.2f, 0.2f, 0.2f));
        }

        //Bind texture for Road
        Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureRoad.getId());

        //Use a polygon offset to allow Z-fighting to stop for road (Road wins!!1)
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(-1.0f, -1.0f);
        Shader.setPenColor(gl, Color.WHITE);
        for (Road r: roads) {
            r.getRoadMesh().draw(gl, frame);
        }
        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }
    
}
