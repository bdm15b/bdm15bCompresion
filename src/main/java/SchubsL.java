/**
 * ------------- HEADER -------------
 *  Author: Brighton Mica
 *  Date: December 6, 2019
 *  Prof: Dr. Reeves (Software Engineering 2)
 *  Assignment: Final Compression Project
 * 
 * ------------- CLASS INFO -------------
 *  Class: SchubsL
 *  Dependencies: BinaryIn.java BinaryOut.java TST.java Queue.java
 *  Modified Version of Files From:   http://algs4.cs.princeton.edu/55compression/
 *   
 *  ------------- CLI -------------
 *  Note: "/" or "\" may vary depending on OS
 *  
 *  To Compress a File
 *      > mvn compile
 *      > java -cp target/classes SchubsL <filename>
 * 
 *  Example
 *     This will compress all files in folder4 into thier own *.txt.ll files
 *     > java -cp target/classes/ SchubsL src/files/lzwTests/*.txt
 *      
 *     To uncompress...
 *     > java -cp target/classes/  Deschubs  src/files/lzwTests/test1.txt.ll
 *  
 *  To Test
 *     > mvn test
 *     Note: tests can be found in src/tests/java
 * 
 * ------------- DESIGN -------------
 *  Overview
 *      Adaptive model → Progressively learn an update model as you read text 
 *      (assume the decoder does the same thing). LZW compression is an adaptive
 *      model that encodes data based on the input it has processed at a point in time.
 *  
 *  Process
 *      Initialized with a dictionary of code words (assuming ASCII) where the key is
 *      the string and the value is encoded hex. As we are reading from the file, anytime 
 *      we see a string of characters that isn’t in our table, we add it to our table with 
 *      the string being the key and the value being hex so that the next time we see this 
 *      specific string (if we do) we can encode the the string using the hex value from 
 *      our table. 
 *  
 *  Trade-Offs
 *      Great for Space
 *          We do not have to store a table/model in the compressed file as the 
 *          “decompresser” learns the model as it reads the compressed file. 
 *          Can encoded entire strings rather than only single characters.
 *  
 *      Compresses repetitive data very well
 *          Since data already seen before is in the table, if it is seen again it can 
 *          be easily encoded.
 * 
 *      Only have to read through the file once
 */


import sedgewick.*;

import java.io.File;

public class SchubsL {
    private static final int R = 256;        // number of input chars
    private static final int L = 4096;       // number of codewords = 2^W
    private static final int W = 12;         // codeword width

    private static BinaryIn in;
    private static BinaryOut out;

    public static void compress() { 

        if (in.isEmpty()) return;

        String input = in.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            out.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        out.write(R, W);
        out.close();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter the correct number of arguments");
            return;
        }

        for (int i = 0; i < args.length; i++) {
            try {
    
                File file = new File(args[i]);
                if (!file.exists()) continue;
    
                // create input stream
                in = new BinaryIn(args[i]);
    
                // create output stream
                out = new BinaryOut(args[i] + ".ll");
    
                // compress
                compress();
            } finally {
                // close streams
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
        }
    }
}
