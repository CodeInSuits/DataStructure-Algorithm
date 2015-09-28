import java.util.*;
import java.io.*;
import java.lang.*;
public class HuffmanCodes
{
  private Map<Byte,Integer> freqTable = new TreeMap<Byte,Integer>();
  private Node root;
  private Map<String,Byte> encodeTable;  
  
  
  //magic number
  private static final int VALUE_TAG = 1;
  private static final int DECISION_TAG = 0;
  
  
  
  
  
  //*********************************** convert byte to characters *************************************  
  private static final Map<String,String> escapedLiterals = new HashMap<String,String>();
  static
  {
    escapedLiterals.put("\n","\\n");
    escapedLiterals.put("\r","\\r");
    escapedLiterals.put("\t","\\t");
    escapedLiterals.put("\\","\\\\");
    escapedLiterals.put("\'", "\\\'");
    escapedLiterals.put("\"","\\\"");
  }
  public static String asASCIILiteral(byte asciiValue)
  {
    String escStr = new String(new byte[] {asciiValue});
    escStr = escapedLiterals.getOrDefault(escStr,escStr);
    return String.format("\'%s\'",escStr);
  }  
  //************************************* convert byte to characters ************************************
  
  



  
  
  
  //********************************* main *********************************************    
  public static void main(String[] args)
  {
    HuffmanCodes hardHW = new HuffmanCodes();
    if(args.length==0)
    {
      System.out.println("Usage: java HuffmanCodes MODE [OPTIONS...] IN OUT");
      System.out.println("MODE is one of:");
      System.out.println("  -e   Encodes file IN to file OUT");
      System.out.println("  -d   Decodes file IN to file OUT");
      System.out.println("OPTIONS are zero or more of:");
      System.out.println("  -F   show the frequencies of each byte");
      System.out.println("  -C   show the codes for each byte");
      System.out.println("  -B   show the encoded sequence in binary");
    }
    else if(args[0].equals("-e"))
    {
      if(args[1].equals("-F"))
      {
        hardHW.encoding(args[2],args[3]);
        hardHW.FFlagOn();
      }
      else if(args[1].equals("-C"))
      {
        hardHW.encoding(args[2],args[3]);
        hardHW.CFlagOn();
      }
      else if(args[1].equals("-B"))
      {
        String encodeSequence = hardHW.encoding(args[2],args[3]);
        hardHW.BFlagOn(encodeSequence);
      }
      else
      {
        hardHW.encoding(args[1],args[2]);
      }
    }
    else
    {
      hardHW.decoding(args[1],args[2]);
    }

    
  }
  //****************************** main ***********************************************************
  
  
  
  //*************************************** encoding ***************************************
  public String encoding(String input,String output)
  {
    String encodeSequence = "";
    try
    {
      int inputLength = (this.readBytes(input)).length;
      if(inputLength==0)
      {
        BitOutputStream emptyOut = new BitOutputStream(new File(output));
        emptyOut.close();
        return "";
      }
      else
      {
        Node root = this.buildTree(this.freqTable);
        //root is built
        this.root = root;
        //inOrderChecking(this.root);
        BitOutputStream out = new BitOutputStream(new File(output));
        out.writeInt(inputLength);
        root.writeTo(out);
        Map<String,Byte> encodeTable = getCodeTable();
        Map<Byte,String> reversedTable = new HashMap<Byte,String>();
        Set<Map.Entry<String, Byte>> entrySet = encodeTable.entrySet();
        for(Map.Entry<String, Byte> entry: entrySet) 
        {  
          reversedTable.put(entry.getValue(),entry.getKey()); 
        }      
        BitInputStream reader = new BitInputStream(new File(input));
        byte[] data = reader.allBytes();
        for(int i=0;i<data.length;i++)
        {
          encodeSequence = encodeSequence + reversedTable.get(data[i]); 
        }
        for(int i=0;i<encodeSequence.length();i++)
        {
          int bit = Character.getNumericValue(encodeSequence.charAt(i));
          //System.out.print(bit);
          out.writeBit(bit);
        }
        out.close();
      }
    }
    catch(IOException e)
    {
      System.out.println("There are problems with the files");
    }
    return encodeSequence;

  }
  //*************************************** encoding ***************************************
  

  public void decoding(String input,String output)
  {
    try
    {
      int inputLength = (this.readBytes(input)).length;
      if(inputLength==0)
      {
        BitOutputStream emptyOut = new BitOutputStream(new File(output));
        emptyOut.close();
      }
      else
      {
        BitInputStream in = new BitInputStream(new File(input));
        int sizeOfFile = in.readInt();
        Node recoveredRoot = rebuildTree(in);
        this.root = recoveredRoot;
        Map<String,Byte> decodeTable = getCodeTable();
        BitOutputStream out = new BitOutputStream(new File(output));
        for(int i=0;i<sizeOfFile;i++)
        {
          String code = "";
          boolean experiment = true;
          while(experiment)
          {
            code = code + in.readBit();
            if(decodeTable.containsKey(code))
            {
              byte oneByte = decodeTable.get(code);
              out.writeByte(oneByte);
              experiment = false;
            }
          }
        }
        out.close();

      }
    }
    catch(IOException e)
    {
      System.out.println("Something went wrong!");
    }
  }


  public Node rebuildTree(BitInputStream in) throws IOException
  {
    if(in.readBit()==0)
    {
      Node decisionNode = new DecisionNode(rebuildTree(in),rebuildTree(in));
      return decisionNode;
    }
    else
    {
      Node valueNode = new ValueNode(new Integer(in.readByte()).byteValue(),0);
      return valueNode;
    }
  }
  
  
  
  //************************************** flags *******************************************
  public void CFlagOn()
  {
    Map<String,Byte> encodeTable = this.encodeTable;
    //getCodeTable();
    Set<Map.Entry<String, Byte>> entrySet = encodeTable.entrySet();
    for(Map.Entry<String, Byte> entry: entrySet) 
    {  
      System.out.println("\""+entry.getKey()+"\""+" -> "+asASCIILiteral(entry.getValue()));
    }
  }
  
  
  public void FFlagOn()
  {
    FreqComparator comparator = new FreqComparator(this.freqTable);
    TreeMap<Byte, Integer> treeMap = new TreeMap<Byte, Integer>(comparator);
    treeMap.putAll(this.freqTable);
    Set<Map.Entry<Byte, Integer>> entrySet = treeMap.entrySet();
    for(Map.Entry<Byte, Integer> entry: entrySet) 
    {  
      System.out.println(asASCIILiteral(entry.getKey())+": "+entry.getValue());
    }
  }

  public void BFlagOn(String input)
  {
    System.out.println(input); 
  }
  //************************************** flags *******************************************
  

  
  
  
  
  
  
  //************************************** helpers to build the encodeTable *******************************
  public Map<String,Byte> getCodeTable()
  {
    Map<String,Byte> encodeTable = new TreeMap<String,Byte>(new Comparator<String>()
    {
      public int compare(String s1,String s2)
      {
        if(s1.length()==s2.length())
        {
          return s1.compareTo(s2);
        }
        else
        {
          return s1.length()-s2.length();
        }

      }
    });
    this.root.putCodes(encodeTable,"");
    this.encodeTable = encodeTable;
    return encodeTable;
  }
  //************************************** helpers to build the codeTable *******************************
  
  


  
  
  
  //********************************* helpers to build the freqTable ****************************************
  private class FreqComparator implements Comparator<Byte>
  {
    Map<Byte,Integer> base;
    public FreqComparator(Map<Byte,Integer> base)
    {
      this.base = base;
    }
    public int compare(Byte a,Byte b)
    {
      if(base.get(a)>base.get(b))
      {
        return 1;
      }
      else if(base.get(a)<base.get(b))
      {
        return -1;
      }
      else
      {
        return a.compareTo(b);
      }
    }
  }


  public byte[] readBytes(String input) throws IOException
  {
    BitInputStream reader = new BitInputStream(new File(input));
    byte[] data = reader.allBytes();
    if(data.length==0)
    {
      return data;
    }
    for(int i=0;i<data.length;i++)
    {
      if(!this.freqTable.containsKey(data[i]))
      {
        this.freqTable.put(data[i],1);
      }
      else
      {
        int value = this.freqTable.get(data[i]);
        value++;
        this.freqTable.put(data[i],value);
      }
    }
    return data;
  }
  //********************************* helpers to build the freqTable ****************************************
  
  
  
  
  
  
  
  //********************************* helpers to build the root node of the Huffman tree *********************************
  public Deque<Node> buildForest(Map<Byte,Integer> freqTable)
  {
    Deque<Node> valueNodes = new LinkedList<Node>();
    FreqComparator comparator = new FreqComparator(this.freqTable);
    TreeMap<Byte, Integer> treeMap = new TreeMap<Byte, Integer>(comparator);
    treeMap.putAll(this.freqTable);
    Set<Map.Entry<Byte, Integer>> entrySet = treeMap.entrySet();
    for(Map.Entry<Byte, Integer> entry: entrySet) 
    {  
      valueNodes.add(new ValueNode(entry.getKey(),entry.getValue()));
    }
    return valueNodes;
  }

  
  
  //build tree and 
  public Node buildTree(Map<Byte,Integer> freqTable)
  {
    Deque<Node> valueNodes = buildForest(freqTable);
    Deque<Node> decisionNodes = new LinkedList<Node>();
    while(valueNodes.size() + decisionNodes.size() > 1)
    {
      Node left = removeMin(valueNodes,decisionNodes);
      Node right = removeMin(valueNodes,decisionNodes);
      decisionNodes.add(new DecisionNode(left,right));
    }
    return removeMin(valueNodes,decisionNodes);
  }
  
  
  
  //helper method for buildTree
  public static Node removeMin(Deque<Node> valueNodes,Deque<Node> decisionNodes)
  {
    if(valueNodes.isEmpty())
    {
      return decisionNodes.removeFirst();
    }
    if(decisionNodes.isEmpty())
    {
      return valueNodes.removeFirst();
    }
    int valueCount = valueNodes.getFirst().getCount();
    int decisionCount = decisionNodes.getFirst().getCount();
    return ((valueCount <= decisionCount) ? valueNodes : decisionNodes).removeFirst();
  }
  //********************************* helpers to build the root node of the Huffman tree *********************************
  
  
  
    
  
  
  
  
  //************************************************ A tester for the tree structure ***************************************
  protected void inOrderChecking(Node root)
  {
    if(root instanceof ValueNode)
    {
      System.out.println(asASCIILiteral(((ValueNode)root).value) + ((ValueNode)root).getCount());
      return;
    }
    else
    {
      inOrderChecking(((DecisionNode)root).left);
      System.out.println(((DecisionNode)root).getCount());
      inOrderChecking(((DecisionNode)root).right);
    }
  }
  //************************************************ A tester for the tree structure ***************************************
  
  
  


  
  
  
  //************************************************* Node class system ***********************************************
  public abstract class Node 
  {
    private String code;
    final protected int count;
    public Node(int count)
    {
      this.count = count;
    }
    public int compareTo(Node that)
    {
      return this.count-that.count;
      
    }
    public int getCount()
    {
      return this.count;
    }
    public final Map<Byte,String> getAllCodes()
    {
      return null;
    }
    public void setCode(String code)
    {
      this.code = code;
    }
    public String getCode()
    {
      return this.code;
    }
    public abstract void writeTo(BitOutputStream out) throws IOException;
    public abstract void putCodes(Map<String,Byte> encodeTable,String code);
  
  }
 
  public class DecisionNode extends Node
  {
    private Node left;
    private Node right;
    public DecisionNode(Node left,Node right)
    {
      super(left.getCount()+right.getCount());
      this.left = left;
      this.right = right;
    }
    public void writeTo(BitOutputStream out) throws IOException
    {
      out.writeBit(DECISION_TAG);
      left.writeTo(out);
      right.writeTo(out);
    }
    public void putCodes(Map<String,Byte> encodeTable,String code)
    {
      this.setCode(code);
      left.putCodes(encodeTable,this.getCode()+"0");
      right.putCodes(encodeTable,this.getCode()+"1");
    }
  }
  
  
  public class ValueNode extends Node
  {

    private byte value;
    public ValueNode(byte value,int freq)
    {
      super(freq);
      this.value = value;
    }
    public void writeTo(BitOutputStream out) throws IOException
    {
      out.writeBit(VALUE_TAG);
      out.writeByte(value);
    }
    public void putCodes(Map<String,Byte> encodeTable,String code)
    {
      this.setCode(code);
      encodeTable.put(this.getCode(),this.value);
    }
  }
  //************************************************* Node class system ***********************************************
  


}
