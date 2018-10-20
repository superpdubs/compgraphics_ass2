package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.world.Rain;

/**
 * COMP3421 Computer Graphics - UNSW
 * 
 * Assignment 2
 * 
 * Jie Chen 		- z5080307
 * Peter Wang 		- z5063199
 * Charlton Wong 	- z5112642
 * 
 * Implemented the following Extensions:
 * 
 * - Build a complex model or a model with walking animation or something beautiful or interesting for your avatar! (2..4 marks) 
 * 		- Implemented in unsw.graphics.world/Character.java
 * 		- Called via unsw.graphics.world/Camera.java
 * 		- Model and textures included in Models and Textures folders. 
 * - Make the sun move and change colour according to the time of day (2 marks) 
 * 		- Implemented in unsw.graphics.world/Sun.java
 * - Add rain using particle effects (4 marks)
 * 		- Implemented in unsw.graphics.world/Rain.java
 * 
 * Hotkey Bindings:
 * 	- 'WASD' = Movement
 *  - 'Arrow Keys' = Movement
 *  - 'F' = Fun Character Animation
 *  - 'P' = Toggle Torch
 *  - '[' = Toggle Day / Night
 *  - ']' = Toggle DayNight Cycle (Sun colour / movement)
 *  - '\' = Toggle Rain
 *	- 'SPACE' = Toggle First/Third Person Camera View.
 */
public class World extends Application3D {

    private Terrain terrain;
    private Rain RainSystem;
    private Camera camera;
    private static final boolean USE_LIGHTING = true;

    private Texture texture;
    private Light modelLight;
    private Sun sun;

    public World(Terrain terrain){
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        this.RainSystem = new Rain(terrain.getWidth(), terrain.getDepth());
        this.camera = new Camera(this);
        this.sun = new Sun(terrain.getWidth(), 2000);
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
        
        CoordFrame3D frame = CoordFrame3D.identity();

        RainSystem.display(gl, camera.getViewFrame());
        modelLight.setCameraPosition(this.camera.getPosition());
        
        // Check Sun Object to see if animation is on.
    	if (sun.getToggle()) {
    		sun.setLight(modelLight);
    	} else {
        	// Monitor Day/Night toggle
	        if (modelLight.getToggle()) {
	            modelLight.setSunlightShader();
	        } else {
	            modelLight.setNightLightShader();
	        }
    	}
    	
    	// Monitor Torch toggle
        if (modelLight.getTorch()) {
        	modelLight.torchOn();
        } else {
        	modelLight.torchOff();
        }

        camera.setView(gl);

        Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());

        Shader.setPenColor(gl, Color.GREEN);

        terrain.drawTerrain(gl, frame);

    }

    public Terrain getTerrain() {
        return terrain;
    }

    @Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		modelLight.destroy(gl);
		terrain.destroy(gl);
		camera.destroy(gl);
		//TO DO: destroy on all children
	}

	@Override
	public void init(GL3 gl) {
	
		super.init(gl);
        terrain.initTerrain(gl);
        texture = new Texture(gl, "res/textures/grass.bmp", "bmp", false);
        // Setup the particle shader
        getWindow().addKeyListener(camera);
    	getWindow().addKeyListener(modelLight);
    	getWindow().addKeyListener(sun);

        getWindow().addKeyListener(this.RainSystem);

        if (USE_LIGHTING && this.terrain.getSunlight() != null) {
            modelLight = new Light(gl, 2 , this.terrain.getSunlight().asPoint3D(), this.camera.getPosition());
        	modelLight.setCameraPosition(this.camera.getPosition());
        	getWindow().addKeyListener(modelLight);
        }

        camera.init(gl);

        RainSystem.init(gl);
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 0.05f, 100));
	}
}
