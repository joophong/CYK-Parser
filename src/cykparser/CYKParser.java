/**
 * 
 */
package cykparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Joopyo Hong
 *
 */
public class CYKParser {
    private Map<String, ArrayList<Character>> gMap;
    private char start;

    /**
     * Constructor for CYKParser class
     * 
     * @param map   Mapping of right hand side of the rule to left hand side of the rule
     * @param start The starting nonterminal
     * @throws IOException  If an I/O error occurs
     */
    public CYKParser(Map<String, ArrayList<Character>> map, char start) {
        gMap = map;
        this.start = start;
    }
    
    /**
     * Factory method for CYKParser class
     * 
     * @param fileName   File name input by the user
     * @throws IOException  If an I/O error occurs
     */
    static CYKParser createCYK(String fileName) throws IOException {
        CYKParser parser = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Please put a name of an existing file.");
            e.printStackTrace();
        }
        
        try {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            try {
                Map<String, ArrayList<Character>> grammarMap = new HashMap<String, ArrayList<Character>>();
                boolean first = true;
                char start = '\0';
                while (true) {
                    String s = bufferedReader.readLine();
                    //if end of file, bufferedReader returns null
                    if (s == null) break;
                    
                    int i = 0;
                    char org;
                    String des = "";
                    
                    if (s.charAt(i) == '-') {
                        System.out.println("Invalid rule syntax: no non-terminal was found before ->.");
                        return null;
                    }
                    org = s.charAt(i);
                    if (first) {
                        start = org;
                        first = false;
                    }
                    
                    if (s.charAt(++i) != ' ') {
                        System.out.println("Invalid rule syntax: ' ' was not found before '->'.");
                        return null;
                    }
         
                    if (s.charAt(++i) != '-') {
                        System.out.println("Invalid rule syntax: '-' was not found after non-terminal.");
                        return null;
                    }
                    
                    if (s.charAt(++i) != '>') {
                        System.out.println("Invalid rule syntax: '>' was not found after '-'.");
                        return null;
                    }
                    
                    if (s.charAt(++i) != ' ') {
                        System.out.println("Invalid rule syntax: ' ' was not found after '->'.");
                        return null;
                    }
                    
                    i++;
                    while (i < s.length()) {
                        des += s.charAt(i);
                        i++;
                    }
                    
                    if (!(grammarMap.containsKey(des))) grammarMap.put(des, new ArrayList<Character>());
                    grammarMap.get(des).add(org);

                }
                parser = new CYKParser(grammarMap, start);
                
        
        } catch (IOException e) {
            System.out.println("Please put proper text in the file.");
            e.printStackTrace();
        }
        }
        
        finally {
        //whatever happens we want to close the stream
        fileReader.close();
        }
        
        return parser;
    }
    
    /**
     * Indicates whether the CFG will accept or reject the given string.
     * 
     * @param input String to be tested
     */
    boolean parse(String input) {
        ArrayList<String> list = new ArrayList<String>();
        int i = 0;
        while (i < input.length()) {
            String temp = "";
            while (input.charAt(i) != ' ') {
                temp += input.charAt(i);
                if(i == input.length() - 1) return false;
                i++;
            }
            temp += ' ';
            list.add(temp);
            i++;
        }
        
//        System.out.println(list);
        
        int tokenNum = list.size();
        ArrayList<Character> [][] matrix = new ArrayList [tokenNum][tokenNum];
        
        for (int row = 0; row < tokenNum; row++) {
            for (int col = 0; col < tokenNum; col++) {
                if (col + row == tokenNum) break;
                matrix[col][row] = new ArrayList<Character>();
                if (row == 0) {
                    ArrayList<Character> candidates = gMap.get(list.get(col));
                    if (candidates != null) matrix[col][row].addAll(candidates);
                    
                } else {
                    int count = 0;
                    while (count != row) {
                        ArrayList<Character> list1 = matrix[col][count];
                        ArrayList<Character> list2 = matrix[col+count+1][row-count-1];
                        
                        for (int outer = 0; outer < list1.size(); outer++) {
                            for (int inner = 0; inner < list2.size(); inner++) {
                                String sum = "" + list1.get(outer) + list2.get(inner);
                                ArrayList<Character> nontSet = gMap.get(sum);
                                if (nontSet != null) matrix[col][row].addAll(nontSet);
                            }
                        }
                        count++;
                    }
                }
            }
        }
        
        ArrayList<Character> answerSlot = matrix[0][tokenNum-1];
        return answerSlot.contains(start);
    }
    
    /**
     * Prompts the user to enter the name of the file that is supposed to contain CFG in CNF.
     * Prompts the user to enter any string to test if it part of the CFG.
     * 
     * @param args
     * @throws IOException If an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of file you want to read in CNF grammar from:");
        String fileName = sc.nextLine().trim();
        CYKParser parser = createCYK(fileName);
        
        if (parser == null) {
            System.out.println("\nThe designated file has error in its syntax. "
                    + "Please correct the file before submitting it for parsing.");
            sc.close();
            return;
        }
        
        
        while (true) {
            System.out.println("\nEnter the any string for evaluation. Or enter 'q' to quit.");
            String input = sc.nextLine();
            if (input.length() == 1 && input.charAt(0) == 'q') break;
            System.out.println(parser.parse(input));
        }
        sc.close();
        System.out.println("\nBye!");
        
        return;
    }

}
