package client;


import java.awt.Component;
import java.awt.event.*;

public class InputHandler implements KeyListener
{
	
	private boolean[] mKeys = new boolean[256];
	
	public InputHandler(Component c) {
		c.addKeyListener(this); 
	}
	
	public boolean isKeyDown(int keyCode) { 
		if (keyCode > 0 && keyCode < 256) {
			return mKeys[keyCode];
		}
		return false;
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
			mKeys[e.getKeyCode()] = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
			mKeys[e.getKeyCode()] = false;
		}
	}
	
	public void keyTyped(KeyEvent e){}
}