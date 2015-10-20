/*
 * Tetris
 *
 * Version 1.0
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
 * Last Revised: 11 December 2014
 */ 

import javax.swing.JFrame;

public class Tetris {

	public static JFrame frame;
	public static boolean gridChoice;
	public static int graphicsChoice;
	public static Display display;
	
	public static void main(String[] args) throws Throwable{
		
		//creates the frame and adds components
		frame = new JFrame("Tetris");
		gridChoice = false;
		graphicsChoice = 0;
		display = new Display(gridChoice, graphicsChoice);
		frame.getContentPane().add(display);
		//makes the frame closeable
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//packs the frame so that everything fits
		frame.pack();
		//sets the frame unresizable
		frame.setResizable(false);
		//makes the frame visible
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}
	
}
