import java.io.*;
import java.util.*;
import java.lang.*;
/**
 * PROTOTYPE
 *
 */

public class HuffmanCodes {

	//Global Variables
	private static ArrayList<String> flags = new ArrayList<String>();
	private static ArrayList<File> files = new ArrayList<File>();
	static final int DECISION_TAG = 0;
	static final int VALUE_TAG = 1;

	/**
	 * Abstract Node Class - Adapted From Piazza
	 */
	abstract class Node implements Comparable<Node> {
		int value;
		public Node(int value){
			this.value = value;
		}
		public int compareTo(Node node2) {
			return Integer.compare(this.value, node2.value);
		}

		public final Map<Byte, String> getAllCodes() {
			Map<Byte, String> codeTable = new HashMap<>();
			this.putCodes(codeTable, "");
			return codeTable;
		}
		
		public final Map<String , Byte> getAllCodes2() {
			Map<String, Byte> codeTable = new HashMap<>();
			this.putCodes2("", codeTable);
			return codeTable;
		}

		//Abstract Methods
		protected abstract void writeTo(BitOutputStream out) throws IOException;
		protected abstract void putCodes(Map<Byte, String> table, String path);
		protected abstract void putCodes2(String path,Map<String, Byte> table);
		public abstract byte next(BitInputStream in) throws IOException;
	}

	/**
	 * Value Node Class - Adapted from Piazza
	 */
	class ValueNode extends Node{
		final byte value;
		public ValueNode(byte bvalue, int value) {
			super(value);
			this.value = bvalue;
		}

		@Override
		public void writeTo(BitOutputStream out) throws IOException {
			out.writeBit(VALUE_TAG);
			out.writeByte(value);
		}

		@Override
		protected void putCodes(Map<Byte, String> table, String path) {
			table.put(value, path);
		}
		
		@Override
		protected void putCodes2(String path, Map<String, Byte> table) {
			table.put(path, value);
		}
		
		@Override
		public byte next(BitInputStream in) throws IOException {
			return value;
		}
	}

	/**
	 * Decision Node Class - Adapted from Piazza
	 */
	class DecisionNode extends Node{

		Node leftNode;
		Node rightNode;
		public void writeTo(BitOutputStream out) throws IOException {
			out.writeBit(DECISION_TAG);
			leftNode.writeTo(out);
			rightNode.writeTo(out);
		}

		DecisionNode(Node leftNode , Node rightNode){
			super(leftNode.value+rightNode.value);
			this.leftNode = leftNode;
			this.rightNode = rightNode;
		}

		@Override
		protected void putCodes(Map<Byte, String> table, String path) {
			leftNode.putCodes(table, path + "0");
			rightNode.putCodes(table, path + "1");

		}

		@Override
		public byte next(BitInputStream in) throws IOException {
			Node branch;
			if (in.readBit() == 0)
				branch = leftNode;
			else
				branch = rightNode;
			return branch.next(in);
		}

		@Override
		protected void putCodes2(String path, Map<String, Byte> table) {
			leftNode.putCodes2(path + "0" , table);
			rightNode.putCodes2(path + "1", table);
			
		}
	}


	private Node readTree(BitInputStream in) throws IOException{
		int tag = in.readBit();
		if (tag == 0)
		{
			Node left = readTree(in);
			Node right = readTree(in);
			return new DecisionNode(left, right);
		}
		else
		{
			byte value = (byte)in.readByte();
			return new ValueNode(value, 0);
		}
	}
	
	/**
	 * This code was provided by Mac 
	 */
	private static final Map<String, String> escapedLiterals = new HashMap<>();
	static{
		escapedLiterals.put("\n", "\'\\n\'");
		escapedLiterals.put("\r", "\'\\r\'");
		escapedLiterals.put("\r", "\'\\r\'");
		escapedLiterals.put("\\", "\'\\\\\'");
		escapedLiterals.put("\'", "\'\\\'\'");	
	}

	/**
	 * This code was provided by Mac
	 */
	private static String asASCIILiteral(byte asciiValue)
	{
		String convertedStr = new String(new byte[] {asciiValue});
		String literal = escapedLiterals.get(convertedStr);
		if (literal == null){
			literal = String.format("\'%s\'", convertedStr);
		}
		return literal;
	}

	/**
	 * Build Tree Method using a Priority Queue
	 */
	private Node buildTree(Map<Byte, Integer> freq){
		PriorityQueue<Node> bTree = buildForest(freq);
		if(bTree.size() == 1)
			bTree.add(bTree.peek());
		while (bTree.size() > 1){
			DecisionNode n = new DecisionNode(bTree.remove(), bTree.remove());
			bTree.add(n);
		}
		return bTree.remove();
	}

	/**
	 * BuildForest Method Using a Priority Queue
	 */
	private PriorityQueue<Node> buildForest(Map<Byte, Integer> freq){
		PriorityQueue<Node> bForest = new PriorityQueue<>(freq.size());
		for (Map.Entry<Byte, Integer> entry: freq.entrySet()){
			bForest.add(new ValueNode(entry.getKey(), entry.getValue()));
		}
		return bForest;
	}

	/**
	 * The encode method counts the frequency of each byte and then builds a binary tree.
	 * The main purpose of the method is to encode what originally was the input.
	 */
	private void encode(BitInputStream in, BitOutputStream out) throws IOException
	{
		byte[] data = in.allBytes();
		final Map<Byte, Integer> freq = freqCount(data);
		freqPrint(freq);
		Node HuffTree = buildTree(freq);

		int lengthInput = data.length;
		out.writeInt(lengthInput);
		HuffTree.writeTo(out);
		int headBits = out.tally();

		Map<Byte, String> codeTable = HuffTree.getAllCodes();
		CodePrint(codeTable);
    System.out.println();
		for (int i = 0; i < data.length; ++i)
		{
      //System.out.println("Here");
			String codes = codeTable.get(data[i]);
			for (int idx = 0; idx < codes.length(); ++idx){
				if (codes.charAt(idx) == '1'){
          //System.out.print("1");
					out.writeBit(1);
        }
				else
        {
          //System.out.print("0");
					out.writeBit(0);
        }
			}
		}
    out.close();
		SequencePrint(data, codeTable);
		int encodingBits = out.tally() - headBits;
		int outputLength = out.bytesNeeded();
		double saved = outputLength / (double)lengthInput;
		System.out.printf(" input: %d bytes [%d bits]%n",lengthInput, lengthInput * 8);
		System.out.printf("output: %d bytes [header: %d bits; encoding: %d bits]%n", outputLength, headBits, encodingBits);
		System.out.printf("output/input size: %.4f%%%n", saved * 100.0);
	}

	/**
	 * "Reverse Engineering" method
	 *  Reads header in your encoded file, construct the tree and/or Huffman 
	 *  table that relates encodings to symbols/characters. 
	 *	Reads bits from the rest of the file one bit at a time, and keep reading bits until
	 *  you can properly access your data structure with the encoding mappings in it.
	 */
	private void decode(BitInputStream in, BitOutputStream out) throws IOException
	{
		
		int originalSize = in.readInt();
		Node tree = readTree(in);

		Map<String , Byte> codeTable = tree.getAllCodes2();
		
		//System.out.printf("original size: %d%n", originalSize);
		for (int i = 0; i < originalSize; ++i)
		{
			boolean finish = true;
			String code = "";
			while(finish){
				code = code + in.readBit();
				if(codeTable.containsKey(code))
				{
					byte ch = codeTable.get(code);
					out.writeByte(ch);
					finish = false;
				}
			}
		}
    out.close();
		
		//int numDecoded = 0;
		//while(numDecoded < originalSize)
		//{
			//initialize empty string str
			//String str = null;
			//while(!codeTable.containsKey(str))
			//{
				//readBit
				//int bitR = in.readBit();
				//String str2 = Integer.toString(bitR);
				//str = str + str2.substring(str2.length() - 1);
				//concatenate bit to str
				
				
			//}
			//add codeTable.get(str) to file
			//out.writeByte(codeTable.get(str).byteValue());
			
			//increment numDecoded
		   // numDecoded++;
		//}
		
	}

	/**
	 * Method that prints the frequencies chart which represents how often a letter is represented
	 */
	private void freqPrint(final Map<Byte, Integer> freq)
	{
		if (flags.contains("-F"))
		{
			System.out.printf("FREQUENCY TABLE%n");
			List<Byte> toSortList = new ArrayList<Byte>(freq.keySet());
			Collections.sort(toSortList);
			Collections.sort(toSortList, new Comparator<Byte>()
					{
				public int compare(Byte firstByte, Byte secondByte)
				{
					Integer frequencies = Integer.compare(freq.get(firstByte), freq.get(secondByte));
					return frequencies;
				}
					});

			for (Byte value: toSortList){
				int count = freq.get(value);
				System.out.printf("%s: %s%n", asASCIILiteral(value), count);
			}
		}
		else
			return;
	}

	/**
	 * Using a HashMap that takes count of the frequencies
	 */
	private Map<Byte, Integer> freqCount (byte[] data)
	{
		Map<Byte, Integer> freqMap = new HashMap<>();
		for (int i = 0; i < data.length; ++i){
			Integer val = freqMap.get(data[i]);
			if (val == null){
				val = 0;
			}
			freqMap.put(data[i], val + 1);
		}	
		return freqMap;
	}

	/**
	 * Method that prints the code chart which has the binary representation of each char/letter
	 */
	private void CodePrint(final Map<Byte, String> codeTable)
	{
		if (flags.contains("-C")){
			System.out.printf("CODES%n");
			List<Byte> toSortList = new ArrayList<Byte>(codeTable.keySet());
			Collections.sort(toSortList, new Comparator<Byte>(){
				public int compare(Byte firstB, Byte secondB){
					String firstCode = codeTable.get(firstB);
					String secondCode = codeTable.get(secondB);
					int lengthFirst = Integer.compare(firstCode.length(), secondCode.length());
					if (lengthFirst == 0){
						return firstCode.compareTo(secondCode);
					} 
					return lengthFirst;
				}
			});
			for (Byte value: toSortList){
				String code = codeTable.get(value);
				System.out.printf("\"%s\" -> %s%n", code, asASCIILiteral(value));
			}	
		}
		else
			return;
	}

	/**
	 * Method that prints the encoded sequence
	 */
	private void SequencePrint(byte[] data, Map<Byte, String> codeTable)
	{
		if (flags.contains("-B")){
			System.out.printf("ENCODED SEQUENCE%n");
			for (int i = 0; i < data.length; ++i){
				String code = codeTable.get(data[i]);
				System.out.print(code);
			}
			System.out.println();
		}
		else
			return;
	}

	/**
	 * Modify the file passed in by the argument using the classes provided BitInputStream and BitOutputStream
	 * Print an error if these files do not exist
	 */
	public void fileModification()
	{
		try{ 
			BitInputStream in = new BitInputStream(files.get(0));
			BitOutputStream out = new BitOutputStream(files.get(1));
			if (flags.contains("-e"))
				encode(in, out);
			if(flags.contains("-d"))
				decode(in, out);
		}
		catch (Exception e)
		{
			System.err.printf("Error: %s%n", e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Store the flags in an Arraylist of flags , and store the files in an Arraylist of files
	 * By storing the flags into an ArrayList the program will know what methods to execute 
	 */
	public static void main(String[] args){
    /*if(args[0].equals("-d"))
    {
      try
      {
      BitInputStream in = new BitInputStream(new File(args[1]));
			BitOutputStream out = new BitOutputStream(new File(args[2]));
      HuffmanCodes hw = new HuffmanCodes();
      hw.decode(in,out);



      }
    }*/
		/*else
    {*/
    for(int i = 0; i < args.length ; i++){
			if(args[i].contains("-e") || args[i].contains("-d") || args[i].contains("-F") || args[i].contains("-C") || args[i].contains("-B") )
				flags.add(args[i]);
			if(!args[i].contains("-e") && !args[i].contains("-d") && !args[i].contains("-F") && !args[i].contains("-C") && !args[i].contains("-B") )
				files.add(new File(args[i]));
		}
		HuffmanCodes application = new HuffmanCodes();
		application.fileModification();

	  }	
}


