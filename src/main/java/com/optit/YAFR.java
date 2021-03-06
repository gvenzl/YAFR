package com.optit;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.optit.core.Renamer;
import com.optit.gui.JLogArea;
import com.optit.log.Logger;
import javax.swing.JScrollPane;

public class YAFR {

	private JFrame frame;
	private JTextField directory;
	private JTextField pattern;
	private JButton doRename;
	private JTextArea logArea;
	private JTextField newLayout;
	private JCheckBox chckbxRecursive;
	private JScrollPane scrollPane;
	private JCheckBox chckbxAnalyzeOnly;
	private JCheckBox chckbxReplaceSpecialCharaters;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					YAFR window = new YAFR();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public YAFR()
	{
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 1024, 512);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblDirectory = new JLabel("Directory:");
		lblDirectory.setBounds(6, 13, 68, 16);
		frame.getContentPane().add(lblDirectory);
		
		directory = new JTextField();
		directory.setBounds(107, 6, 581, 28);
		frame.getContentPane().add(directory);
		directory.setColumns(10);
		
		JLabel lblPattern = new JLabel("Pattern:");
		lblPattern.setBounds(6, 41, 61, 16);
		frame.getContentPane().add(lblPattern);
		
		doRename = new JButton("Rename");
		doRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				rename();
			}
		});
		doRename.setBounds(888, 8, 117, 29);
		frame.getContentPane().add(doRename);
		
		pattern = new JTextField();
		pattern.setText("(\\w*)\\.(\\w*)\\.(.*)\\.(\\w*)\\.(\\w*$)");
		pattern.setBounds(107, 34, 581, 28);
		frame.getContentPane().add(pattern);
		pattern.setColumns(10);
		
		JButton chooseFilePath = new JButton("...");
		chooseFilePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				if(System.getProperty( "os.name" ).equalsIgnoreCase( "mac os x" ))
				{
					FileDialog chooser = new FileDialog(frame, "Select target folder or file" );
					System.setProperty( "apple.awt.fileDialogForDirectories", "true" );
					chooser.setVisible( true );
					System.setProperty( "apple.awt.fileDialogForDirectories", "false" );
				
					if(chooser.getDirectory() != null)
						directory.setText(chooser.getDirectory() + chooser.getFile());
					else
						directory.setText("");
					
				}
				else
				{
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Select target folder or file");
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

					if(chooser.showDialog(frame, null) == JFileChooser.APPROVE_OPTION)
						directory.setText(chooser.getSelectedFile().getAbsolutePath());
					else
						directory.setText("");
				}
			}
		});
		chooseFilePath.setBounds(700, 14, 25, 16);
		frame.getContentPane().add(chooseFilePath);
		
		JLabel lblNewLayout = new JLabel("New Layout:");
		lblNewLayout.setBounds(6, 66, 83, 16);
		frame.getContentPane().add(lblNewLayout);
		
		newLayout = new JTextField();
		newLayout.setText("$1 - $2 - $4.$5");
		newLayout.setColumns(10);
		newLayout.setBounds(107, 61, 581, 28);
		frame.getContentPane().add(newLayout);
		
		chckbxRecursive = new JCheckBox("Recursive");
		chckbxRecursive.setBounds(737, 9, 92, 23);
		frame.getContentPane().add(chckbxRecursive);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 101, 999, 371);
		frame.getContentPane().add(scrollPane);
		
		logArea = new JLogArea();
		scrollPane.setViewportView(logArea);
		
		chckbxAnalyzeOnly = new JCheckBox("Analyze only");
		chckbxAnalyzeOnly.setSelected(true);
		chckbxAnalyzeOnly.setBounds(737, 37, 128, 23);
		frame.getContentPane().add(chckbxAnalyzeOnly);
		
		chckbxReplaceSpecialCharaters = new JCheckBox("Replace special characters");
		chckbxReplaceSpecialCharaters.setSelected(true);
		chckbxReplaceSpecialCharaters.setBounds(737, 62, 203, 23);
		frame.getContentPane().add(chckbxReplaceSpecialCharaters);
		
		frame.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{frame.getContentPane(), lblPattern, doRename, lblDirectory, pattern, logArea, chooseFilePath, lblNewLayout, newLayout, chckbxRecursive, directory}));
	}
	
	private void rename()
	{
		// Only allow rename when all field are filled
		if(directory.getText().isEmpty())
		{
			JOptionPane.showMessageDialog(null, "The directory field is empty!", "Validation error", JOptionPane.ERROR_MESSAGE);
		}
		else if (pattern.getText().isEmpty())
		{
			JOptionPane.showMessageDialog(null, "The pattern field is empty!", "Validation error", JOptionPane.ERROR_MESSAGE);
		}
		else if (newLayout.getText().isEmpty())
		{
			JOptionPane.showMessageDialog(null, "No new layout was defined!", "Validation error", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			Renamer renamer = new Renamer(pattern.getText(), newLayout.getText(),
											chckbxRecursive.isSelected(), chckbxAnalyzeOnly.isSelected(),
											chckbxReplaceSpecialCharaters.isSelected(), (Logger)logArea);
			try
			{
				renamer.rename(directory.getText());
				JOptionPane.showMessageDialog(null, "Renaming done!", "Finish", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (Exception e)
			{
				((Logger)logArea).log("Error during renaming: " + e.getMessage());
			}
		}
	}
}
