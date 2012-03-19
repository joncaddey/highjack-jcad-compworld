package main;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.IntBuffer;
import java.util.Observer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * 
 * @author Steven Cozart Jonathan Caddey
 * 
 */
@SuppressWarnings("serial")
public class AsteroidsCanvas extends GLCanvas implements GLEventListener{
	private static final float NANO = 1f / 1000000000;
	private static final int TARGET_FPS = 60;
	private static final int FRAMES_TO_AVERAGE = 10;
	private static final float MINIMUM_TIME_BETWEEN_FRAMES = 1f / 30;
	
	

	private AsteroidsGame my_game;
	private static final int BOARD_SIZE = 16;

	private int my_frame_count;
	private float my_average_time_between_frames = 1f / TARGET_FPS;
	private long my_time;
	
	private float left, right, top, bottom;
	
	

	public AsteroidsCanvas(final GLCapabilities capabilities, AsteroidsGame the_game) {
		my_game = the_game;
		setFocusable(true);
		addGLEventListener(this);
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent the_e) {
				my_game.keyPressed(the_e);
			}
			
			public void keyReleased(KeyEvent the_e) {
				my_game.keyReleased(the_e);
			}
		});
	}
	public static AsteroidsCanvas getInstance(final AsteroidsGame the_game) {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		return new AsteroidsCanvas(capabilities, the_game);
	}

	public AsteroidsGame getObservable() {
		return my_game; // TODO sigh
	}
	


	public void display(GLAutoDrawable drawable) {
		
		// predict time per frame
		my_frame_count++;
		if (my_frame_count == FRAMES_TO_AVERAGE) {
			my_frame_count = 0;
			final long old = my_time;
			my_time = System.nanoTime();
			my_average_time_between_frames = Math.min((float)(my_time - old) * NANO / FRAMES_TO_AVERAGE, MINIMUM_TIME_BETWEEN_FRAMES);
		}
		my_game.display(drawable, my_average_time_between_frames);
		

	}

	public void dispose(GLAutoDrawable drawable) {
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0, 0, 0, 0);
		IntBuffer selectBuffer = Buffers.newDirectIntBuffer(3);
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);
		my_time = System.nanoTime();
		FPSAnimator fps = new FPSAnimator(drawable, TARGET_FPS);
		fps.start();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		if (width < height) {
			left = -BOARD_SIZE / 2;
			right = -left;
			top = (float) height / width * BOARD_SIZE / 2;
			bottom = -top;
		} else {
			top = BOARD_SIZE / 2;
			bottom = -top;
			right = (float) width / height * BOARD_SIZE / 2;
			left = -right;
		}
		my_game.setWidth((right - left) / 1);
		my_game.setHeight((top - bottom) / 1);


		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}
	
	


}
