/**
 * ------------- HEADER -------------
 *  Author: Brighton Mica
 *  Date: December 6, 2019
 *  Prof: Dr. Reeves (Software Engineering 2)
 *  Assignment: Final Compression Project
 * 
 * ------------- CLASS INFO -------------
 *  Class: SchubsArcTest
 *  Dependencies:   BinaryIn.java BinaryOut.java MinPQ.java 
 *                  SchubsH.java SchubsArc.java Deschubs.java
 * 
 *  ------------- CLI -------------
 *  Note: "/" or "\" may vary depending on OS
 *  
 *  To Test
 *      > mvn test
 *      Note: test files can be found in src/files/
 * 
 * ------------- DESIGN -------------
 *  Overview/Process
 *      OLD files are the oringal file contents. When we test we compress teh orignal
 *      file and rename it by appending OLD to it so you can view the original files (OLD)
 *      and the recently uncompressed files side by side
 * 
 *      1. Delete all .zh and OLD files in test dir
 *      2. Store test file contents in a map
 *      3. Compress test files → creating .*.zh compressed files
 *      4. Rename the uncompressed test files (OLD)
 *      5. Decompress compressed test files
 *      6. Compare the decompressed content to original test file content stored in map
 * 
 *  Tests
 *      Our test harness covers
 *          - no files
 *          - empty files
 *          - single files
 *          - multiple files
 *          - handling of invalid extensions
 *
 *  NOTE: if you want to remove compressed and OLD files, then uncomment the last 
 *  or add a cleanTestDir("xx") in the bottom of tests
 */

import sedgewick.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;



public class SchubsArcTest {

   // -------------------------- HELPER FUNCTIONS -------------------------- //

    // returns the contents of a file in a string
    public String getFileContents(String path) throws IOException {
        FileInputStream in = null;
        String file_content = "";

        try {
            in = new FileInputStream(path);
            int character;
            while ((character = in.read()) != -1) {
                file_content += (char) character;
            }
        } finally {
            if (in != null)
                in.close();
        }
        return file_content;
    }

    // remove all files with "extension" from test dir
    public void cleanTestDir(String extension) {
        String testDirPath =    "src" + File.separator + "files" + File.separator + 
                                "huffmanArchiveTests";

        File root = new File(testDirPath);
        String[] folders = root.list();

        // add testDirPath to folders because the files in files is local to root
        for (int i = 0; i < folders.length; i++)
            folders[i] = testDirPath + File.separator + folders[i];

        boolean dirCleaned = true;
        for (String folder : folders) {

            File dir = new File(folder);
            String[] files = dir.list();

            for (String filepath : files)
                if (filepath.substring(filepath.lastIndexOf(".") + 1).equals(extension))
                    if (!deleteFile(folder + File.separator + filepath))
                        dirCleaned = false;
        }
     
        if (!dirCleaned)
            System.out.println("ERROR cleaning dir " + testDirPath);
    }

    // deletes a file
    public boolean deleteFile(String filename) {

        try {
            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("Cannot find find file " + file.getAbsolutePath());
                return false;
            }
            if (file.delete())
                return true;
            else
                System.out.println("FILE NOT DELETED");
            return false;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }





    // -------------------------- TESTS -------------------------- //


    @Test
    public void folders123Test() throws IOException {

        // removes all ".zh" files from huffmanTests directory
        cleanTestDir("zh");

        Map<String, String> originalContents = new HashMap<String, String>();
        String testDirPath =  "src" + File.separator + "files" + File.separator + "huffmanArchiveTests";

        // folders holds all of the dirs in huffmanArchiveTests
        File root = new File(testDirPath);
        String[] folders = root.list();

        // add testDirPath to folders because the files in files is local to root
        for (int i = 0; i < folders.length; i++)
            folders[i] = testDirPath + File.separator + folders[i];

        for (String folder : folders) {

            String[] archiveName = { folder + File.separator + "archive.zh" };
            
            // files holds all the files in a folder
            File dir = new File(folder);
            String[] files = dir.list();

             // add folder to files because the each files is local to folder and folder has path from src
            for (int i = 0; i < files.length; i++)
                files[i] = folder + File.separator + files[i];

             // store original file contents
            for (String filepath : files) 
                originalContents.put(filepath, getFileContents(filepath));

            // prepend archive name to files so we can pass one array as args
            String[] args = ArrayUtils.addAll(archiveName, files);

            // compress files into archive
            SchubsArc.main(args);

            // delete original files
            for (String fileTobeDeleted : files)
                deleteFile(fileTobeDeleted);
            
            // decompress archive
            Deschubs.main(archiveName);

            // compare orignal contents to the contents of the decompressed file
            for (String filepath : files)
                assertEquals(getFileContents(filepath), originalContents.get(filepath));
        }
    }
}
