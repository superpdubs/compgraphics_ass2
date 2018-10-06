package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements MouseListener {

    private Terrain terrain;
    
    private Camera camera;
    private Point2D myMousePoint = null;
    private float rotateX = 0;
    private float rotateY = 0;
    private static final int ROTATION_SCALE = 1;
    private static final boolean USE_LIGHTING = true;

    private TriangleMesh model;
    private Texture texture;
    
    private boolean useCamera;    

    public World(Terrain terrain){
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
   
        this.camera = new Camera(this);
        useCamera = false;
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
        super.display(gl);
        Shader.setInt(gl, "tex", 0);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());

        // Set the lighting properties

        Shader.setPoint3D(gl, "lightDir", new Point3D(-1 , 0, 0));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));

        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.6f, 0.6f, 0.6f));
        Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
        Shader.setFloat(gl, "phongExp", 8f);
        
        CoordFrame3D frame;

        if (useCamera) {
            camera.setView(gl);
        }


//		Jie Implementation
//        Point3D cameraPos = camera.transformPoint(new Point3D(0, 0, -1));
//        if (cameraPos.getX() >= 0 && Math.round(cameraPos.getX()) < terrain.getWidth() - 1 && cameraPos.getZ() >= 0 && Math.round(cameraPos.getZ()) < terrain.getDepth() - 1) {
//            camera.setView(gl);
//        } else {
//            camera.setView(gl);
//        }
        
        // Translate terrain into visible position
        frame = CoordFrame3D.identity()
                .translate(-2, 0.0f, -20f)
                .rotateX(rotateX)
                .rotateY(rotateY)
                .scale(1.0f, 1.0f, 1.0f);
        
        Shader.setPenColor(gl, Color.GREEN);
        terrain.drawTerrain(gl, frame);
	}

    public Terrain getTerrain() {
        return terrain;
    }

    @Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		
		terrain.initTerrain(gl);
		texture = new Texture(gl, "res/textures/grass.bmp", "bmp", false);
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}
	
	// Mouse movement for debug purposes
    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D p = new Point2D(e.getX(), e.getY());

        if (myMousePoint != null) {
            float dx = p.getX() - myMousePoint.getX();
            float dy = p.getY() - myMousePoint.getY();

            // Note: dragging in the x dir rotates about y
            //       dragging in the y dir rotates about x
            rotateY += dx * ROTATION_SCALE;
            rotateX += dy * ROTATION_SCALE;

        }
        myMousePoint = p;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        myMousePoint = new Point2D(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseWheelMoved(MouseEvent e) { 
    	useCamera = false;
    }
}
