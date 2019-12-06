/**
 * ------------- HEADER -------------
 *  Author: Brighton Mica
 *  Date: December 6, 2019
 *  Prof: Dr. Reeves (Software Engineering 2)
 *  Assignment: Final Compression Project
 * 
 * ------------- CLASS INFO -------------
 *  Class: Deschubs
 *  Dependencies:   BinaryIn.java BinaryOut.java MinPQ.java 
 *                  SchubsH.java Queue.java TST.java SchubsL.java
 *                  SchubsArc.java
 *  Modified Version of Files From:   http://algs4.cs.princeton.edu/55compression/
 * 
 *  ------------- CLI -------------
 *  Note: "/" or "\" may vary depending on OS
 *  
 *  To Compress a File
 *      > mvn compile
 *      > java -cp target/classes Deschubs <filename>
 *  
 *  To Test
 *      > mvn test
 *      Note: tests can be found in src/tests/java
 * 
 * ------------- DESIGN -------------
 *  Overview
 *      Handles Huffman (.hh), LZW (.ll), and Tarred Huffman (.zh) decompression.
 *  
 *  Process
 *      Depending on the extension given, Deschubs will either carry out a Huffman, 
 *      LZW, or Tarred Huffman decompression. More specifics o each of these 
 *      compression techniques can be found in their “compression” classes.
 *      
 *      Huffman
 *          - Read in trie to create encoding scheme
 *          - Use encoding scheme to then decode file 
 *      
 *      LZW
 *          - Read file an rebuild table as you go
 *      
 *      Tarred Huffman 
 *          - Read in trie to create encoding scheme
 *          - Use encoding scheme to then decode file
 *          - Untar file according to agreement (elaboration in SchubsArc)
 */

import java.io.File;

import sedgewick.*;

public class Deschubs {

    // alphabet size of extended ASCII
    public static boolean logging = true;
    private static BinaryIn in;
    private static BinaryOut out;

    private static final int R = 256;        // number of input chars
    private static final int L = 4096;       // number of codewords = 2^W
    private static final int W = 12;         // codeword width

    // -------------------------- HUFFMAN -------------------------- //

    private static class Node implements Comparable<Node> {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert (left == null && right == null) || (left != null && right != null);
            return (left == null && right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

    public static void err_print(String msg) {
        if (logging)
            System.err.print(msg);
    }

    public static void err_println(String msg) {
        if (logging)
            System.err.println(msg);
    }

    public static void expandH() {

        if (in.isEmpty()) return;

        // read in Huffman trie from input stream
        Node root = readTrie(); 
        // number of bytes to write
        int length = in.readInt();
        // decode using the Huffman trie
        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = in.readBoolean();
                if (bit) x = x.right;
                else     x = x.left;
            }
            out.write(x.ch);
        }
        out.flush();
    }

    private static Node readTrie() {
        boolean isLeaf = in.readBoolean();
        if (isLeaf) {
            char x = in.readChar();
            // err_println("t: " + x );
            return new Node(x, -1, null, null);
        }
        else {
            // err_print("f");
            return new Node('\0', -1, readTrie(), readTrie());
        }
    }

    private static void huffmanExpand(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                System.out.println(filepath + " does not exist");
                return;
            }
            
            in = new BinaryIn(filepath);
            out = new BinaryOut(filepath.substring(0,filepath.length() - 3));
            expandH();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }

    // -------------------------- LZW -------------------------- //

    public static void expandL() {

        if (in.isEmpty()) return;

        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = in.readInt(W);
        String val = st[codeword];

        while (true) {
            out.write(val);
            codeword = in.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        out.close();
    }

    public static void lzwExpand(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                System.out.println(filepath + " does not exist");
                return;
            }
            
            in = new BinaryIn(filepath);
            out = new BinaryOut(filepath.substring(0,filepath.length() - 3));
            expandL();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }


    // -------------------------- TARS -------------------------- //

    // extracts files(s) from a file aaccording to the agreement 
    public static void unTarFile(String tarname) {

        char sep = (char) 255; // all ones 1111111

        in = new BinaryIn(tarname);
        while (!in.isEmpty()) {
            try {                
                // read in file name size
                int filenameSize = in.readInt();
                sep = in.readChar();

                // read in filename
                String filename = "";
                for (int i = 0; i < filenameSize; i++) {
                    filename += in.readChar();
                }
                sep = in.readChar();
    
                // read in filesize
                long filesize = in.readLong();
                sep = in.readChar();
    
                // read in file content
                out = new BinaryOut(filename);
                for (int i = 0; i < filesize; i++)
                    out.write(in.readChar());
            } finally {
                if (out != null)
                    out.close();
            }
        }
        in.close();
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please enter the correct number of arguments");
            return;
        }


        // REDO THIS TO GO FROM LAST DOT NOT LAST TWO CHARS
        // get the extension of the current file 
        String extension = args[0].substring(args[0].lastIndexOf(".") + 1);
        
        
        switch (extension) {
            case "hh":
                // decompress (expand) .hh file using huffman
                huffmanExpand(args[0]);
                break;
            case "ll":
                 // decompress (expand) .ll file using lzw
                lzwExpand(args[0]);
                break;
            case "zh":
                // decompress (expand) .zh file using huffman
                // remove ".zh" extension from archive file
                huffmanExpand(args[0]);

                // untars uncompressed archive 
                unTarFile(args[0].substring(0,args[0].length() - 3));

                // delete file created by huffman decompression
                File file = new File(args[0].substring(0,args[0].length() - 3));
                if (!file.delete())
                    System.out.println("ERROR deleting archive after HUffman decompression");
                break;
            default:
                System.out.println("This filetype is not supported");
        }
    }
}
