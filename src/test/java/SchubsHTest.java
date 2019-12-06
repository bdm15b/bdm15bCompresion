/**
 * ------------- HEADER -------------
 *  Author: Brighton Mica
 *  Date: December 6, 2019
 *  Prof: Dr. Reeves (Software Engineering 2)
 *  Assignment: Final Compression Project
 * 
 * ------------- CLASS INFO -------------
 *  Class: SchubsHTest
 *  Dependencies: BinaryIn.java BinaryOut.java MinPQ.java SchubsH.java
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
 *      1. Delete all .hh files in test dir
 *      2. Store test file contents in a map
 *      3. Compress test files → creating .*.hh compressed files
 *      4. Delete the uncompressed test files
 *      5. Decompress compressed test files
 *      6. Compare the decompressed content to original test file content stored in map
 * 
 *  Tests
 *      Testing covers
 *          - single files
 *          - multiple files
 *
 *  NOTE: if you want to remove compressed files, then uncomment the last 
 *  cleanTestDir("hh") in the last test
 */

import sedgewick.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class SchubsHTest {

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
        String testDirPath =    "src" + File.separator + 
                                "files" + File.separator + "huffmanTests";

        File folder = new File(testDirPath);
        String[] files = folder.list();

        boolean dirCleaned = true;
        for (String filepath : files)
            if (filepath.substring(filepath.lastIndexOf(".") + 1).equals(extension))
                if (!deleteFile(testDirPath + File.separator + filepath))
                    dirCleaned = false;
     
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
            return false;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    // -------------------------- TESTS -------------------------- //

    @Test
    public void singleFileTest() throws IOException {

        // removes all ".hh" files from huffmanTests directory
        cleanTestDir("hh");

        Map<String, String> originalContents = new HashMap<String, String>();
        String testDirPath =  "src" + File.separator + "files" + File.separator + "huffmanTests";

        File folder = new File(testDirPath);
        String[] files = folder.list();

        for (String relativeFilepath : files) {

            // must add testDirPath to relativeFilePath because folder.list() is relative
            // to folder
            String filepath = testDirPath + File.separator + relativeFilepath;

            // store original file contents
            originalContents.put(filepath, getFileContents(filepath));

            // compress file
            SchubsH.main(new String[] { filepath });

            // delete original file
            deleteFile(filepath);

            // decompress file
            Deschubs.main(new String[] { filepath + ".hh" });

            // compare orignal contents to the contents of the decompressed file
            assertEquals(getFileContents(filepath), originalContents.get(filepath));
        }
    }

    @Test
    public void multipleFileTest() throws IOException {

        // removes all ".hh" files from huffmanTests directory
        cleanTestDir("hh");

        Map<String, String> originalContents = new HashMap<String, String>();
        String testDirPath =  "src" + File.separator + "files" + File.separator + "huffmanTests";

        File folder = new File(testDirPath);
        String[] files = folder.list();

        // add testDirPath to files because the files in files is local to folder
        for (int i = 0; i < files.length; i++)
            files[i] = testDirPath + File.separator + files[i];

        // store original file contents
        for (String filepath : files) 
            originalContents.put(filepath, getFileContents(filepath));

        for (int i = 1; i < files.length; i++) {

            // create an array of multiple args
            String[] test = new String[i+1];
            for (int j = 0; j <= i; j++)
                test[j] = files[j];

            // compress with multiple args
            SchubsH.main(test);

            // delete original files
            for (String fileTobeDeleted : test)
                deleteFile(fileTobeDeleted);

            // descompress files
            for (String fileTobeDecompressed : test)
                Deschubs.main(new String[] { fileTobeDecompressed + ".hh" });

            // compare orignal contents to the contents of the decompressed file
            for (String filepath : test)
                assertEquals(getFileContents(filepath), originalContents.get(filepath));
        }
        //cleanTestDir("hh");
    }
}
