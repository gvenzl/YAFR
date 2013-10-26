package com.optit.core;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.optit.log.Logger;

public class Renamer
{
	private final boolean recursive;
	private final boolean analyzeOnly;
	private final boolean replaceSpecialChars;
	private File directory;
	private final String pattern;
	private Pattern regExPattern;
	private final String newLayout;
	private Pattern regExReplace;
	private final Logger logger;
	
	public Renamer(String pattern, String newLayout, boolean recursive, boolean analyzeOnly, boolean replaceSpecialChars, Logger logger)
	{
		this.pattern = pattern;
		this.newLayout = newLayout;
		this.recursive = recursive;
		this.analyzeOnly = analyzeOnly;
		this.replaceSpecialChars = replaceSpecialChars;
		this.logger = logger;
	}
	
	public void rename(String dir)
		throws Exception
	{
		logger.clear();
		
		// Only continue if both regular expressions are valid
		if(parseRegEx())
		{
			directory = new File(dir);
			logger.log("Renaming files...");
		
			// Get file list and iterate over them to rename
			File[] renameList;
			// If directory, get the content of it
			if (directory.isDirectory())
				renameList = directory.listFiles();
			// If single file, just replace single file
			else
				renameList = new File[] {directory};
			
			for (File file : renameList)
			{
				// If file is hidden, skip it!
				if (file.isHidden())
					continue;
				
				// If file is a sub directory and recursive is set, rename content of sub directory
				if (file.isDirectory() && recursive &&
						(file.getName().compareTo(".")!=0) &&
						(file.getName().compareTo("..")!=0)
					)
				{
					// Recursive call to rename sub directory files
					rename(file.getAbsolutePath());
				}
				
				try
				{
					String newFileName = file.getName();
					if (regExPattern.matcher(newFileName).matches())
					{
						newFileName = regExPattern.matcher(file.getName()).replaceAll(regExReplace.toString());
						// If special character replace is true then remove . and , from file name
						if (replaceSpecialChars)
							newFileName = newFileName.replaceAll("[\\.,_](?!\\w{3}$)", " ");
					}
					
					logger.log("Renaming file \"" + file.getName() + "\" to \"" + newFileName);
					newFileName = file.getParent() + "/" + newFileName;
					
					// If not only analyze, rename file on file system
					if (!analyzeOnly)
						file.renameTo(new File(newFileName));
				}
				catch (Exception e)
				{
					logger.log("Error renaming the file " + file.getName() + ": " + e.getMessage());
				}
			}
		}
	}
	
	public boolean parseRegEx()
	{
		try
		{
			logger.log("Pattern: " + pattern);
			regExPattern = Pattern.compile(pattern);
	
			logger.log("New layout: " + newLayout);
			regExReplace = Pattern.compile(newLayout);
			
			return true;
		}
		catch (PatternSyntaxException e)
		{
			logger.log("Invalid regular expression: " + e.getMessage());
			return false;
		}
	}
}
