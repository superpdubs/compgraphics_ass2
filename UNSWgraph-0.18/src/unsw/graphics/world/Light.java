package unsw.graphics.world;

import java.awt.Color;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;

public class Light implements KeyListener{

	private Shader activeShader;
	private int lightIndex;
	private Point3D lightPosition;
	private GL3 targetGL;
	private boolean toggleLight;
	private Point3D cameraPosition;
	
	/**
	 * Light Constructor
	 * 
	 * @param gl
	 * @param lightIndex - 1 sunlight, 2 night, 3 torch and point light
	 */
	public Light(GL3 gl, int lightIndex, Point3D lightPosition, Point3D cameraPosition) {
		this.lightIndex = lightIndex;
		this.targetGL = gl;
		this.lightPosition = lightPosition;
		this.cameraPosition = cameraPosition;
		
		switch(lightIndex) {
			// sunlight
			case(1):
				activeShader = new Shader(this.targetGL, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong_mod.glsl");
				activeShader.use(targetGL);
				this.setSunlight();
				this.toggleLight = true;
				break;
			case(2):
//				activeShader = new Shader(this.targetGL, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong_mod.glsl");
				activeShader = new Shader(this.targetGL, "shaders/vertex_tex_phong.glsl", "shaders/fragment_testSpotlight.glsl");
				activeShader.use(targetGL);
				this.setNightlight();
				this.toggleLight = false;
				break;
			case(3):
			default:
				break;
		}
		
	}
	
	public boolean getToggle() {
		return toggleLight;
	}
	
	public void setSunlight() {
        //Implement Lighting/Sunlight
        Shader.setPoint3D(this.targetGL, "lightPos", this.lightPosition);
        Shader.setColor(this.targetGL, "lightIntensity", Color.WHITE);
        Shader.setColor(this.targetGL, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
        
        // Set the material properties
        Shader.setColor(this.targetGL, "ambientCoeff", Color.WHITE);
        Shader.setColor(this.targetGL, "diffuseCoeff", new Color(0.6f, 0.6f, 0.6f));
        Shader.setColor(this.targetGL, "specularCoeff", new Color(0.6f, 0.6f, 0.6f));
        Shader.setFloat(this.targetGL, "phongExp", 12f);
	}
	
	public void setNightlight() {
        //Implement Lighting/Nightlight
//        Shader.setPoint3D(this.targetGL, "lightPos", new Point3D(3,3,0));
		
		// test torch light
		Shader.setPoint3D(this.targetGL, "lightPos", this.cameraPosition);
        Shader.setColor(this.targetGL, "lightIntensity", Color.WHITE);
        Shader.setColor(this.targetGL, "ambientIntensity", new Color(0.1f, 0.1f, 0.1f));
        
        // Set the material properties
        Shader.setColor(this.targetGL, "ambientCoeff", new Color(0,0,200));
        Shader.setColor(this.targetGL, "diffuseCoeff", new Color(0.6f, 0.6f, 0.6f));
        Shader.setColor(this.targetGL, "specularCoeff", new Color(0.1f, 0.1f, 0.1f));
        Shader.setFloat(this.targetGL, "phongExp", 32f);
	}
	
	public void setCameraPosition(Point3D position) {
		this.cameraPosition = position;
	}
	
	public Point3D getCameraPosition() {
		return this.cameraPosition;
	}
	
	@Override
    public void keyPressed(KeyEvent e) {
   	
        switch(e.getKeyCode()) {
        //Night and Day toggle
        case KeyEvent.VK_OPEN_BRACKET:
        	if (toggleLight == false) {
        		toggleLight = true;
        	} else if (toggleLight == true){
            	toggleLight = false;
            }
            break;
            
        // Toggle Torch
        case KeyEvent.VK_CLOSE_BRACKET:
        	System.out.println("close bracket");
            break;

        }

    }

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
