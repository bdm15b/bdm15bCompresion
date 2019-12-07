/**
 * ------------- HEADER -------------
 *  Author: Brighton Mica
 *  Date: December 6, 2019
 *  Prof: Dr. Reeves (Software Engineering 2)
 *  Assignment: Final Compression Project
 * 
 * ------------- CLASS INFO -------------
 *  Class: SchubsH
 *  Dependencies: BinaryIn.java BinaryOut.java MinPQ.java
 *  Modified Version of Files From:   http://algs4.cs.princeton.edu/55compression/
 * 
 *  ------------- CLI -------------
 *  Note: "/" or "\" may vary depending on OS
 *  
 *  This file is not meant to be run from CLI 
 * 
 * ------------- DESIGN -------------
 *  Overview
 *      accepts a single file and compresses it using huffman. Does 
 *      not add extension.
 */

 import sedgewick.*;
import java.io.File;

public class HelperArcH {

    // alphabet size of extended ASCII
    private static final int R = 256;
    public static boolean logging = true;
    private static BinaryIn in;
    private static BinaryOut out;

    // Huffman trie node
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

    public static void err_print(String msg){
        if (logging)
            System.err.print(msg);
    }

    public static void err_println(String msg) {
        if (logging)
            System.err.println(msg);
    }

    // compress bytes from standard input and write to standard output
    public static void compress() {

        if (in.isEmpty()) return;

        // read the input
        String s = in.readString();
        char[] input = s.toCharArray();

        // tabulate frequency counts
        int[] freq = new int[R];
        for (int i = 0; i < input.length; i++)
            freq[input[i]]++;

        // build Huffman trie
        Node root = buildTrie(freq);

        // build code table
        String[] st = new String[R];
        buildCode(st, root, "");

        // print trie for decoder
        writeTrie(root);
        // err_println("writeTrie");

        // print number of bytes in original uncompressed message
        out.write(input.length);
        // err_println("writing input length " + input.length);

        // err_println("happily encoding... ");
        // String compressed = "";
        // use Huffman code to encode input
        for (int i = 0; i < input.length; i++) {
            String code = st[input[i]];
            // err_print("Char " + input[i] + " ");
            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '0') {
                    out.write(false);
                    // compressed += "0";
                    // err_print("0");
                }
                else if (code.charAt(j) == '1') {
                    out.write(true);
                    // compressed += "1";
                    // err_print("1");
                }
                else throw new RuntimeException("Illegal state");
            }
            // err_println("");
        }

        // System.out.println(compressed);

        // flush output stream
        out.flush();
    }

    // build the Huffman trie given frequencies
    private static Node buildTrie(int[] freq) {

        // initialze priority queue with singleton trees
        MinPQ<Node> pq = new MinPQ<Node>();
        for (char i = 0; i < R; i++)
            if (freq[i] > 0)
                pq.insert(new Node(i, freq[i], null, null));

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left  = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            //err_println("buildTrie parent " + left.freq + " " + right.freq);
            pq.insert(parent);
        }
        return pq.delMin();
    }


    // write bitstring-encoded trie to standard output
    private static void writeTrie(Node x) {
        if (x.isLeaf()) {
            out.write(true);
            out.write(x.ch);
            //err_println("T" + x.ch);
            return;
        }
        out.write(false);
        //err_print("F");

        writeTrie(x.left);
        writeTrie(x.right);
    }

    // make a lookup table from symbols and their encodings
    private static void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left,  s + '0');
            buildCode(st, x.right, s + '1');
        }
        else {
            st[x.ch] = s;
            //err_println("buildCode " + x.ch + " " + s);
        }
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please enter the correct number of arguments");
            return;
        }
        
        // iterate through arguments and compress each one into their
        // own respective file
        //for (int i = 0; i < args.length; i++) {
            try {
                // check if file exists
                File file = new File(args[0]);
                //if (!file.exists()) break;

                // create input stream
                in = new BinaryIn(args[0]);

                // create output stream
                out = new BinaryOut(args[0] + ".zh");

                // compress
                compress();
            } finally {
                // close streams
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
        //}        
    }
}
