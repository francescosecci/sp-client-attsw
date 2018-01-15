package com.pufose.client;


import org.assertj.swing.edt.GuiActionRunner;

import com.pufose.client.gui.GUI;

/**
 * Main application entry point
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	GuiActionRunner.execute(()-> new GUI());
    }
}
