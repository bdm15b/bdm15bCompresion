/**
 * ------------- HEADER -------------
 *  Author: Brighton Mica
 *  Date: December 6, 2019
 *  Prof: Dr. Reeves (Software Engineering 2)
 *  Assignment: Final Compression Project
 * 
 * ------------- CLASS INFO -------------
 *  Class: SchubsArc
 *  Dependencies: BinaryIn.java BinaryOut.java MinPQ.java
 *  Modified Version of Files From: Class Session in SE2
 * 
 *  ------------- CLI -------------
 *  Note: "/" or "\" may vary depending on OS
 *  
 *  To Compress a File
 *      > mvn compile
 *      > java -cp target/classes SchubsArc <archivename>.zh <filename>
 *  
 *  To Test
 *      > mvn test
 *      Note: tests can be found in src/tests/java
 * 
 *  Example
 *      This will compress all files in folder4 into an archive.zh in folder4
 *      > java -cp target/classes/ SchubsArc src/files/huffmanArchiveTests/folder4/archive.zh src/files/huffmanArchiveTests/folder4/*.txt
 *      
 *      To uncompress...
 *      > java -cp target/classes/ Deschubs src/files/huffmanArchiveTests/folder4/archive.zh
 * 
 * ------------- DESIGN -------------
 *  Overview/Process
 *      Tar files then use Huffman to compress the tar.
 * 
 *      Tarring lets us organize multiple files into one according to an agreement (details below).
 *      This allows for us to compress a single tar file. This is beneficial because we can have only
 *      one Huffman table if we choose to compress Huffman or more make better use of our table if we 
 *      choose to compress with lzw.
 *       
 *      TARS Agreement
 *          - (int) filename size
 *          - separator
 *          - filename
 *          - separator
 *          - (long) file size
 *          - separator
 *          - file contents 
 */
 
import java.io.File;

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
                    //System.out.println(archiveName.substring(0,archiveName.lastIndexOf(".")));
                    out = new BinaryOut(archiveName.substring(0,archiveName.lastIndexOf(".")));
                    for (int i = 1; i < args.length; i++) {
                        tarFile(args[i], out);
                    }
                } catch (Exception ex) {
                    System.out.println("Please enter a valid archive path");
                } finally {
                    if (out != null)
                        out.close();
                }

                // at this point there is a tar file called <archive> (no extension)

                // compress (Huffman) on tar file (adds ".zh" extension)
                HelperArcH.main(new String[] { archiveName.substring(0,archiveName.lastIndexOf(".")) });
                File del = new File(archiveName.substring(0,archiveName.lastIndexOf(".")));
                if (del.exists())
                    if (!del.delete())
                        System.out.println("didn't delete file");
                
                break;
            default:
                System.out.println("This filetype is not supported");
        }
    }
}
