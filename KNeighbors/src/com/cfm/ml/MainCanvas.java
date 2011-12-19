package com.cfm.ml;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainCanvas extends Canvas implements MouseListener, MouseMotionListener, KeyListener{
	
	public static final Color[] COLORS = {Color.lightGray, Color.white};
	
	public static final int PLUS_VALUE = 0,
							MINUS_VALUE = 1;
	
	public static final String[] CHAR_VALUE = {"+", "-"};
	
	public List<TestPoint> points = new ArrayList<TestPoint>();
	
	private int xMouse, yMouse;
	private int k = 1;
	private TestPoint t = null;
	
	private List<TestPoint> tests = new ArrayList<TestPoint>();
	
	static class TestPoint{
		int x, y;
		int value;
		public TestPoint(int x, int y, int value) {
			super();
			this.x = x;
			this.y = y;
			this.value = value;
		}
		public TestPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public long sqDistanceTo(long px, long py){
			return (px - this.x) * (px - this.x) + (py - this.y)*(py - this.y);
		}
		@Override
		public String toString() {
			return "TestPoint [x=" + x + ", y=" + y + ", v=" + CHAR_VALUE[value] + "]";
		}
	}

	public MainCanvas() {
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.addMouseMotionListener(this);
	}

	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		drawKNN(g, k);
		drawPoints(g);
		drawTests(g);
		
		g.setColor(Color.blue);
		g.drawString("k = " + k, 0, getHeight() - 10);
		g.drawString("x = " + xMouse + ", y = " + yMouse,  0, getHeight());
	}
	
	private void drawPoints(Graphics g) {
		g.setColor(Color.black);
		for(int i = 0; i < points.size(); i++ )
			g.drawString(CHAR_VALUE[points.get(i).value],points.get(i).x , points.get(i).y);
		
		if( t == null )
			return;

		g.setColor(Color.blue);
		g.drawString(CHAR_VALUE[t.value],t.x , t.y);
		
	}

	private void drawTests(Graphics g){
		
		for(TestPoint p : tests){
			int v = valueFor(p.x , p.y, k);
			if (v == p.value )
				g.setColor(Color.green);
			else
				g.setColor(Color.red);
				
			g.drawString(CHAR_VALUE[p.value],p.x , p.y);
		}
	}
	
	public void drawKNN(Graphics g, int k){
		System.out.println("Start drawing kNN map ...");
		if( points.size() == 0 )
			return;
		
		for(int x = 0; x < getWidth(); x++)
			for(int y = 0; y < getHeight(); y++){
				int c = valueFor(x, y, k);
				g.setColor(COLORS[c]);
				g.drawRect(x, y, 1, 1);
			}
		System.out.println("End drawing");
	}
	
	public int valueFor(int x, int y, int k){
		TestPoint[] ns = neighbors(x, y, k);
		int pluses = 0;
		
		for(TestPoint t : ns )
			if( t.value == PLUS_VALUE )
				pluses ++;
		
		if( pluses >= (ns.length - pluses) )
			return PLUS_VALUE;
		
		return MINUS_VALUE;
	}
	
	public TestPoint[] neighbors(int x, int y, int k){
		TestPoint[] neigh = new TestPoint[k];
		long dist[] = new long[points.size()];
		
		for(int i = 0; i < dist.length; i++)
			dist[i] = points.get(i).sqDistanceTo(x, y);
		
		for(int j = 0; j < k; j++){
			long min = Long.MAX_VALUE;
			int minIndex = -1;
			for(int i = 0; i < dist.length; i++){
				if( dist[i] < min ){
					minIndex = i;
					min = dist[i];
				}
			}
			neigh[j] = points.get(minIndex);
			dist[minIndex] = Long.MAX_VALUE;
		}
		
		return neigh;
	}

	public void mouseClicked(MouseEvent e) {
		if( e.getButton() != MouseEvent.BUTTON1 )
			return;
		
		if ( (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK){
			points.add(new TestPoint(e.getX(), e.getY(), MINUS_VALUE));
			repaint();
		}else if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK){
			int val = valueFor(e.getX(), e.getY(), k);
			t = new TestPoint(e.getX(), e.getY(), val);
			repaint();
		}else{
			points.add(new TestPoint(e.getX(), e.getY(), PLUS_VALUE));
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		switch(e.getKeyChar()){
			case '+':
				if( k < points.size())
					k++;
				repaint();
				break;
			case '-':
				if( k > 1 )
					k--;
				repaint();
				break;
			
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		xMouse = e.getX();
		yMouse = e.getY();
	}
	
	public void loadPointsInto(String file, List<TestPoint> points) throws IOException{
		Scanner sc = new Scanner(new File(file));
		while(sc.hasNext()){
			
			TestPoint p = new TestPoint(
					sc.nextInt(), 
					sc.nextInt(), 
					sc.nextInt()
			);
			
			points.add(p);
		}
	}
	
	public void loadPoints(String file) throws IOException{		
		loadPointsInto(file, points);
	}
	
	private void loadTests(String file) throws IOException {
		loadPointsInto(file, tests);
	}
	
	public static void main(String[] ar) throws IOException{
		JFrame frame = new JFrame("kNN Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		MainCanvas canvas = new MainCanvas();
		canvas.loadPoints("points_midterm.txt");
		canvas.loadTests("points_midterm_tests.txt");
		canvas.setSize(359, 142);
		
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
	}
}
