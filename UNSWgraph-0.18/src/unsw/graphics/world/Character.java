package unsw.graphics.world;

import java.awt.Color;

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
    
    private TriangleMesh body;
    private Texture texture;
    
    public Character(Point3D origin, float angle, float scale) {
    	this.myPos = origin;
    	this.myAngle = angle;
    	this.myScale = scale;
        try {
            body = new TriangleMesh("res/models/bunny.ply", true, true);
        } catch (Exception e) {
            System.out.println("Exception occured at tree");
        }
    }
    
    public void init(GL3 gl) {
    	body.init(gl);
    	texture = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", false);
    }
    
    public void destroy(GL3 gl) {
        body.destroy(gl);
    }
    
    public void drawCharacter(GL3 gl) {
        Shader.setPenColor(gl, Color.WHITE);
        
    	CoordFrame3D frame = CoordFrame3D.identity();
    	System.out.println(myAngle);
    	body.draw(gl, frame.translate(myPos).rotateY(myAngle).scale(myScale, myScale, myScale));
    }
    
    public void setPosition(Point3D destination) {
    	myPos = destination;
    }
    
    public void setRotation(float rotation) {
    	myAngle = rotation;
    }
    
    public void addRotation(float rotation) {
    	myAngle += rotation;
    }
}
