package unsw.graphics.world;

import com.jogamp.opengl.GL3;

import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * Make a tree object which contains the tree mesh and position.
 *
 * @author malcolmr
 */
public class Tree {

    private Point3D position;
    private TriangleMesh treeModel;
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y+1, z+0.1f);
        try {
            treeModel = new TriangleMesh("res/models/tree.ply", true, true);
        } catch (Exception e) {
            System.out.println("Exception occured at tree");
        }
    }

    public TriangleMesh getTreeModel() {
        return treeModel;
    }

    public Point3D getPosition() {
        return position;
    }

    public void destroy(GL3 gl) {
    	this.treeModel.destroy(gl);
    }

}
