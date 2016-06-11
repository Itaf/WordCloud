package Assignment5_WordCloud;

import java.util.*;
import java.io.*;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;
import java.lang.String;

/**
 * A program that reads in a text document,
 * counts the occurrence of words, determines 
 * the ten most frequently used words in the document,
 * and creates an HTML file containing the tag cloud.
 * 
 * @author (Itaf O. Joudeh) 
 * @version (March 30, 2014)
 */
public class WordCloud
{
    // instance variables
    private static Set<String> stopWords = new HashSet <String>(); // stores the stop words
    
    private static Scanner input;
    private static String fileName;
    
    private static File document;
    private static Scanner reader;   // scans the text document
    
    private static FileWriter FW;   // new version of the text document
    private static BufferedWriter BW;
    
    private static Map<String,Integer> WordsAndCount; // stores the words read from the text document
    private static int occurrence; // the word frequency
    
    private static Map<String,Integer> sortedMap; // stores the WordsAndCount map entries sorted by occurences (in descending order)
    private static Map<String,Integer> mostFrequentWords = new HashMap<String,Integer>(); // stores the 10 most occurring words

    public static void main(String[]args) throws IOException
    {
        // Please refer to the functions below for the implementations!
        STOP_Words();
        textDocument();
        TextDocToHTML(WordsAndCount, fileName);
    } //end main
    
    /**
     * Creates a Hash Set of the desired stop words read from the provided text file.
     */
    public static void STOP_Words() throws IOException
    {
        File words = new File("Stopwords.txt");
        Scanner wordsReader = new Scanner(words);
        
        while(wordsReader.hasNext())
        {
            stopWords.add(wordsReader.next());  // creating the set of stop words
        } // end while loop
        
        wordsReader.close(); // done: close reader
        
        /**temporary; used for testing and/or checking**/
        //System.out.println(stopWords);  
    } // end STOP_Words
    
    /**
     * Prompts the user for a text document, scans it and removes all stop words from it.
     * Then, creats a tree map of every other word(key) with the occurence count(value).
     * Sorts all words, based on their frequencies, and
     * Uses the provided JCF SORT method to determine the 10 most commonly occurring ones.
     * 
     */
    public static void textDocument() throws IOException
    {
        System.out.print("Please enter the text document file name(include file extension): ");
        input = new Scanner(System.in);
        fileName = input.next();            // gets the entered input file
        document = new File(fileName);
        input.close(); // close input stream
        
        reader = new Scanner(document);   // scans the text document
        
        FW = new FileWriter("new"+fileName);   // new version of the text document
        BW = new BufferedWriter(FW);
        
        WordsAndCount = new TreeMap <String,Integer>();
        
        while(reader.hasNext())
        {
            String word = reader.next();
            word = word.replaceAll("[^A-Za-z]", "");  // Removes all punctuations, numeric digits(0-9), and non-ascii characters
            String lowerCaseWord = word.toLowerCase();
            
            if((stopWords.contains(lowerCaseWord)) || (lowerCaseWord.length() < 3)) // if the word is a stop word or has less than 3 characters (2 and lower)
            {
                /** DO NOTHING, AND MOVE TO THE NEXT WORD IN THE TEXT **/
            } //end if
            else
            {
                if(WordsAndCount.containsKey(lowerCaseWord))  // if the WordsAndCount map already has this word mapped into it
                {   
                    // set the number of occurrences to the current value and increment by 1
                    occurrence = WordsAndCount.get(lowerCaseWord);  
                    occurrence++;                                   
                    WordsAndCount.put(lowerCaseWord,occurrence);
                } //end if
                else
                {
                    //the first occurrence(value = 1) of the word in the document
                    occurrence = 1;
                    WordsAndCount.put(lowerCaseWord,occurrence); 
                } //end else
            } //end else 
        } // end while loop
        
        writeDocument(WordsAndCount, BW); // writes a new text document
        
        reader.close(); // close the file being read
        BW.close(); // close the BuffereWriter
        
        sortedMap = sortByOccurrences(WordsAndCount);   //sorts the WordsAndCount map
        @SuppressWarnings("unchecked")
        Map.Entry<String,Integer>[] sortedArray = sortedMap.entrySet().toArray(new Map.Entry[sortedMap.size()]); // creates an array copy of the sortedMap entries
        
        System.out.println("The 10 most frecuently used words are: ");
        for(int i = 0; i < 10; i++)
        {
            System.out.println(sortedArray[i]);
            mostFrequentWords.put(sortedArray[i].getKey(), sortedArray[i].getValue());
        } // end for loop
        
        mostFrequentWords = sortByOccurrences(mostFrequentWords);  //sorts the mostFrequentWords map
        
        /**temporary; used for testing and/or checking**/
        //System.out.println(WordsAndCount.entrySet());  
        //System.out.println(WordsAndCount.values());
        //for(int k = 0; k < sorted.length; k++)
        //{
            //System.out.println(sorted[k]);       
        //}
        //System.out.println(mostFrequentWords.entrySet());
    } //end textDocument
    
    /**
     * Sorts the specified word map by the words number of occurrences(value) in descending order.
     * Returns an array representation of the new sorted map entries.
     */
    public static <String, Integer extends Comparable<? super Integer>> Map<String, Integer> sortByOccurrences(Map<String, Integer> map) 
    {
        @SuppressWarnings("unchecked")
        Map.Entry<String,Integer>[] MapToArray = map.entrySet().toArray(new Map.Entry[map.size()]); // creates an array of the map entries
        
        // sorts the array in descending order depending on the values
        Arrays.sort(MapToArray, new Comparator<Map.Entry<String, Integer>>() 
        {
            public int compare(Map.Entry<String, Integer> w1, Map.Entry<String, Integer> w2) 
            {
                return w2.getValue().compareTo(w1.getValue());
            }
        });
        
        // creates a new sorted map of the map's entries
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : MapToArray)
            sortedMap.put(entry.getKey(), entry.getValue());
        
        return sortedMap;
    } // end sortByOccurrences

    /**
     * Writes the specified string to the text document accompanied with the specified bufffered writer.
     * 
     */
    public static void writeDocument(Map<String,Integer> map, BufferedWriter BR) throws IOException
    {
        for(String key : map.keySet())
        {
            for(int i = 0; i < key.length(); i++)
            {
                BR.write(key.charAt(i));
            } 
            BR.write(" ");
        } //end for loop
    } // end writeDocument
    
    /**
     * Creates an html WordCloud/Tag consisting of the words in the specified text map, 
     * and names it using the specified file name.
     * 
     */
    public static void TextDocToHTML(Map<String,Integer> text, String file)
    {
        try
        {
            file = file.substring(0,(file.length()-4));  // generates the file name removing the ".txt" extention
            
            FileOutputStream fos = new FileOutputStream(file+"Tags.html");
            OutputStreamWriter output = new OutputStreamWriter(fos);
            
            output.write("<html>");  
            
            // Takes care of the html browser's(bar) title
            output.write("<head>"); 
            output.write("<title>");  
            output.write(file + " Tag Cloud");
            output.write("</title>");  
            output.write("</head>");
            
            // Takes care of the browser's contents(text)
            output.write("<body>");
            for(String key : WordsAndCount.keySet())
            {
                output.write("<span style=\"font-size: " + FoNt_SiZe(mostFrequentWords,key) + "px\">" + key + "</span>");
                output.write(" ");
            } //end for
            output.write("</body>");
            
            // Finishing up
            output.write("</html>");     
            output.close(); // closes the BufferedWriter
            
            System.out.println("An html file is created!");
        } // end try
        catch (IOException e){
            System.err.println(e);
        }
    } // end TextDocToHTML
    
    /**
     * Determines a font size for the specified word passed on its position in the most frequent words map.
     * 
     */
    public static double FoNt_SiZe(Map<String,Integer> frequent, String word)
    {
        int minimum=12, maximum=36, rank = 0;
        
        //The range to which the font size of a word should be adjusted by
        double fontRank = (maximum - minimum)/frequent.size();
        
        if(frequent.containsKey(word)) //if the specified word is in the 10 most frequent words then set the font to the proper size depending on occurrence value
        {
            for(String key : frequent.keySet())
            {
                if(key.equals(word))
                    return maximum - (fontRank*rank);
                else
                    rank++;
            } // end for loop
        } 
        else // else; if the specified word is not in the 10 most frequent words then set the font to its minimum size
        { 
            return minimum;
        } // end if-else
        
        return 0;
    } // end FoNt_SiZe
}


