package unsw.graphics.world;

import java.awt.Color;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;

public class Light implements KeyListener{

	private Shader activeShader;
	private int lightIndex;
	private GL3 targetGL;
	private boolean toggleLight;
	private boolean torch;
	private Point3D lightPosition;
	private Point3D origLightPosition;
	private Point3D cameraPosition;
	private Point3D avatarPosition;
	
	/**
	 * Light Constructor
	 * 
	 * @param gl
	 * @param lightIndex - 1 sunlight & null torch, 2 night & torch
	 */
	public Light(GL3 gl, int lightIndex, Point3D lightPosition, Point3D cameraPosition, Point3D avatarPosition) {
		this.lightIndex = lightIndex;
		this.targetGL = gl;
		this.lightPosition = lightPosition;
		this.cameraPosition = cameraPosition;
		this.avatarPosition = avatarPosition;
		this.origLightPosition = lightPosition;
		

		activeShader = new Shader(this.targetGL, "shaders/vertex_tex_phong.glsl", "shaders/fragment_combo.glsl");
		activeShader.use(targetGL);

		switch(lightIndex) {
			case(1):
				this.setSunlight();
	        	this.torchOff();
				this.toggleLight = true;
				this.torch = false;
				break;
			case(2):
				this.setNightlight();
	        	this.torchOn();
				this.toggleLight = false;
				this.torch = true;
				break;
			default:
				break;
		}
		
	}
	
	public boolean getToggle() {
		return toggleLight;
	}
	
	public boolean getTorch() {
		return torch;
	}
	
	public void setSunlight() {    
      //Implement Lighting/Nightlight
        Shader.setPoint3D(this.targetGL, "envLightPos", this.lightPosition);
        Shader.setColor(this.targetGL, "envLightIntensity", Color.WHITE);
        Shader.setColor(this.targetGL, "envAmbientIntensity", new Color(0.3f, 0.3f, 0.3f));
		
        // Set the material properties
        Shader.setColor(this.targetGL, "envAmbientCoeff", Color.WHITE);
        Shader.setColor(this.targetGL, "envDiffuseCoeff", new Color(0.6f, 0.6f, 0.6f));
        Shader.setColor(this.targetGL, "envSpecularCoeff", new Color(0.6f, 0.6f, 0.6f));
        Shader.setFloat(this.targetGL, "envPhongExp", 8f);        
	}
	
	public void setNightlight() {
        //Implement Lighting/Nightlight
        Shader.setPoint3D(this.targetGL, "envLightPos", this.lightPosition);
        Shader.setColor(this.targetGL, "envLightIntensity", Color.WHITE);
        Shader.setColor(this.targetGL, "envAmbientIntensity", new Color(0.1f, 0.1f, 0.1f));
		
        // Set the material properties
        Shader.setColor(this.targetGL, "envAmbientCoeff", new Color(0,0,1f));
        Shader.setColor(this.targetGL, "envDiffuseCoeff", new Color(0.3f, 0.3f, 0.3f));
        Shader.setColor(this.targetGL, "envSpecularCoeff", new Color(0.1f, 0.1f, 0.1f));
        Shader.setFloat(this.targetGL, "envPhongExp", 8f);
	}
	
	public void torchOn() {
		// test torch light
		Shader.setPoint3D(this.targetGL, "torchLightPos", this.cameraPosition.translate(0, 0, 0));
        Shader.setColor(this.targetGL, "torchLightIntensity", Color.WHITE);
        Shader.setColor(this.targetGL, "torchAmbientIntensity", new Color(0.7f, 0.7f, 0.7f));
		Shader.setFloat(this.targetGL, "k", 0.5f);
        // Set the material properties
        Shader.setColor(this.targetGL, "torchAmbientCoeff", new Color(1f,1f,1f));
        Shader.setColor(this.targetGL, "torchDiffuseCoeff", new Color(0.6f, 0.6f, 0.6f));
	}
	
	public void torchOff() {
		// test torch light
		Shader.setPoint3D(this.targetGL, "torchLightPos", this.cameraPosition);
        Shader.setColor(this.targetGL, "torchLightIntensity", Color.WHITE);
        Shader.setColor(this.targetGL, "torchAmbientIntensity", new Color(0.0f, 0.0f, 0.0f));
		Shader.setFloat(this.targetGL, "k", 1f);
        // Set the material properties
        Shader.setColor(this.targetGL, "torchAmbientCoeff", new Color(0,0,0));
        Shader.setColor(this.targetGL, "torchDiffuseCoeff", new Color(0.0f, 0.0f, 0.0f));
	}
	
	public void setCameraPosition(Point3D position) {
		this.cameraPosition = position;
	}
	
	public Point3D getCameraPosition() {
		return this.cameraPosition;
	}
	
	public void setAvatarPosition(Point3D position) {
		this.avatarPosition = position;
	}
	
	public Point3D getAvatarPosition() {
		return this.avatarPosition;
	}
	
	@Override
    public void keyPressed(KeyEvent e) {
   	
        switch(e.getKeyCode()) {
        //Night and Day toggle
        case KeyEvent.VK_OPEN_BRACKET:
        	toggleLight = !toggleLight;
            break;
            
        // Toggle Torch
        case KeyEvent.VK_CLOSE_BRACKET:
        	torch = !torch;
            break;
        
        
	    case KeyEvent.VK_BACK_SLASH:
	    	torch = !torch;
	        break;
	        
	    default:
	    	break;
	    }

    }

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

