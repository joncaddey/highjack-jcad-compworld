import java.awt.*;
import java.awt.event.*;
import java.nio.IntBuffer;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.util.*;

public class Demo4 implements GLEventListener {
	private static JFrame appFrame;
	private static SceneGraphNode sceneGraphRoot;
	private static boolean pickNextFrame;
	private static Point pickedPoint;
	private static double left, right, top, bottom;
	private static int displayListID = -1;
	
	public static void main(String[] args) {
		GLProfile.initSingleton();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new Demo4()).createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		// Fix for background flickering
		// System.setProperty("sun.awt.noerasebackground", "true");
		
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				pickNextFrame = true;
				pickedPoint = new Point(e.getX(), e.getY());
			}
		});

		sceneGraphRoot = SierpinskiTriangle.create(2);
		
		appFrame = new JFrame("JOGL Demo 4");
		appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appFrame.setMinimumSize(new Dimension(256, 256));
		appFrame.add(canvas);
		appFrame.pack();
		if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			appFrame.setExtendedState(appFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		appFrame.setVisible(true);
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
//		sceneGraphRoot.render(drawable);
//		sceneGraphRoot.rotation++;
		gl.glRotatef(1, 0, 0, 1);
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

