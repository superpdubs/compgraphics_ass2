package unsw.graphics.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Character {
    private Point3D myPos;
    private float myAngle;
    private float myScale;
    
    private int animSpeed = 2;
    private int fps;
    private int curAnim;
    private boolean isWalking;
    private int animCooldown;
    
    private TriangleMesh body;
    private ArrayList<TriangleMesh> walk;
    private Texture texture;
	private boolean moveBackwards;
    
    public Character(Point3D origin, float angle, float scale) {
    	this.myPos = origin;
    	this.myAngle = angle;
    	this.myScale = scale;
    	this.fps = 0;
    	this.curAnim = 0;
    	this.animCooldown = 0;
    	this.isWalking = false;
    	this.moveBackwards = false;
    	
    	try {
    		body = new TriangleMesh("res/models/MadBunny/madbunny_walk00.ply");
    	} catch (Exception e) {
            System.out.println("Exception occured loading character model");
        }
    	
    	this.walk = new ArrayList<TriangleMesh>();
    	
        for (int i = 0; i < 33; i++) {
        	String frameNum = "";
        	if (i < 10) {
        		frameNum = "0" + i;
        	} else {
        		frameNum = "" + i;
        	}
	        try {
            	String modelPath = "res/models/MadBunny/madbunny_walk" + frameNum + ".ply";
	            walk.add(i, new TriangleMesh(modelPath, true, true));
	        } catch (Exception e) {
	        	System.out.println(walk.size());
	            System.out.println("Exception occured loading character model");
	        }
	    }
    }
    
    public void init(GL3 gl) {
    	for (int i = 0; i < walk.size(); i++) {
    		walk.get(i).init(gl);
    	}
    	
    	body.init(gl);
    	texture = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", false);
    }
    
    public void destroy(GL3 gl) {
    	for (int i = 0; i < walk.size(); i++) {
    		walk.get(i).destroy(gl);
    	}
    	
        body.destroy(gl);
    }
    
    public void drawCharacter(GL3 gl) {
        Shader.setPenColor(gl, Color.WHITE);
        
    	CoordFrame3D frame = CoordFrame3D.identity();
    	System.out.println(myAngle);
    	//body.draw(gl, frame.translate(myPos).rotateY(myAngle).scale(myScale, myScale, myScale));
    	
    	if (isWalking) {
    		if (moveBackwards) {
    			walk.get(curAnim).draw(gl, frame.translate(myPos).rotateY(myAngle + 180).scale(myScale, myScale, myScale));
    		} else {
    			walk.get(curAnim).draw(gl, frame.translate(myPos).rotateY(myAngle).scale(myScale, myScale, myScale));
    		}
	    	
	    	fps ++;
	    	if (fps >= animSpeed) {
	    		fps = 0;
	    		curAnim ++;
	    		animCooldown --;
	    	}
	    	if (curAnim >= walk.size()) {
	    		curAnim = 0;
	    		if (animCooldown <= 0) isWalking = false;
	    	} else if (curAnim == 18) {
	    		if (animCooldown <= 0) isWalking = false;
	    	}
    	} else {
    		if (moveBackwards) {
    			walk.get(0).draw(gl, frame.translate(myPos).rotateY(myAngle + 180).scale(myScale, myScale, myScale));
    		} else {
    			walk.get(0).draw(gl, frame.translate(myPos).rotateY(myAngle).scale(myScale, myScale, myScale));
    		}
    		fps = 0;
    		
    		Random rand = new Random();
    		if (rand.nextInt(2) == 0) {
    			curAnim = 0;
    		} else {
    			curAnim = 18;
    		}
    	}
    }
    
    public void setPosition(Point3D destination) {
    	myPos = destination;
    }
    
    public void setRotation(float rotation) {
    	myAngle = rotation;
    }
    
    public void addRotation(float rotation) {
    	myAngle += rotation;
    	if (myAngle > 180) {
    		myAngle -= 360;
    	} else if (myAngle <= -180) {
    		myAngle += 360;
    	}
    }
    
    public void isMoving(int forward) {
    	if (forward < 0) {
    		this.moveBackwards = true;
    	} else if (forward > 0){
    		this.moveBackwards = false;
    	}
    	this.isWalking = true;
    	animCooldown = 8;
    }
}
