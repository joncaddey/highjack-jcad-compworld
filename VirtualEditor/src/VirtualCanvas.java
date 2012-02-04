import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.nio.IntBuffer;
import java.util.Observable;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.util.*;

public class VirtualCanvas extends Observable implements GLEventListener {
	private SceneGraphNode sceneGraphRoot;
	private boolean pickNextFrame;
	private Point pickedPoint;
	
	private double left, right, top, bottom;
	private int displayListID = -1;
	private final GLCanvas my_canvas;
	SceneGraphNode deleteThis;
	
	float my_oldX;
	float my_oldY;
	
	private SceneGraphNode my_selected;
	
	
	
	public VirtualCanvas() {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		my_canvas = new GLCanvas(capabilities);
		my_canvas.addGLEventListener(this);
		my_canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				my_oldX = my_selected.translateX;
				my_oldY = my_selected.translateY;
				pickNextFrame = true;
				pickedPoint = new Point(e.getX(), e.getY());
				setChanged();
				notifyObservers(my_selected);
			}
			
			public void mouseReleased(MouseEvent e) {
				System.out.println("Dragging");
				my_selected.translateX = my_oldX + (float) ((right - left) / my_canvas.getWidth() *  (e.getX() - pickedPoint.x));
				my_selected.translateY = my_oldY + (float) ((bottom - top) / my_canvas.getHeight() *  (e.getY() - pickedPoint.y));
				refresh();
			}
		});
		sceneGraphRoot = new SceneGraphNode();
		deleteThis = new Triangle();
		sceneGraphRoot.addChild(deleteThis);
		my_selected = deleteThis;
	}

	
	private Point2D.Float pixelToCoor(int x, int y) {
		Point2D.Float point = new Point2D.Float();
		point.x = (float) ((right - left) * x / my_canvas.getWidth() + left);
		point.y = (float) ((top - bottom) * y / my_canvas.getHeight() + top);
		return point;
	}

	public Component getCanvas() {
		return my_canvas;
	}
	
	public SceneGraphNode getSelected() {
		return my_selected;
	}
	
	public void refresh() {
		displayListID = -1;
		//my_canvas.repaint();
		
	}
	
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		if (pickNextFrame) {
			GLU glu = GLU.createGLU(gl);
			int viewport[] = new int[4];
			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			glu.gluPickMatrix(pickedPoint.x, (double)(viewport[3] - pickedPoint.y), 1, 1, viewport, 0);
			gl.glOrtho(left, right, bottom, top, -1, 1);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			System.out.println(sceneGraphRoot.getPicked(drawable));
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			pickNextFrame = false;
		}
		if (displayListID == -1) {
			displayListID = 1;
			gl.glNewList(displayListID, GL2.GL_COMPILE_AND_EXECUTE);
			sceneGraphRoot.render(drawable);
			gl.glEndList();
		} else
			gl.glCallList(displayListID);

	}

	public void dispose(GLAutoDrawable drawable) {
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0, 0, 0, 0);
		IntBuffer selectBuffer = Buffers.newDirectIntBuffer(3);
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);
		FPSAnimator fps = new FPSAnimator(drawable, 30);
		fps.start();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final double UNIT = 2;
		
		if (width < height) {
			left = -UNIT / 2;
			right = UNIT / 2;
			top = (double)height / width * UNIT / 2;
			bottom = -top;
		} else {
			top = UNIT / 2;
			bottom = -UNIT / 2;
			right = (double)width / height * UNIT / 2;
			left = -right;
		}
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}
}

