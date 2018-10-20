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
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;
    private Texture textureRoad;
    private TriangleMesh meshes;

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
        float diff = ((x - lowerX) * (lowerZ - upperZ)) - ((z - upperZ)* (upperX - lowerX));
        if (upperX - lowerX == 0 && upperZ - lowerZ == 0) return altitudes[(int) lowerX][(int) upperZ];
        if (upperX - lowerX == 0) {
            return ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ) * altitudes[(int) lowerX][(int) lowerZ]);
        }
        if (upperZ - lowerZ == 0) {
            return (((x - lowerX)/(upperX - lowerX)) * altitudes[(int) upperX][(int) lowerZ])  + (((upperX - x)/(upperX - lowerX)) * altitudes[(int) lowerX][(int) lowerZ]);
        }
        if (diff >= 0) {
            float polatedlowerX =  ((z - lowerZ)/(upperZ - lowerZ)) * lowerX + ((upperZ - z)/(upperZ - lowerZ)) * lowerX;
            float polatedupperX = ((z - lowerZ)/(upperZ - lowerZ)) * lowerX + ((upperZ - z)/(upperZ - lowerZ)) * upperX;

            float polatedLowerY = ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) lowerZ];
            float polatedUpperY = ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ)) * altitudes[(int) upperX][(int) lowerZ];

            altitudeZ = (((x - polatedlowerX)/(polatedupperX - polatedlowerX)) * polatedUpperY)  + (((polatedupperX - x)/(polatedupperX - polatedlowerX)) * polatedLowerY);
        } else {
            float polatedupperX =  ((z - lowerZ)/(upperZ - lowerZ)) * upperX + ((upperZ - z)/(upperZ - lowerZ)) * upperX;
            float polatedlowerX = ((z - lowerZ)/(upperZ - lowerZ)) * lowerX + ((upperZ - z)/(upperZ - lowerZ)) * upperX;

            float polatedUpperY = ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) upperX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ)) * altitudes[(int) upperX][(int) lowerZ];
            float polatedLowerY = ((z - lowerZ)/(upperZ - lowerZ)) * altitudes[(int) lowerX][(int) upperZ] + ((upperZ - z)/(upperZ - lowerZ)) * altitudes[(int) upperX][(int) lowerZ];

            altitudeZ = (((x - polatedlowerX)/(polatedupperX - polatedlowerX)) * polatedUpperY)  + (((polatedupperX - x)/(polatedupperX - polatedlowerX)) * polatedLowerY);
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
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);


        float altitude = this.altitude(road.point(0).getX(), road.point(0).getY());
        road.setAltitude(altitude);
        road.makeRoadMesh();

        System.out.println("sgfafas" + altitude);
        roads.add(road);
    }

    public void makeTerrainMesh() {
        ArrayList<Point3D> points = new ArrayList<Point3D>();
        ArrayList<Point2D> texCoord = new ArrayList<Point2D>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < width; j++) {

                points.add(new Point3D(i, altitudes[i][j], j));
                texCoord.add(new Point2D(i, j));

                if (i == depth - 1 || j == width - 1) continue;

                indices.add(i * width + j);
                indices.add(i * width + 1 + j);
                indices.add(j + ((i + 1) * width));

                indices.add(j + ((i + 1) * width));
                indices.add(i * width + 1 + j);
                indices.add((j + 1) + (i + 1) * width);

            }
        }
        int counter = 0;
        for (Integer i: indices) {
            counter++;
        }
        meshes = new TriangleMesh(points, indices, true, texCoord);
    }
    
    public void initTerrain(GL3 gl) {
        makeTerrainMesh();
        meshes.init(gl);


        this.textureRoad = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
        for (Tree t: trees) {
            t.getTreeModel().init(gl);
        }


        for (Road r: roads) {
            r.getRoadMesh().init(gl);
        }

    }

    public void drawTerrain(GL3 gl, CoordFrame3D frame) {
        Shader.setPenColor(gl, Color.GREEN);
        this.meshes.draw(gl, frame);
        for (Tree t: trees) {
            Shader.setPenColor(gl, Color.RED);
            t.getTreeModel().draw(gl, frame.translate(t.getPosition().getX(), t.getPosition().getY(), t.getPosition().getZ()).scale(0.2f, 0.2f, 0.2f));

        }

        Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureRoad.getId());

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
