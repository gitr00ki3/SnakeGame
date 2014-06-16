package com.snake;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class MyWindow {
	private static final int SIZE= 0x15;
	private static final int RIGHT= 0x00;
	private static final int LEFT= 0x01;
	private static final int UP= 0x02;
	private static final int DOWN= 0x03;
	private static final int INITIAL_LENGTH= 0x02;
	private static final int FULL_SCREEN_WIDTH= Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int FULL_SCREEN_HEIGHT= Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final Color SNAKE_HEAD= Color.GREEN;
	private static final Color SNAKE_BODY= Color.BLUE;
	private static final Color SNAKE_FOOD= Color.RED;
	
	private boolean pause= false;
	private boolean gameOver= false;
	private int delay= 200;
	private boolean fruitAte= true;
	private List<JWindow> snake= new ArrayList<JWindow>();
	private JWindow fruit;
	private int direction;
	
	public static void main(String[] args) {
		try {
			MyWindow myWindow= new MyWindow();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public MyWindow() throws InterruptedException {
		JFrame jFrame= new JFrame();
		jFrame.setUndecorated(true);
		jFrame.setOpacity(0.01f);
		jFrame.setUndecorated(true);
		jFrame.addKeyListener(new MyKeyListener());
		jFrame.addFocusListener(new myFocusListener());
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jFrame.setVisible(true);
		
		int x= MyWindow.FULL_SCREEN_WIDTH/2;
		int y= MyWindow.FULL_SCREEN_HEIGHT/2;
		
		JWindow jWindow;
		for(int i=0; i<MyWindow.INITIAL_LENGTH; i++) {
			jWindow= i%2==0?getABox(x, y, MyWindow.SNAKE_HEAD):getABox(x, y, MyWindow.SNAKE_BODY);
			x+= jWindow.getWidth();
			snake.add(jWindow);
		}
		direction= MyWindow.LEFT;
		while(!gameOver) {
			while(pause);
			if(fruitAte==true) {
				addSnakeBody();
				getNewFood();
				fruitAte= false;
			}
			isFoodThere();
			move();
			Thread.sleep(delay);
		}
	}
	
	private JWindow getABox(int x, int y, Color color) {
		JWindow jWindow= new JWindow();
		jWindow.setAlwaysOnTop(true);
		jWindow.setContentPane(new TranslucentPanel(color));
		jWindow.setSize(MyWindow.SIZE, MyWindow.SIZE);
//		jWindow.pack();
		jWindow.setLocation(x, y);
		jWindow.setVisible(true);
		return jWindow;
	}
	
	private void getNewFood() {
		fruit= getABox((int)(Math.random()*MyWindow.FULL_SCREEN_WIDTH),
					(int)(Math.random()*MyWindow.FULL_SCREEN_HEIGHT), MyWindow.SNAKE_FOOD);
	}
	
	private void isFoodThere() {
		int x= Math.abs(fruit.getX()-snake.get(0).getX());
		int y= Math.abs(fruit.getY()-snake.get(0).getY());
		if(fruit.contains(x, y)) {
			fruit.dispose();
			fruitAte= true;
		}
	}
	
	private void addSnakeBody() {
		JWindow snakeTail= snake.get(snake.size()-1);
		JWindow snakeBody= getABox(snakeTail.getX(), snakeTail.getY(), snake.size()%2==0?MyWindow.SNAKE_HEAD:MyWindow.SNAKE_BODY);
		snake.add(snakeBody);
	}
	
	private void move() throws InterruptedException {
		JWindow nWindow;
		JWindow pWindow;
		JWindow snakeHead= snake.get(0);

		for(int i=snake.size()-1; i>0; i--) {
			nWindow= snake.get(i);
			pWindow= snake.get(i-1);
			nWindow.setLocation(pWindow.getX(), pWindow.getY());
		}
		
		if(direction==MyWindow.LEFT) {
			int x= snakeHead.getX()-snakeHead.getWidth();
			x= x<0?MyWindow.FULL_SCREEN_WIDTH-x:x;
			snakeHead.setLocation(x, snakeHead.getY());
		}else if(direction==MyWindow.RIGHT) {
			snakeHead.setLocation((snakeHead.getX()+snakeHead.getWidth())%MyWindow.FULL_SCREEN_WIDTH, snakeHead.getY());
		}else if(direction==MyWindow.UP) {
			int y= snakeHead.getY()-snakeHead.getHeight();
			y= y<0?MyWindow.FULL_SCREEN_HEIGHT-y:y;
			snakeHead.setLocation(snakeHead.getX(), y);
		}else {
			snakeHead.setLocation(snakeHead.getX(), (snakeHead.getY()+snakeHead.getHeight())%MyWindow.FULL_SCREEN_HEIGHT);
		}
		
		for(int i=1; i<snake.size(); i++) {
			nWindow= snake.get(i);
			if(nWindow.contains(Math.abs(nWindow.getX()-snakeHead.getX()), Math.abs(nWindow.getY()-snakeHead.getY()))) {
				callGameOver();
			}
		}
	}
	
	private void callGameOver() {
		System.out.println("Game over");
		gameOver= true;
	}
	
	class TranslucentPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Color color;
		
		public TranslucentPanel(Color color) {
			setOpaque(false);
			this.color= color;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2D= (Graphics2D) g.create();
			g2D.setComposite(AlphaComposite.SrcOver.derive(0.85f));
			g2D.setBackground(getBackground());
			g2D.setColor(color);
			g2D.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	class MyKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent arg0) {
			super.keyPressed(arg0);
			if(pause) {
				pause= false;
				return;
			}
			switch (arg0.getExtendedKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			case KeyEvent.VK_UP:
				if(direction!=MyWindow.DOWN) {
					direction= MyWindow.UP;
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction!=MyWindow.UP) {
					direction= MyWindow.DOWN;
				}
				break;
			case KeyEvent.VK_LEFT:
				if(direction!=MyWindow.RIGHT) {
					direction= MyWindow.LEFT;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction!=MyWindow.LEFT) {
					direction= MyWindow.RIGHT;
				}
				break;
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	class myFocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent arg0) {
			super.focusLost(arg0);
			pause= true;
		}
	}
}
