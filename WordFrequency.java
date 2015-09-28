import java.util.*;
import java.io.*;
import java.lang.*;


public class WordFrequency
{
  //private ArrayList<WordPair> list = new ArrayList<WordPair>();
  private Map<String, Integer> map = new LinkedHashMap<String, Integer>();
  private int threshold = 0;
  
  public static void main(String[] args)
  {
    WordFrequency hw = new WordFrequency();
    if(args.length==0||args.length==1)
    {
      System.err.println("Error: Mode and filename expected");
      System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
      System.out.println("Utility to count the occurences of each word in a text file.");
      System.out.println("MODE is one of:");
      System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
      System.out.println("  --by-word  sort by words, alphabetically");
      System.out.println("  --by-orig  show words in order of first appearance");
    }
    else if(args.length==2)
    {
      if(args[0].equals("--by-freq"))
      {
        try
        {
          File readIn = new File(args[1]);
          hw.storeInfo(readIn);
          hw.sortByFreq();
        }
        catch(IOException e)
        {
          System.err.println("Error: Mode and filename expected");
          System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
          System.out.println("Utility to count the occurences of each word in a text file.");
          System.out.println("MODE is one of:");
          System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
          System.out.println("  --by-word  sort by words, alphabetically");
          System.out.println("  --by-orig  show words in order of first appearance");
        }
        
      }
      else if(args[0].equals("--by-word"))
      {
        try
        {
          File readIn = new File(args[1]);
          hw.storeInfo(readIn);
          hw.sortByWord();
        }
        catch(IOException e)
        {
          System.err.println("Error: Mode and filename expected");
          System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
          System.out.println("Utility to count the occurences of each word in a text file.");
          System.out.println("MODE is one of:");
          System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
          System.out.println("  --by-word  sort by words, alphabetically");
          System.out.println("  --by-orig  show words in order of first appearance");
        }
      }
      else if(args[0].equals("--by-orig"))
      {
        try
        {
          File readIn = new File(args[1]);
          hw.storeInfo(readIn);
          hw.sortByOrig();
        }
        catch(IOException e)
        {
          System.err.println("Error: Mode and filename expected");
          System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
          System.out.println("Utility to count the occurences of each word in a text file.");
          System.out.println("MODE is one of:");
          System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
          System.out.println("  --by-word  sort by words, alphabetically");
          System.out.println("  --by-orig  show words in order of first appearance");
        }
      }
      else
      {
        System.err.println("Error: Mode and filename expected");
        System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
        System.out.println("Utility to count the occurences of each word in a text file.");
        System.out.println("MODE is one of:");
        System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
        System.out.println("  --by-word  sort by words, alphabetically");
        System.out.println("  --by-orig  show words in order of first appearance");
      }
      
    }
    else if(args.length==3)
    {
      if(args[0].equals("--by-freq"))
      {
        String[] threshold = args[1].split("=");
        if(threshold.length==2&&threshold[0].equals("--threshold"))
        {
          hw.setThreshold(Integer.parseInt(threshold[1])); 
          try
          {
            File readIn = new File(args[2]);
            hw.storeInfo(readIn);
            hw.sortByFreq();
          }
          catch(IOException e)
          {
            System.err.println("Error: Mode and filename expected");
            System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
            System.out.println("Utility to count the occurences of each word in a text file.");
            System.out.println("MODE is one of:");
            System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
            System.out.println("  --by-word  sort by words, alphabetically");
            System.out.println("  --by-orig  show words in order of first appearance");
          }
        }
        else
        {
          System.err.println("Error: Mode and filename expected");
          System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
          System.out.println("Utility to count the occurences of each word in a text file.");
          System.out.println("MODE is one of:");
          System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
          System.out.println("  --by-word  sort by words, alphabetically");
          System.out.println("  --by-orig  show words in order of first appearance");
        }
        
      }
      else if(args[0].equals("--by-word"))
      {
        String[] threshold = args[1].split("=");
        if(threshold.length==2&&threshold[0].equals("--threshold"))
        {
          hw.setThreshold(Integer.parseInt(threshold[1]));
          try
          {
            File readIn = new File(args[2]);
            hw.storeInfo(readIn);
            hw.sortByWord();
          }
          catch(IOException e)
          {
            System.err.println("Error: Mode and filename expected");
            System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
            System.out.println("Utility to count the occurences of each word in a text file.");
            System.out.println("MODE is one of:");
            System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
            System.out.println("  --by-word  sort by words, alphabetically");
            System.out.println("  --by-orig  show words in order of first appearance");
          }
        }
        else
        {
          System.err.println("Error: Mode and filename expected");
          System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
          System.out.println("Utility to count the occurences of each word in a text file.");
          System.out.println("MODE is one of:");
          System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
          System.out.println("  --by-word  sort by words, alphabetically");
          System.out.println("  --by-orig  show words in order of first appearance");
        }

      }
      else if(args[0].equals("--by-orig"))
      {
        String[] threshold = args[1].split("=");
        if(threshold.length==2&&threshold[0].equals("--threshold"))
        {
          //System.out.println("I am here");
          hw.setThreshold(Integer.parseInt(threshold[1]));
          try
          {
            File readIn = new File(args[2]);
            hw.storeInfo(readIn);
            hw.sortByOrig();
          }
          catch(IOException e)
          {
            //System.out.println("isItException?");
            System.err.println("Error: Mode and filename expected");
            System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
            System.out.println("Utility to count the occurences of each word in a text file.");
            System.out.println("MODE is one of:");
            System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
            System.out.println("  --by-word  sort by words, alphabetically");
            System.out.println("  --by-orig  show words in order of first appearance");
          }
        }
        else
        {
          System.err.println("Error: Mode and filename expected");
          System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
          System.out.println("Utility to count the occurences of each word in a text file.");
          System.out.println("MODE is one of:");
          System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
          System.out.println("  --by-word  sort by words, alphabetically");
          System.out.println("  --by-orig  show words in order of first appearance");
        }

      }
      else
      {
        System.err.println("Error: Mode and filename expected");
        System.out.println("Usage: java WordFrequency <MODE> [--threshold=NUM] <TEXTFILE>");
        System.out.println("Utility to count the occurences of each word in a text file.");
        System.out.println("MODE is one of:");
        System.out.println("  --by-freq  sort by frequency count, from highest to lowest");
        System.out.println("  --by-word  sort by words, alphabetically");
        System.out.println("  --by-orig  show words in order of first appearance");
      }
        
    }
  }

  /*
   * Cite: Book
   * Title: Introduction to JAVA programming comprehensive version Tenth Edition
   * Author: Y.Daniel Liang
   * Page: 815
   * Citation: Line 235-249
   *
   *
   */
  public void storeInfo(File readIn) throws IOException
  {
    Scanner in = new Scanner(readIn);
    in.useDelimiter("[^A-Za-z']+");
    while(in.hasNext())
    {
      String text = in.next();
      String word = text.toLowerCase();
      if(word.length()>0)
      {
        if(!this.map.containsKey(word))
        {
          this.map.put(word,1);
        }
        else
        {
          int value = this.map.get(word);
          value++;
          this.map.put(word,value);
        }
      }
        
      
    }
    
  }
  public void sortByOrig() 
  {
    
    Set<Map.Entry<String, Integer>> entrySet = this.map.entrySet();
    for(Map.Entry<String, Integer> entry: entrySet) 
    {  
      if(entry.getValue()>this.threshold)
      {
        System.out.printf("%18s: %d%n", entry.getKey(), entry.getValue());
      }
    }
    
  }
  
  public void sortByWord()
  {
    Map<String, Integer> treeMap = new TreeMap<String, Integer>(this.map);
    Set<Map.Entry<String, Integer>> entrySet = treeMap.entrySet();
    for(Map.Entry<String, Integer> entry: entrySet) 
    {  
      if(entry.getValue()>this.threshold)
      {
        System.out.printf("%18s: %d%n", entry.getKey(), entry.getValue());
      }
    }
  }
  
  /*
   * Cite:
   * URL: http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-
   *      value-on-the-values-in-java
   * Date:July 10th
   *
   * Description: I referenced to the algorithum it is used on the web page
   * in order to achieve the goal to sort the entries based on the value 
   * instead of the key
   *
   *
   */
  public void sortByFreq()
  {
    FreqComparator comparator = new FreqComparator(this.map);
    TreeMap<String, Integer> treeMap = new TreeMap<String, Integer>(comparator);
    treeMap.putAll(this.map);
    Set<Map.Entry<String, Integer>> entrySet = treeMap.entrySet();
    for(Map.Entry<String, Integer> entry: entrySet) 
    {  
      if(entry.getValue()>this.threshold)
      {
        System.out.printf("%18s: %d%n", entry.getKey(), entry.getValue());
      }
    }
    
  }
  private void setThreshold(int holder)
  {
    this.threshold = holder;
  }
  private class FreqComparator implements Comparator<String>
  {
    Map<String, Integer> base;
    public FreqComparator(Map<String, Integer> base)
    {
      this.base = base;
    }
    public int compare(String a, String b)
    {
      if(base.get(a)>base.get(b))
      {
        return -1;
      }
      else if(base.get(a)<base.get(b))
      {
        return 1;
      }
      else
      {
        return a.compareTo(b);
      }
    }
  }
  /*private class WordPair
  {
    private String word;
    private int frequency;
    public WordPair(String word, int frequency)
    {
      setFrequency(frequency);
      setWord(word);
    }
    public String getWord()
    {
      return this.word;
    }
    public int getFrequency()
    {
      return this.frequency;
    }
    public void setWord(String word)
    {
      this.word = word;
    }
    public void setFrequency(int frequency)
    {
      this.frequency = frequency;
    }
  }*/
}
