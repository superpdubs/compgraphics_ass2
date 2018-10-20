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
	
	private ArrayList<Integer> animLength;
	private boolean isSlashing;
    
    public Character(Point3D origin, float angle, float scale) {
    	this.myPos = origin;
    	this.myAngle = angle;
    	this.myScale = scale;
    	this.fps = 0;
    	this.curAnim = 0;
    	this.animCooldown = 0;
    	this.isWalking = false;
    	this.isSlashing = false;
    	this.moveBackwards = false;
    	
    	this.animLength = new ArrayList<Integer>();
    	animLength.add(0, 1);	//walk start
    	animLength.add(1, 11);	//walk mid
    	animLength.add(2, 12);	//walk mid
    	animLength.add(3, 22);	//walk end
    	animLength.add(4, 23);	//slash start
    	animLength.add(5, 29);	//slash end
    	
    	try {
    		body = new TriangleMesh("res/models/MadBunny_midRes/madbunny_walk00.ply");
    	} catch (Exception e) {
            System.out.println("Exception occured loading character model");
        }
    	
    	this.walk = new ArrayList<TriangleMesh>();
    	
        for (int i = 0; i <= animLength.get(3); i++) {
        	String frameNum = "";
        	if (i < 10) {
        		frameNum = "0" + i;
        	} else {
        		frameNum = "" + i;
        	}
	        try {
            	String modelPath = "res/models/MadBunny_midRes/madbunny_walk" + frameNum + ".ply";
	            walk.add(i, new TriangleMesh(modelPath, true, true));
	        } catch (Exception e) {
	        	System.out.println(walk.size());
	            System.out.println("Exception occured loading character model");
	        }
	    }
        
        for (int n = animLength.get(4); n <= animLength.get(5); n++) {
        	String frameNum = "";
        	String modelPath = "";
        	int i = n - animLength.get(4) + 1;
        	
        	if (i < 10) {
        		frameNum = "0" + i;
        	} else {
        		frameNum = "" + i;
        	}
	        try {
            	modelPath = "res/models/MadBunny_midRes/madbunny_slash" + frameNum + ".ply";
	            walk.add(n, new TriangleMesh(modelPath, true, true));
	        } catch (Exception e) {
	            System.out.println("Exception occured loading character model");
	        }
	    }
    }
    
    public void init(GL3 gl) {
    	for (int i = 0; i < walk.size(); i++) {
    		walk.get(i).init(gl);
    	}
    	
    	body.init(gl);
    	texture = new Texture(gl, "res/textures/fur_texture.bmp", "bmp", false);
    }
    
    public void destroy(GL3 gl) {
    	for (int i = 0; i < walk.size(); i++) {
    		walk.get(i).destroy(gl);
    	}
    	
        body.destroy(gl);
    }
    
    public void drawCharacter(GL3 gl) {
        Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        Shader.setPenColor(gl, Color.WHITE);
        
    	CoordFrame3D frame = CoordFrame3D.identity();
    	System.out.println(myAngle);
    	//body.draw(gl, frame.translate(myPos).rotateY(myAngle).scale(myScale, myScale, myScale));
    	
    	if (isWalking && !isSlashing) {    		
    		if (curAnim == 0) {
	    		Random rand = new Random();
	    		if (rand.nextInt(2) == 0) {
	    			curAnim = animLength.get(0);
	    		} else {
	    			curAnim = animLength.get(2);
	    		}
    		}
    		
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
	    	if (curAnim >= animLength.get(3)) {
	    		curAnim = animLength.get(0);
	    		if (animCooldown <= 0) {
	    			curAnim = 0;
	    			isWalking = false;
	    		}
	    	} else if (curAnim == animLength.get(2)) {
	    		if (animCooldown <= 0) {
	    			curAnim = 0;
	    			isWalking = false;
	    		}
	    	}
    	} 
    	
    	if (isSlashing) {
    		isWalking = false;
    		
    		if (moveBackwards) {
    			walk.get(curAnim).draw(gl, frame.translate(myPos).rotateY(myAngle + 180).scale(myScale, myScale, myScale));
    		} else {
    			walk.get(curAnim).draw(gl, frame.translate(myPos).rotateY(myAngle).scale(myScale, myScale, myScale));
    		}
	    	
	    	fps ++;
	    	if (fps >= animSpeed) {
	    		fps = 0;
	    		curAnim ++;
	    	}
	    	if (curAnim >= animLength.get(5)) {
	    		curAnim = 0;
	    		isSlashing = false;
	    	}
    	} 
    	if (!isWalking && !isSlashing){
    		if (moveBackwards) {
    			walk.get(0).draw(gl, frame.translate(myPos).rotateY(myAngle + 180).scale(myScale, myScale, myScale));
    		} else {
    			walk.get(0).draw(gl, frame.translate(myPos).rotateY(myAngle).scale(myScale, myScale, myScale));
    		}
    		fps = 0;
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
    	
    	if (!isSlashing) animSpeed = 3;
    }
    
    public void isSlashing() {
    	this.isSlashing = true;
    	curAnim = animLength.get(4);
    	animSpeed = 4;
    }
    
    public void resetAnimation() {
    	curAnim = 0;
    	fps = 0;
    	animSpeed = 2;
    	animCooldown = 0;
    	isWalking = false;
    	isSlashing = false;
    }
}
