package OurGame;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Dude {
	int x, dx, y,nx,nx2,left, dy;
	Image still,jump,reverse;
	
	ImageIcon s; 
	ImageIcon j;
	ImageIcon l; 
	
	public Dude() {
		s = new ImageIcon("E:/kid1.png");
		j= new ImageIcon("E:/kid1rev.png");
		l = new ImageIcon("E:/kid1.png");
		x = 75;
		left = 150;
		nx = 0;
		nx2= 685;
		y = 172;
		still = s.getImage();
		System.out.println("Dude width: "+s.getIconWidth());
		System.out.println("Dude image width: "+s.getImage().getWidth(null));
		
	
	}

	public void move() {
		if (dx != -1){
			if (left + dx <= 150)
				left+=dx;
			else{
		x = x + dx;
		
		nx2= nx2+dx;
			nx = nx + dx;
	}}
		else
	{
		if (left+dx >0)
		left = left + dx;
	}
		}

	public int getX() {
		return x;
	}

	public int getnX() {
		return nx;
	}
	
	public int getnX2() {
		return nx2;
	}

	public int getdx() {
		return dx;
	}
	
	public Image getImage() {
		return still;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_LEFT)
		{		dx = -1;
		still = l.getImage();		}
		
		if (key == KeyEvent.VK_RIGHT)
			{dx = 1;
		still = s.getImage();	
			}
		
		if (key == KeyEvent.VK_UP)
			{dy = 1;
			still = j.getImage();
			}			}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_LEFT)
			dx = 0;

		if (key == KeyEvent.VK_RIGHT)
			dx = 0;
		
		if (key == KeyEvent.VK_UP)
			{dy = 0;
			still = s.getImage();}
			}
	}