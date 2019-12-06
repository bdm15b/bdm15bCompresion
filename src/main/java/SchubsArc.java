
/**
 * ############## HEADER ##############
 * Author: Brighton Mica
 * Date: December 2, 2019
 * Class: Software Engineering 2
 * Prof: Dr. Reeves
 * 
 * ############## DESIGN ##############
 * 
 * 
 * 
 */

/*************************************************************************
 *  Compilation:  javac Huffman.java
 *  Execution:    java Huffman - < input.txt   (compress)
 *  Execution:    java Huffman + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   http://algs4.cs.princeton.edu/55compression/abra.txt
 *                http://algs4.cs.princeton.edu/55compression/tinytinyTale.txt
 *  modified:     change logging to true to enable debugging info to StdErr
 *
 *
 *  Compress or expand a binary input stream using the Huffman algorithm.
 *
 *  % java Huffman - < abra.txt | java BinaryDump 60
 *  010100000100101000100010010000110100001101010100101010000100
 *  000000000000000000000000000110001111100101101000111110010100
 *  120 bits
 *
 *  % java Huffman - < abra.txt | java Huffman +
 *  ABRACADABRA!
 *
 *************************************************************************/

 

import java.io.File;
import java.io.FileNotFoundException;


import sedgewick.*;

public class SchubsArc {

    public static void tarFile(String filename, BinaryOut out) {
        File in = null;
        BinaryIn bin = null;
        
        // all ones -> 11111111
        // the separator is used to distinguish between the elements in our Tar agreement 
        char separator = (char) 255;

        try {

            // open file for read
            in = new File(filename);
            if (!in.exists() || !in.isFile()) {
                System.out.println(filename + " does not exist or is not a file.");
                return;
            }

            // write filename size
            int filenameSize = filename.length();
            out.write(filenameSize);
            out.write(separator);

            // write filename
            out.write(filename);
            out.write(separator);

            // write file size
            long fileSize = in.length();
            out.write(fileSize);
            out.write(separator);

            // write file contents
            bin = new BinaryIn(filename);
            while (!bin.isEmpty()) {
                char x = bin.readChar();
                out.write(x);
            }
            bin.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Please enter the correct number of arguments");
            return;
        }

        BinaryOut out = null;
        String archiveName = args[0];

        // get archive extension -> lets us know which type of compression
        String extension = args[0].substring(args[0].lastIndexOf(".") + 1);
        
        switch (extension) {
            case "zh":

                // tar the files into "archive.zh"
                try {
                    out = new BinaryOut(archiveName);
                    for (int i = 1; i < args.length; i++) {
                        tarFile(args[i], out);
                    }
                } catch (Exception ex) {
                    System.out.println("Please enter a valid archive path");
                } finally {
                    if (out != null)
                        out.close();
                }

                // compress (Huffman) on tar file
                SchubsH.main(new String[] { archiveName });
                
                // rename archive to get extension correct (compresing adds a .hh to archive name)
                File oldName = new File(archiveName + ".hh");
                File newName = new File(archiveName);
                if (!oldName.renameTo(newName))
                    System.out.println("Error renaming compredd archive");

                break;
            default:
                System.out.println("This filetype is not supported");
        }
    }
}
