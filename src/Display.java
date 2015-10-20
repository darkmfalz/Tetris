/*
 * Display
 *
 * Version 3.0
 * 
 * Copyright Adeeb Sheikh, 2014
 * 
 * Course: CSC171 Fall 2014
 * 
 * Assignment: Project04
 * 
 * Author: Adeeb Sheikh
 * 
 * Lab Session: TR 12:30 - 1:45
 * 
 * Lab TA: Tom Craw
 * 
 * Last Revised: 17 December 2014
 */ 

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;


public class Display extends JPanel{

	private boolean gridChoice;
	private int graphicsChoice;
	
	private String[][] grid;
	private String[][] ghostGrid;
	private final int square = 24;
	private int x;
	private int ghostx;
	private int y;
	private int ghosty;
	private char block;
	private int border;
	private String[][] tetromino;
	private char hold;
	private int tx;
	private int ty;
	private int tetrCounter;
	private int lines;
	private int score;
	private boolean gameOver = false;
	
	private char[] bag = {'I', 'O', 'T', 'J', 'L', 'S', 'Z'};
	private char[] bagTwo = {'I', 'O', 'T', 'J', 'L', 'S', 'Z'};
	
	private InputMap input;
	private ActionMap action;
	private boolean keyPress;
	private boolean holdPress;
	
	private final Color whiteGreen = new Color(223, 248, 208);
	private final Color lightGreen = new Color(131, 193, 110);
	private final Color darkGreen = new Color(64, 106, 84);
	private final Color blackGreen = new Color(7, 27, 26);
	
	private static Timer gravTimer;
	private static Timer setTimer;
	
	public Display(boolean gridChoice, int graphicsChoice) throws Throwable{
		
		this.gridChoice = gridChoice;
		this.graphicsChoice = graphicsChoice;
		
		lines = 0;
		score = 0;
		
		hold = 'y';
		
		if(graphicsChoice == 0){
			String filename = "580443_Tetris-8bit.wav";
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(Tetris.class.getResourceAsStream(filename));
			clip.open(inputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
			setBackground(whiteGreen);
		}
		else if(graphicsChoice == 1){
			String filename = "580507_Tetris-Rave.wav";
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(Tetris.class.getResourceAsStream(filename));
			clip.open(inputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
			setBackground(Color.BLACK);
		}
		
		grid = new String[10][22];
		ghostGrid = new String[10][22];
		
		for(int i = 0; i < grid.length; i++){
			
			for(int j = 0; j < grid[0].length; j++){
				
				grid[i][j] = "000";
				ghostGrid[i][j] = "000";
				
			}
			
		}
		
		keyPress = true;
		holdPress = true;
		
		input = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		action = getActionMap();
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "LeftArrow");
		action.put("LeftArrow", new KeyAction("LeftArrow"));
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "RightArrow");
		action.put("RightArrow", new KeyAction("RightArrow"));
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "DownArrow");
		action.put("DownArrow", new KeyAction("DownArrow"));
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "UpArrow");
		action.put("UpArrow", new KeyAction("UpArrow"));
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "Space");
		action.put("Space", new KeyAction("Space"));
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "UpArrowRelease");
		action.put("UpArrowRelease", new KeyAction("UpArrowRelease"));
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "SpaceRelease");
		action.put("SpaceRelease", new KeyAction("SpaceRelease"));
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "Shift");
		action.put("Shift", new KeyAction("Shift"));
		
		shuffle(bag);
		shuffle(bagTwo);
		
		gravTimer = new Timer(1000, new GravityTimer());
		setTimer = new Timer(1000, new SetTimer());
		
		setPreferredSize(new Dimension((grid.length)*square*2+square+5*square, (grid[0].length-2)*square+2*square-10));
		
		spawnTetromino();
		
	}
	
	public void paintComponent(Graphics g){
		
		super.paintComponent(g);
		
		int rwidth = square;
		int rheight = square;
		
		if(graphicsChoice == 0){
			
			//classic tetris
			
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			
			//the border
			for(int i = 0; i < grid.length+2; i++){
				
				g.setColor(blackGreen);
				g.fillRect(i*rwidth, 0, rwidth, rheight);
				g.setColor(darkGreen);
				g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth), 0+(int)((3.0/24.0)*rheight), (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
				g.setColor(lightGreen);
				g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth), 0+(int)((6.0/24.0)*rheight), (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(blackGreen);
				g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth), 0+(int)((9.0/24.0)*rheight), (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(lightGreen);
				g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth), 0+(int)((6.0/24.0)*rheight), (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(darkGreen);
				g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth), 0+(int)((9.0/24.0)*rheight), (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
				
				g.setColor(blackGreen);
				g.fillRect(i*rwidth, (grid[0].length-2)*rheight+square, rwidth, rheight);
				g.setColor(darkGreen);
				g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth), (grid[0].length-2)*rheight+square+(int)((3.0/24.0)*rheight), (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
				g.setColor(lightGreen);
				g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth), (grid[0].length-2)*rheight+square+(int)((6.0/24.0)*rheight), (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(blackGreen);
				g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth), (grid[0].length-2)*rheight+square+(int)((9.0/24.0)*rheight), (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(lightGreen);
				g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth), (grid[0].length-2)*rheight+square+(int)((6.0/24.0)*rheight), (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(darkGreen);
				g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth), (grid[0].length-2)*rheight+square+(int)((9.0/24.0)*rheight), (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
				
			}
			
			for(int i = 1; i < grid[0].length-1; i++){
				
				g.setColor(blackGreen);
				g.fillRect(0, i*rheight, rwidth, rheight);
				g.setColor(darkGreen);
				g.fillRect(0+(int)((3.0/24.0)*rwidth), i*rheight+(int)((3.0/24.0)*rheight), (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
				g.setColor(lightGreen);
				g.fillRect(0+(int)((6.0/24.0)*rwidth), i*rheight+(int)((6.0/24.0)*rheight), (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(blackGreen);
				g.fillRect(0+(int)((6.0/24.0)*rwidth), i*rheight+(int)((9.0/24.0)*rheight), (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(lightGreen);
				g.fillRect(0+(int)((6.0/24.0)*rwidth), i*rheight+(int)((6.0/24.0)*rheight), (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(darkGreen);
				g.fillRect(0+(int)((9.0/24.0)*rwidth), i*rheight+(int)((9.0/24.0)*rheight), (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
				
				g.setColor(blackGreen);
				g.fillRect((grid.length+1)*rwidth, i*rheight, rwidth, rheight);
				g.setColor(darkGreen);
				g.fillRect((grid.length+1)*rwidth+(int)((3.0/24.0)*rwidth), i*rheight+(int)((3.0/24.0)*rheight), (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
				g.setColor(lightGreen);
				g.fillRect((grid.length+1)*rwidth+(int)((6.0/24.0)*rwidth), i*rheight+(int)((6.0/24.0)*rheight), (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(blackGreen);
				g.fillRect((grid.length+1)*rwidth+(int)((6.0/24.0)*rwidth), i*rheight+(int)((9.0/24.0)*rheight), (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(lightGreen);
				g.fillRect((grid.length+1)*rwidth+(int)((6.0/24.0)*rwidth), i*rheight+(int)((6.0/24.0)*rheight), (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
				g.setColor(darkGreen);
				g.fillRect((grid.length+1)*rwidth+(int)((9.0/24.0)*rwidth), i*rheight+(int)((9.0/24.0)*rheight), (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
				
			}
			
			//HUD
			
			
			g.setFont(new Font("Monospaced", Font.BOLD, 20));
			if(!gameOver){
				
				g.drawString("Lines: " + lines, this.getWidth()/2+40+rheight*5, this.getHeight()-100);
				g.drawString("Level: " + lines/5, this.getWidth()/2+40+rheight*5, this.getHeight()-80);
				g.drawString("Score: " + score, this.getWidth()/2+40+rheight*5, this.getHeight()-60);
			
			}
			else
				g.drawString("GAME OVER!", this.getWidth()/2+40+rheight*5, this.getHeight()-100);
		
			g.drawString("Hold:", this.getWidth()/2+40, rheight);
			
			g.drawString("Next:", this.getWidth()/2+40, rheight*6);
			
			g.setFont(new Font("Monospaced", Font.BOLD, 13));
			g.drawString("© Adeeb Sheikh, 2014", 2*this.getWidth()/3-15, this.getHeight()-5);
			
			//Hold Piece
			char tempBlock = hold;
			String[][] temp = createTetromino(hold);
			
			g.setColor(Color.BLACK);
			g.fillRect(this.getWidth()/2+40, rheight+10, rwidth*4, rheight*4);
			
			for(int i = 0; i < temp.length; i++){
				
				for(int j = 0; j < temp[i].length; j++){
					
					int xoffset = this.getWidth()/2+40;
					int yoffset = rheight*2+10;
					
					if(tempBlock != 'I' && tempBlock != 'O'){
					
						xoffset = this.getWidth()/2+40+rwidth/2;
						yoffset = rheight*2+10-rheight;
					
					}
					else if(tempBlock == 'I'){
						
						xoffset = this.getWidth()/2+40;
						yoffset = rheight*2+10-rheight/2;
						
					}
					else if(tempBlock == 'O'){

						xoffset = this.getWidth()/2+40+rwidth;
						yoffset = rheight*2+10;
						
					}
					
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					switch(Character.toLowerCase(temp[i][j].charAt(0))){
					case 'i':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						break;
					case 'o':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						break;
					case 't':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'j':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'l':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(darkGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						break;
					case 's':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(darkGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'z':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
						
					}
					
				}
				
			}
			
			//Next Pieces
			tempBlock = bag[0];
			temp = createTetromino(bag[0]);
			
			g.setColor(Color.BLACK);
			g.fillRect(this.getWidth()/2+40, rheight*6+10, rwidth*4, rheight*4);
			
			for(int i = 0; i < temp.length; i++){
				
				for(int j = 0; j < temp[i].length; j++){
					
					int xoffset = this.getWidth()/2+40;
					int yoffset = rheight*7+10;
					
					if(tempBlock != 'I' && tempBlock != 'O'){
					
						xoffset = this.getWidth()/2+40+rwidth/2;
						yoffset = rheight*7+10-rheight;
					
					}
					else if(tempBlock == 'I'){
						
						xoffset = this.getWidth()/2+40;
						yoffset = rheight*7+10-rheight/2;
						
					}
					else if(tempBlock == 'O'){

						xoffset = this.getWidth()/2+40+rwidth;
						yoffset = rheight*7+10;
						
					}
					
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					switch(Character.toLowerCase(temp[i][j].charAt(0))){
					case 'i':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						break;
					case 'o':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						break;
					case 't':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'j':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'l':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(darkGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						break;
					case 's':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(darkGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'z':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
						
					}
					
				}
				
			}
			
			tempBlock = bag[1];
			temp = createTetromino(bag[1]);
			
			g.setColor(Color.BLACK);
			g.fillRect(this.getWidth()/2+40, rheight*6+10+5*rheight, rwidth*4, rheight*4);
			
			for(int i = 0; i < temp.length; i++){
				
				for(int j = 0; j < temp[i].length; j++){
					
					int xoffset = this.getWidth()/2+40;
					int yoffset = rheight*7+10+5*rheight;
					
					if(tempBlock != 'I' && tempBlock != 'O'){
					
						xoffset = this.getWidth()/2+40+rwidth/2;
						yoffset = rheight*7+10-rheight+5*rheight;
					
					}
					else if(tempBlock == 'I'){
						
						xoffset = this.getWidth()/2+40;
						yoffset = rheight*7+10-rheight/2+5*rheight;
						
					}
					else if(tempBlock == 'O'){

						xoffset = this.getWidth()/2+40+rwidth;
						yoffset = rheight*7+10+5*rheight;
						
					}
					
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					switch(Character.toLowerCase(temp[i][j].charAt(0))){
					case 'i':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						break;
					case 'o':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						break;
					case 't':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'j':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'l':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(darkGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						break;
					case 's':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(darkGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'z':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
						
					}
					
				}
				
			}
			
			tempBlock = bag[2];
			temp = createTetromino(bag[2]);
			
			g.setColor(Color.BLACK);
			g.fillRect(this.getWidth()/2+40, rheight*6+10+10*rheight, rwidth*4, rheight*4);
			
			for(int i = 0; i < temp.length; i++){
				
				for(int j = 0; j < temp[i].length; j++){
					
					int xoffset = this.getWidth()/2+40;
					int yoffset = rheight*7+10+10*rheight;
					
					if(tempBlock != 'I' && tempBlock != 'O'){
					
						xoffset = this.getWidth()/2+40+rwidth/2;
						yoffset = rheight*7+10-rheight+10*rheight;
					
					}
					else if(tempBlock == 'I'){
						
						xoffset = this.getWidth()/2+40;
						yoffset = rheight*7+10-rheight/2+10*rheight;
						
					}
					else if(tempBlock == 'O'){

						xoffset = this.getWidth()/2+40+rwidth;
						yoffset = rheight*7+10+10*rheight;
						
					}
					
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					switch(Character.toLowerCase(temp[i][j].charAt(0))){
					case 'i':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						break;
					case 'o':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						break;
					case 't':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'j':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'l':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(darkGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						break;
					case 's':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(darkGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((6.0/24.0)*rheight)+yoffset, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
						g.setColor(whiteGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
					case 'z':
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+xoffset, (j)*rheight+yoffset, rwidth, rheight);
						
						g.setColor(lightGreen);
						g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((3.0/24.0)*rheight)+yoffset, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+xoffset, (j)*rheight+(int)((9.0/24.0)*rheight)+yoffset, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
						break;
						
					}
					
				}
				
			}
			
			//the blocks
			
			for(int i = 0; i < grid.length; i++){
			
				for(int j = 2; j < grid[i].length; j++){
				
					if(gridChoice){
					
						g.setColor(blackGreen);
						g.drawRect(i*rwidth+square, (j-2)*rheight+square, rwidth, rheight);
						
					}
				
					if(grid[i][j].charAt(0) != '0'){
						
						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
						
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+square, (j-2)*rheight+square, rwidth, rheight);
					
						switch(Character.toLowerCase(grid[i][j].charAt(0))){
							case 'i':
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								break;
							case 'o':
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
								break;
							case 't':
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
								break;
							case 'j':
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
								break;
							case 'l':
								g.setColor(darkGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								break;
							case 's':
								g.setColor(darkGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
								break;
							case 'z':
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
								break;
							case 'c':
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+square, (j-2)*rheight+square, rwidth, rheight);
								break;
							
						}
				
					}
					
					//the ghost
					
					else if(ghostGrid[i][j].charAt(0) != '0'){
						
						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
						
						g.setColor(blackGreen);
						g.fillRect(i*rwidth+square, (j-2)*rheight+square, rwidth, rheight);
					
						switch(Character.toLowerCase(grid[i][j].charAt(0))){
							case 'i':
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								break;
							case 'o':
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
								break;
							case 't':
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((9.0/24.0)*rwidth), (int)((9.0/24.0)*rheight));
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
								break;
							case 'j':
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
								break;
							case 'l':
								g.setColor(darkGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								break;
							case 's':
								g.setColor(darkGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((6.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((6.0/24.0)*rheight)+square, (int)((12.0/24.0)*rwidth), (int)((12.0/24.0)*rheight));
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
								break;
							case 'z':
								g.setColor(lightGreen);
								g.fillRect(i*rwidth+(int)((3.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((3.0/24.0)*rheight)+square, (int)((18.0/24.0)*rwidth), (int)((18.0/24.0)*rheight));
								g.setColor(blackGreen);
								g.fillRect(i*rwidth+(int)((9.0/24.0)*rwidth)+square, (j-2)*rheight+(int)((9.0/24.0)*rheight)+square, (int)((6.0/24.0)*rwidth), (int)((6.0/24.0)*rheight));
								break;
							case 'c':
								g.setColor(whiteGreen);
								g.fillRect(i*rwidth+square, (j-2)*rheight+square, rwidth, rheight);
								break;
						}
						
					}
				
				}
			
			}
	
		}
		
		else if(graphicsChoice == 1){
			
			//rave tetris
	
		}	
			
	}
	
	//spawn tetrominoes
	private String[][] createTetromino(char c){
		
		String[][] tempTetromino;
		
		//The individual elements encode data in each character:
		//The first encodes the shape -- 0 refers to blank space
		//the second encodes the x-position relative to the center: 
		//	letters are negative numbers
		//the third encodes the y-position in a similar fashion
		
		
		switch(c){
			case 'I':
				String[][] temp0 = 
					{{"000", "IA0", "000", "000"},
					 {"000", "I00", "000", "000"},
					 {"000", "I10", "000", "000"},
					 {"000", "I20", "000", "000"}};
				tempTetromino = temp0;
				break;
			case 'O':
				String[][] temp1 = 
					{{"O00", "O01"},
					 {"O10", "O11"}};
				tempTetromino = temp1;
				break;
			case 'T':
				String[][] temp2 =
					{{"000", "TA0", "000"},
					 {"000", "T00", "T01"},
					 {"000", "T10", "000"}};
				tempTetromino = temp2;
				break;
			case 'J':
				String[][] temp3 =
					{{"000", "JA0", "JA1"},
					 {"000", "J00", "000"},
					 {"000", "J10", "000"}};
				tempTetromino = temp3;
				break;
			case 'L':
				String[][] temp4 =
					{{"000", "LA0", "000"},
					 {"000", "L00", "000"},
					 {"000", "L10", "L11"}};
				tempTetromino = temp4;
				break;
			case 'S':
				String[][] temp5 =
					{{"000", "SA0", "000"},
					 {"000", "S00", "S01"},
					 {"000", "000", "S11"}};
				tempTetromino = temp5;
				break;
			case 'Z':
				String[][] temp6 =
					{{"000", "000", "ZA1"},
					 {"000", "Z00", "Z01"},
					 {"000", "Z10", "000"}};
				tempTetromino = temp6;
				break;
			default:
				tempTetromino = new String[1][1];
				tempTetromino[0][0] = "000";
				break;
		}
		
		return tempTetromino;
		
	}
	
	public void spawnTetromino(){
		
		tetromino = createTetromino(bag[0]);
		
		block = bag[0];
		border = tetromino.length;
		
		//update the bags
		for(int i = 0; i < bag.length-1; i++)
			bag[i] = bag[i+1];
		if(tetrCounter % 7 == 0 && tetrCounter > 0)
			shuffle(bagTwo);
		bag[bag.length-1] = bagTwo[tetrCounter % 7];
		tetrCounter++;
		
		//add the tetromino to the grid
		x = grid.length/2-1;
		y = 0;
		
		//findCenter
		if(block != 'O'){
			tx = 1;
			ty = 1;
			
			for(int i = x-(tx); i < x-(tx)+tetromino.length; i++){
				
				for(int j = y-(ty)+1; j < y-(ty)+tetromino[i-(x-tx)].length; j++){
					
					grid[i][j] = tetromino[i-(x-tx)][j-(y-ty)];
					
				}
				
			}
			
		}
		
		else{
			
			tx = 0;
			ty = 0;
			
			for(int i = x-(tx); i < x-(tx)+tetromino.length; i++){
				
				for(int j = y-(ty); j < y-(ty)+tetromino[i-(x-tx)].length; j++){
					
					grid[i][j] = tetromino[i-(x-tx)][j-(y-ty)];
					
				}
				
			}
			
		}
		
		repaint();
		
		gravTimer.start();
		
		createGhost();
		
	}
	
	//movement
	private boolean testCollision(int h, int v){
		
		boolean success = true;
		
		int ii = x-tx;
		int fi = x-tx+border;
		int ij = y-ty;
		int fj = y-ty+border;
		
		//uses a nested for loop to see if there is any collision for movement in the direction specified
		test:
		for(int i = ii; i < fi; i++){
			
			for(int j = ij; j < fj; j++){
				
				String s = tetromino[i-ii][j-ij];
				
				if(Character.isUpperCase(s.charAt(0))){
					
					if(i+h >= grid.length || j+v >= grid[i].length || i+h < 0 || j+v < 0){
						
						success = false;
						
						break test;
						
					}
					
					if(Character.isLowerCase(grid[i+h][j+v].charAt(0))){
						
						success = false;
						
						break test;
						
					}
					
				}
				
			}
			
		}
		
		return success;
		
	}
	
	public void moveLeft(){
		
 		if(testCollision(-1, 0)){
 			
 			int ii = (x-tx >= 0) ? x-tx : 0;
			int fi = (x-tx+border < grid.length) ? x-tx+border : grid.length-1;
			int ij = (y-ty >= 0) ? y-ty : 0;
			int fj = (y-ty+border < grid[0].length) ? y-ty+border : grid[0].length-1;
			
			for(int i = ii; i <= fi; i++){
				
				for(int j = ij; j <= fj; j++){
					
					if(Character.isUpperCase(grid[i][j].charAt(0))){
						
						grid[i-1][j] = grid[i][j];
						
						grid[i][j] = "000";
						
					}
					
				}
				
			}
			
			x--;
			
			repaint();
 			
 		}
 		
	}

	public void moveRight(){
		
		if(testCollision(1, 0)){
			
			int ii = (x-tx >= 0) ? x-tx : 0;
			int fi = (x-tx+border < grid.length) ? x-tx+border : grid.length-1;
			int ij = (y-ty >= 0) ? y-ty : 0;
			int fj = (y-ty+border < grid[0].length) ? y-ty+border : grid[0].length-1;
			
			for(int i = fi; i >= ii; i--){
				
				for(int j = ij; j <= fj; j++){
					
					if(Character.isUpperCase(grid[i][j].charAt(0))){
						
						grid[i+1][j] = grid[i][j];
						
						grid[i][j] = "000";
						
					}
					
				}
				
			}
			
			x++;
			
			repaint();
			
		}
		
	}
	
	public void moveDown(){
		
		if(testCollision(0, 1)){
			
			int ii = (x-tx >= 0) ? x-tx : 0;
			int fi = (x-tx+border < grid.length) ? x-tx+border : grid.length-1;
			int ij = (y-ty >= 0) ? y-ty : 0;
			int fj = (y-ty+border < grid[0].length) ? y-ty+border : grid[0].length-1;
			
			for(int i = ii; i <= fi; i++){
				
				for(int j = fj; j >= ij; j--){
					
					if(Character.isUpperCase(grid[i][j].charAt(0))){
						
						grid[i][j+1] = grid[i][j];
						
						grid[i][j] = "000";
						
					}
					
				}
				
			}
			
			y++;
			
			repaint();
			
		}
		
		else
			setTimer.start();
		
	}
	
	public void moveUp(){
		
		if(testCollision(0, -1)){
			
			int ii = (x-tx >= 0) ? x-tx : 0;
			int fi = (x-tx+border < grid.length) ? x-tx+border : grid.length-1;
			int ij = (y-ty >= 0) ? y-ty : 0;
			int fj = (y-ty+border < grid[0].length) ? y-ty+border : grid[0].length-1;
			
			for(int i = ii; i <= fi; i++){
				
				for(int j = ij; j <= fj; j++){
					
					if(Character.isUpperCase(grid[i][j].charAt(0))){
						
						grid[i][j-1] = grid[i][j];
						
						grid[i][j] = "000";
						
					}
					
				}
				
			}
			
			y--;
			
			repaint();
			
		}
		
	}
	
	private boolean testRotation(){
		
		boolean success = true;
		
		int ii = x-tx;
		int ij = y-ty;
		
		//uses a nested for loop to see if there is any collision for movement in the direction specified
		test:
		for(int i = 0; i < tetromino.length; i++){
			
			for(int j = 0; j < tetromino[i].length; j++){
				
				String s = tetromino[i][j];
				
				if(Character.isUpperCase(s.charAt(0))){
					
					if(ii+border-1-j >= grid.length || ij+i >= grid[0].length || ii+border-1-j < 0 || ij+i < 0){
						
						success = false;
						
						break test;
						
					}
					
					if(Character.isLowerCase(grid[ii+border-1-j][ij+i].charAt(0))){
						
						success = false;
						
						break test;
						
					}
					
				}
				
			}
			
		}
		
		return success;
		
	}
	
	public void rotate(){
		
		if(testRotation()){
			
			//copies the tetromino
			String[][] copyTetromino = new String[tetromino.length][];
			for(int i = 0; i < tetromino.length; i++)
				copyTetromino[i] = tetromino[i].clone();
		
			//rotates the tetromino
			for(int i = 0; i < tetromino.length; i++){
			
				for(int j = 0; j < tetromino[i].length; j++){
				
					tetromino[border-1-j][i] = copyTetromino[i][j] ;
				
				}
			
			}
			
			//places the tetromino into the grid
			for(int i = x-(tx); i < x-(tx)+tetromino.length; i++){
			
				for(int j = y-(ty); j < y-(ty)+tetromino[i-(x-tx)].length; j++){
				
					if(tetromino[i-(x-tx)][j-(y-ty)].charAt(0) != '0')
						grid[i][j] = tetromino[i-(x-tx)][j-(y-ty)];
					else if(Character.isUpperCase(grid[i][j].charAt(0)))
						grid[i][j] = "000";
				
				}
			
			}
			
			repaint();
		
		}
		//case-handling, because tetris rotation isn't quite so clear-cut
		
		//if the block is bounded to the right,
		//try moving left and rotate again
		else if((!testCollision(1, 0) || (x == grid.length-2 && block == 'I')) && testCollision(-1, 0)){
			
			moveLeft();
			if(!testRotation()){
				
				if(block == 'I' && testCollision(-1, 0)){
					
					moveLeft();
					if(testRotation())
						rotate();
					else{
						
						moveRight();
						moveRight();
						
					}
				
					
				}
				else
					moveRight();
				
			}
			else
				rotate();
			
		}
		//if the block is bounded to the left
		//move right and try rotating again
		else if((!testCollision(-1, 0) || (x == 1 && block == 'I')) && testCollision(1, 0)){
			
			moveRight();
			if(!testRotation()){
				
				if(block == 'I' && testCollision(1, 0)){
					
					moveRight();
					if(testRotation())
						rotate();
					else{
						
						moveLeft();
						moveLeft();
						
					}
				
					
				}
				else
					moveLeft();
				
			}
			else
				rotate();
			
		}
		//if the block is bounded below
		//try moving up and rotate again
		else if((!testCollision(0, 1) || (y == grid[0].length-1 && block == 'I')) && testCollision(0, -1)){
			
			moveUp();
			if(!testRotation()){
				
				if(block == 'I' && testCollision(0, -1)){
					
					moveUp();
					if(testRotation())
						rotate();
					else{
						
						moveDown();
						moveDown();
						
					}
				
					
				}
				else
					moveDown();
				
			}
			else
				rotate();
			
		}
		//otherwise try this last-ditch attempt
		else{
			
			trial:
			for(int i = 1; i < tetromino.length; i++){
				
				moveUp();
				if(testRotation()){
					
					rotate();
					break trial;
					
				}
				else if(!testCollision(1, 0) && testCollision(-1, 0)){
					
					moveLeft();
					
					if(testRotation()){
						
						rotate();
						break trial;
						
					}
					else if(block == 'I' && testCollision(-1, 0)){
						
						moveLeft();
						
						if(testRotation()){
							
							rotate();
							break trial;
							
						}
						else{
							
							moveRight();
							moveRight();
							
						}
						
					}
					else
						moveRight();
					
				}
				else if(!testCollision(-1, 0) && testCollision(1, 0)){
					
					moveRight();
					
					if(testRotation()){
						
						rotate();
						break trial;
						
					}
					else if(block == 'I' && testCollision(-1, 0)){
						
						moveRight();
						
						if(testRotation()){
							
							rotate();
							break trial;
							
						}
						else{
							
							moveLeft();
							moveLeft();
							
						}
						
					}
					else
						moveLeft();
					
				}
				if(i == tetromino.length-1){
					
					for(int j = i; j > 0; j--)
						moveDown();
					
					break trial;
					
				}
				
			}
			
		}
		
	}
	
	//hold
	public void hold(){
		
		if(holdPress){
		
			holdPress = false;
			
			char temp = block;
		
			removeTetromino();
		
			if(hold == 'y')
				spawnTetromino();
			else{
			
				tetromino = createTetromino(hold);
			
				block = hold;
				border = tetromino.length;
			
				//add the tetromino to the grid
				x = grid.length/2-1;
				y = 0;
			
				//findCenter
				if(block != 'O'){
					tx = 1;
					ty = 1;
				
					for(int i = x-(tx); i < x-(tx)+tetromino.length; i++){
					
						for(int j = y-(ty)+1; j < y-(ty)+tetromino[i-(x-tx)].length; j++){
						
							grid[i][j] = tetromino[i-(x-tx)][j-(y-ty)];
						
						}
					
					}
				
				}
			
				else{
				
					tx = 0;
					ty = 0;
				
					for(int i = x-(tx); i < x-(tx)+tetromino.length; i++){
					
						for(int j = y-(ty); j < y-(ty)+tetromino[i-(x-tx)].length; j++){
						
							grid[i][j] = tetromino[i-(x-tx)][j-(y-ty)];
						
						}
					
					}
				
				}
			
				repaint();
			
				gravTimer.start();
			
				createGhost();
			
			}
		
			hold = temp;
		
		}
		
	}
	
	public void removeTetromino(){
		
		int ii = (x-tx >= 0) ? x-tx : 0;
		int fi = (x-tx+border < grid.length) ? x-tx+border : grid.length-1;
		int ij = (y-ty >= 0) ? y-ty : 0;
		int fj = (y-ty+border < grid[0].length) ? y-ty+border : grid[0].length-1;
		
		for(int i = ii; i <= fi; i++){
			
			for(int j = ij; j <= fj; j++){
				
				if(Character.isUpperCase(grid[i][j].charAt(0))){
					
					grid[i][j] = "000";
					
				}
				
			}
			
		}
		
	}
	
	//ghost
	public boolean ghostCollision(){
		
		int h = 0;
		int v = 1;

		boolean success = true;
	
		int ii = ghostx-tx;
		int fi = ghostx-tx+border;
		int ij = ghosty-ty;
		int fj = ghosty-ty+border;
	
		//uses a nested for loop to see if there is any collision for movement in the direction specified
		test:
			for(int i = ii; i < fi; i++){
		
				for(int j = ij; j < fj; j++){
			
					String s = tetromino[i-ii][j-ij];
			
					if(Character.isUpperCase(s.charAt(0))){
				
						if(i+h >= grid.length || j+v >= grid[i].length || i+h < 0 || j+v < 0){
					
							success = false;
					
							break test;
					
						}
				
						if(Character.isLowerCase(grid[i+h][j+v].charAt(0))){
					
							success = false;
					
							break test;
					
						}
				
					}
			
				}
		
			}
	
		return success;
	
	}

	public void ghostMoveDown(){
		
		if(ghostCollision()){
			
			int ii = (ghostx-tx >= 0) ? ghostx-tx : 0;
			int fi = (ghostx-tx+border < grid.length) ? ghostx-tx+border : grid.length-1;
			int ij = (ghosty-ty >= 0) ? ghosty-ty : 0;
			int fj = (ghosty-ty+border < grid[0].length) ? ghosty-ty+border : grid[0].length-1;
			
			for(int i = ii; i <= fi; i++){
				
				for(int j = fj; j >= ij; j--){
					
					if(Character.isUpperCase(ghostGrid[i][j].charAt(0))){
						
						ghostGrid[i][j+1] = ghostGrid[i][j];
						
						ghostGrid[i][j] = "000";
						
					}
					
				}
				
			}
			
			ghosty++;
			
			repaint();
			
		}
		
	}
	
	public void createGhost(){
		
		ghostx = x;
		ghosty = y;
		
		for(int i = 0; i < grid.length; i++){
			
			for(int j = 0; j < grid[0].length; j++){
				
				if(grid[i][j].charAt(0) != '0' && Character.isUpperCase(grid[i][j].charAt(0)))
					ghostGrid[i][j] = grid[i][j];
				
				else
					ghostGrid[i][j] = "000";
				
			}
			
		}
		
		while(ghostCollision())
			ghostMoveDown();
		
	}
	
	//clear lines
	public void clearLines(int n){
		
		for(int i = 0; i < grid.length; i++)
			grid[i][n] = "000";
			
		repaint();
		
		lines++;
		
		if(lines/5 <= 18){
			
			gravTimer.setDelay(1000-lines/5*50);
			
			setTimer.setDelay(1000-lines/5*25);
			
		}
		
		shiftDown(n);
		
	}
	
	public void shiftDown(int n){
		
		for(int j = n-1; j >= 0; j--){
			
			for(int i = 0; i < grid.length; i++){
				
				grid[i][j+1] = grid[i][j];
				
			}
			
		}
		
	}
	
	//miscellaneous
	private char[] shuffle(char[] array){
		
		Random random = new Random();
		
		for(int i = array.length - 1; i > 0; i--){
			
			int index = random.nextInt(i + 1);
			
			char temp = array[index];
			array[index] = array[i];
			array[i] = temp;
			
		}
		
		return array;
		
	}
	
	public void writeGrid(String[][] a){
		for(int j = 0; j < a[0].length; j++){
			for(int i = 0; i < a.length; i++){
				System.out.print(a[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("~~~~~~");
	}
	
	public void getHeightGrid(){
		
		for(int i = 0; i < grid.length; i++){
			
			if((grid[i][2].charAt(0)) != '0' || (grid[i][1].charAt(0)) != '0' || (grid[i][0].charAt(0))!= '0'){
				
				gameOver = true;
				
			}
			
		}
		
	}
	
	//protected classes
	protected class KeyAction extends AbstractAction{

			private String inputKey;
			
			public KeyAction(String inputKey){
				
				this.inputKey = inputKey;
				
			}

			@Override
			public void actionPerformed(ActionEvent e){
				
				switch(inputKey){
					case "LeftArrow":
						moveLeft();
						break;
					case "RightArrow":
						moveRight();
						break;
					case "DownArrow":
						moveDown();
						break;
					case "UpArrow":
						if(keyPress){
							
							keyPress = false;
							
							rotate();
						
						}
						break;
					case "Space":
						if(keyPress){
							
							keyPress = false;
							holdPress = true;
							
							while(testCollision(0, 1))
								moveDown();
						
							if(setTimer.isRunning())
								setTimer.stop();
						
							for(int i = 0; i < grid.length; i++){
							
								for(int j = 0; j < grid[0].length; j++){
								
									String s = grid[i][j];
								
									grid[i][j] = "" + Character.toLowerCase(s.charAt(0)) + s.substring(1, 3);
								
								}
							
							}
							
							int linesCleared = 0;
							
							for(int j = 0; j < grid[0].length; j++){
								
								int counter = 0;
								
								for(int i = 0; i < grid.length; i++){
									
									if(grid[i][j].charAt(0) != '0')
										counter++;
										
								}
								
								if(counter == grid.length){
									
									clearLines(j);
									linesCleared++;
									
								}
								
							}
							
							if(linesCleared == 1){
								
								score += 40*(lines/5+1);
								
							}
							else if(linesCleared == 2){
								
								score += 100*(lines/5+1);
								
							}
							else if(linesCleared == 3){
								
								score += 300*(lines/5+1);
								
							}
							else if(linesCleared == 4){
								
								score += 1200*(lines/5+1);
								
							}
							
							getHeightGrid();
							
							spawnTetromino();
							
						}
						break;
					case "UpArrowRelease":
						keyPress = true;
						break;
					case "SpaceRelease":
						keyPress = true;
						break;
					case "Shift":
						hold();
						break;
						
				}
				
				createGhost();
				
			}
			
		}

	protected class GravityTimer implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			moveDown();
			
		}

	}
	
	protected class SetTimer implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(!testCollision(0, 1)){
			
				for(int i = 0; i < grid.length; i++){
				
					for(int j = 0; j < grid[0].length; j++){
					
						String s = grid[i][j];
					
						grid[i][j] = "" + Character.toLowerCase(s.charAt(0)) + s.substring(1, 3);
					
					}
				
				}
				
				int linesCleared = 0;
				
				for(int j = 0; j < grid[0].length; j++){
					
					int counter = 0;
					
					for(int i = 0; i < grid.length; i++){
						
						if(grid[i][j].charAt(0) != '0')
							counter++;
							
					}
					
					if(counter == grid.length){
						
						clearLines(j);
						linesCleared++;
						
					}
					
				}
				
				if(linesCleared == 1){
					
					score += 40*(lines/5+1);
					
				}
				else if(linesCleared == 2){
					
					score += 100*(lines/5+1);
					
				}
				else if(linesCleared == 3){
					
					score += 300*(lines/5+1);
					
				}
				else if(linesCleared == 4){
					
					score += 1200*(lines/5+1);
					
				}
				
				getHeightGrid();
				
				spawnTetromino();
			
				Timer tempT = (Timer) e.getSource();
			
				tempT.stop();
				
				holdPress = true;
			
			}
			
			else
				gravTimer.start();

		}
		
	}
	
}
