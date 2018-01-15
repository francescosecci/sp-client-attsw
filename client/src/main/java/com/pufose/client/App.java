package com.pufose.client;

import java.awt.EventQueue;

import com.pufose.client.gui.GUI;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       EventQueue.invokeLater(new Runnable() {

		@Override
		public void run() {
			new GUI();
			
		}
    	   
       });
    }
}
