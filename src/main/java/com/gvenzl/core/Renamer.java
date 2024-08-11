/*
 * Since: September 2023
 * Author: gvenzl
 * Name: Renamer.java
 * Description:
 *
 * MIT License
 *
 * Copyright (c) 2023 Gerald Venzl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gvenzl.core;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.gvenzl.log.Logger;

public class Renamer {

    private final boolean recursive;
    private final boolean analyzeOnly;
    private final boolean replaceSpecialChars;
    private File directory;
    private final String pattern;
    private Pattern regExPattern;
    private final String newLayout;
    private Pattern regExReplace;
    private final Logger logger;

    public Renamer(String pattern, String newLayout, boolean recursive,
                   boolean analyzeOnly,boolean replaceSpecialChars, Logger logger) {
        this.pattern = pattern;
        this.newLayout = newLayout;
        this.recursive = recursive;
        this.analyzeOnly = analyzeOnly;
        this.replaceSpecialChars = replaceSpecialChars;
        this.logger = logger;
    }

    public void rename(String dir) {
        logger.clear();

        // Only continue if both regular expressions are valid
        if(parseRegEx()) {
            directory = new File(dir);
            logger.log("Renaming files...");

            // Get file list and iterate over them to rename
            File[] renameList;
            // If directory, get the content of it
            if (directory.isDirectory()) {
                renameList = directory.listFiles();
            }
            // If single file, just replace single file
            else {
                renameList = new File[] { directory };
            }

            for (File file : renameList) {
                // If file is hidden, skip it!
                if (file.isHidden()) {
                    continue;
                }

                // If file is a subdirectory and recursive is set, rename content of subdirectory
                if (file.isDirectory() && recursive &&
                        (!file.getName().equalsIgnoreCase(".")) &&
                        (!file.getName().equalsIgnoreCase(".."))
                    ) {
                    // Recursive call to rename subdirectory files
                    rename(file.getAbsolutePath());
                }

                try {
                    String newFileName = file.getName();
                    if (regExPattern.matcher(newFileName).matches()) {
                        newFileName = regExPattern.matcher(file.getName()).replaceAll(regExReplace.toString());
                        // If special character replace is true then remove . and , from file name
                        if (replaceSpecialChars) {
                            newFileName = newFileName.replaceAll("[\\.,_](?!\\w{3}$)", " ");
                            }
                    }

                    logger.log("Renaming file \"" + file.getName() + "\" to \"" + newFileName);
                    newFileName = file.getParent() + "/" + newFileName;

                    // If not only analyze, rename file on file system
                    if (!analyzeOnly) {
                        file.renameTo(new File(newFileName));
                    }
                }
                catch (Exception e) {
                    logger.log("Error renaming the file " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    public boolean parseRegEx() {
        try {
            logger.log("Pattern: " + pattern);
            regExPattern = Pattern.compile(pattern);

            logger.log("New layout: " + newLayout);
            regExReplace = Pattern.compile(newLayout);

            return true;
        }
        catch (PatternSyntaxException e) {
            logger.log("Invalid regular expression: " + e.getMessage());
            return false;
        }
    }
}
