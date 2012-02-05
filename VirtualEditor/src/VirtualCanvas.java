import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Observable;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;

public class VirtualCanvas extends Observable implements GLEventListener {
	private SceneGraphNode my_root;
	private boolean pickNextFrame;
	private Point pickedPoint;

	private double left, right, top, bottom;
	private int displayListID = -1;
	private final GLCanvas my_canvas;


	private SceneGraphNode my_selected;

	public VirtualCanvas() {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		my_canvas = new GLCanvas(capabilities);
		my_canvas.addGLEventListener(this);
		my_canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				pickNextFrame = true;
				pickedPoint = new Point(e.getX(), e.getY());
				
			}
		});
		my_root = new SceneGraphNode();
	}


	
	public void addShape(final SceneGraphNode the_shape) {
		my_root.addChild(the_shape);
		refresh();
		my_selected = the_shape;
		setChanged();
		notifyObservers(my_selected);
		
	}

	public Component getCanvas() {
		return my_canvas;
	}

	public SceneGraphNode getSelected() {
		return my_selected;
	}
	
	public SceneGraphNode getRoot() {
		return my_root;
	}

	public void refresh() {
		displayListID = -1;


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
			List<SceneGraphNode> picked = my_root.getPicked(drawable); 
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			pickNextFrame = false;
			
			

			if(picked.isEmpty()){
				my_selected = null;
			}else{
				my_selected = picked.get(picked.size() - 1);
			}
			setChanged();
			notifyObservers(my_selected);
		}
		if (displayListID == -1) {
			displayListID = 1;
			gl.glNewList(displayListID, GL2.GL_COMPILE_AND_EXECUTE);
			my_root.render(drawable);
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

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		final double UNIT = 2;

		if (width < height) {
			left = -UNIT / 2;
			right = UNIT / 2;
			top = (double) height / width * UNIT / 2;
			bottom = -top;
		} else {
			top = UNIT / 2;
			bottom = -UNIT / 2;
			right = (double) width / height * UNIT / 2;
			left = -right;
		}

		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	public void remove() {
		my_root.removeChild(my_selected);
		my_selected = null;
		setChanged();
		notifyObservers();
		
	}
}
