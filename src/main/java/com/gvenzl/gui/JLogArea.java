package com.gvenzl.gui;

import javax.swing.JTextArea;

import com.gvenzl.log.Logger;

public class JLogArea extends JTextArea implements Logger
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3659856375159757979L;
	
	public void clear()
	{
		this.setText("");
	}
	
	public void log()
	{
		this.append("\n");
	}
	public void log(String textToLog)
	{
		this.append(textToLog + "\n");
	}
}
