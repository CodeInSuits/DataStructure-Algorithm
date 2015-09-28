import java.util.*;
import java.io.*;
//import java.lang.*;  habit..

public class HuffmanCodes 
{
	private static final String MODE_ENCODE = "-e";
	private static final String MODE_DECODE = "-d";
	private static final List<String> validOptions = Arrays.asList("-F", "-C", "-B");
	private static final Map<String, String> escapedLiterals = new HashMap<String, String>();

	static 
	{
		escapedLiterals.put("\n", "\\n");
		escapedLiterals.put("\r", "\\r");
		escapedLiterals.put("\t", "\\t");
		escapedLiterals.put("\\", "\\\\");
		escapedLiterals.put("\'", "\\\'");
		escapedLiterals.put("\"", "\\\"");
	}


	private static int arg_length;
	private static String mode = null;
	private static String input = null;
	private static String output = null;
	private static List<String> options = null;

	private static byte[] data;
	private static List<Byte> bytes;
	private static Map<Byte, Integer> byteCount = new HashMap<Byte, Integer>();
	private static Map<Byte, String> byteEncoded = new HashMap<Byte, String>();
	private static Map<String, Byte> byteDecoded = new HashMap<String, Byte>();

	private static abstract class Node 
	{
		protected int freq;
		protected int indicator;
		protected Node left;
		protected Node right;
		protected byte value;
		protected int count;

		protected int traverse (Node tree, byte value, StringBuilder str)
		{
			if (tree!=null)
			{
				if (tree.indicator == 0)
				{
					if (tree.checkValue(value))
					{
						return -1;
					}
					else 
					{
						return 0;
					}
				}
				else
				{
					int l = traverse(tree.left, value, str);
					if (l == -1)
					{
						str.append(0);
						return -1;
					}
					int r = traverse(tree.right, value, str);
					if (r == -1)
					{
						str.append(1);
						return -1;
					}
				}
			}
			return -1111;
		}


		protected Node rebuild (Node tree, BitInputStream bitIn)
		{
			++count;
			try 
			{			
				if (bitIn.readBit() == 1) // if it's 1 i.e. decision node
				{
					if (tree == null && count!=1)
						tree = new DecisionNode(null, null, 0); //creating a new decision node
					tree.left = rebuild(tree.left, bitIn);
					tree.right = rebuild(tree.right, bitIn);
				}
				else //if it's 0 i.e. value node
				{
					if (tree == null)
					{
						byte value = new Integer(bitIn.readByte()).byteValue();
						count+=8;
						bytes.add(value);
						tree = new ValueNode(value);
					}
				}
			}
			catch (Exception e)
			{
				//caught
			}

			return tree;
		}

		protected abstract void writeTo(BitOutputStream bitOut) throws IOException;

		protected abstract boolean checkValue(byte value);
	}

	private static class DecisionNode extends Node
	{
		public DecisionNode(Node left, Node right, int freq)
		{
			indicator = 1; //1 for DecisionNode
			this.freq = freq;
			this.left = left;
			this.right = right;
		}

		public void writeTo (BitOutputStream bitOut) throws IOException
		{
			bitOut.writeBit(1);
			left.writeTo(bitOut);
			right.writeTo(bitOut);
		}

		protected boolean checkValue (byte value)
		{
			return false; //always false
		}
	}

	private static class ValueNode extends Node
	{
		byte value;

		public ValueNode(byte value)
		{
			indicator = 0; //0 for ValueNode
			this.value = value;
			this.freq = freq;
			this.left = null;
			this.right = null;
		}

		public void writeTo (BitOutputStream bitOut) throws IOException
		{
			bitOut.writeBit(0);
			bitOut.writeByte(value);
		}

		protected boolean checkValue (byte value)
		{
			return (this.value == value);
		}


	}

	//checks whether the command line arguments are valid
	private static boolean checkValidity(boolean option_value)
	{
		boolean result = true; //all arguments valid by default

		//check if mode is valid
		if (!mode.equals(MODE_ENCODE) && !mode.equals(MODE_DECODE))
			result = false;

		//check if input is valid
		File input_file = new File(input);
		if (!input_file.isFile())
			result = false;

		//if option is provided
		if (option_value == true)
		{
			for (int i=0; i<options.size(); i++)
			{
				//check if option is valid
				if (!validOptions.contains(options.get(i)))
					result = false;
			}			
		}

		return result;
	}

	//returns the node which has the minimum frequency by comparing the top nodes of valueNodes and decisionNodes
	//if either of them is empty, it'll just return the top nodes of the other list because they're already sorted
	//if top nodes' frequency is equal, the top node of valueNodes is returned
	private static Node removeMin(Deque<Node> valueNodes, Deque<Node> decisionNodes)
	{
		try 
		{
			if (valueNodes.isEmpty())
				return decisionNodes.removeFirst();
			if (decisionNodes.isEmpty())
				return valueNodes.removeFirst();

			Node valueNodesHead = valueNodes.getFirst(), decisionNodesHead = decisionNodes.getFirst();
			
			return ((valueNodesHead.freq <= decisionNodesHead.freq) ? valueNodes : decisionNodes).removeFirst();
		}
		catch (Exception e)
		{
			//caught
		}
		return null;
	}

	private static Deque<Node> buildForest()
	{
		Deque<Node> valueNodes = new LinkedList<>();

	 	List<Byte> keys = new ArrayList<Byte>(bytes);
		List<Integer> values = new ArrayList<Integer>();
		for (int i=0; i<keys.size(); i++)
		{
			values.add(byteCount.get(keys.get(i)));
		}

		Iterator<Byte> keyIter = keys.iterator();
		Iterator<Integer> valueIter = values.iterator();

		while (keyIter.hasNext())
		{
			byte key = keyIter.next();
			ValueNode node = new ValueNode(key);
			node.freq = valueIter.next();
			valueNodes.addFirst(node);
		}

		return valueNodes;

	}

	private static Node buildTree() 
	{
		Deque<Node> valueNodes = buildForest();
		Deque<Node> decisionNodes = new LinkedList<>();

		while (valueNodes.size() + decisionNodes.size() > 1)
		{
			Node left = removeMin(valueNodes, decisionNodes);
			Node right = removeMin(valueNodes, decisionNodes);
			decisionNodes.add(new DecisionNode(left, right, left.freq + right.freq));
		}

		return removeMin(valueNodes, decisionNodes);
	}

	private static void encode()
	{
		File output_file = new File(output);

		if (data.length != 0)
		{
			sortByFreq();

			Node tree = buildTree();

			try (BitOutputStream bitOut = new BitOutputStream(output_file))
			{
				bitOut.writeInt(data.length);
				tree.writeTo(bitOut); //writing out the tree

		 		List<Byte> keys = new ArrayList<Byte>(byteCount.keySet());
		 		Iterator<Byte> keyIter = keys.iterator();

		 		while (keyIter.hasNext())
		 		{
		 			Byte key = keyIter.next();
		 			StringBuilder code = new StringBuilder();
		 			tree.traverse(tree, key, code);
		 			String code_string = code.reverse().toString();
		 			byteEncoded.put(key, code_string);
		 			//System.out.printf("%n%s has the code : %s", asASCIILiteral(key), code);	
		 		}

		 		for (int i=0; i<data.length; i++)
		 		{
		 			Byte ch = data[i];
		 			int code = Integer.parseInt(byteEncoded.get(ch));
		 			bitOut.writeBit(code);
		 		}

			}
			catch (Exception e)
			{
				System.err.printf(e.getMessage());
			}
			finally
			{
				return;
			}
		}	
		else
		{
			try (BitOutputStream bitOut = new BitOutputStream(output_file))
			{
				//do nothing
			}
			catch (Exception e)
			{	
			}
		}
	}

	private static Node recoverTree()
	{
		int output_size = 0;
		File input_file = new File(input);
		String output_size_string = new String();
		Node tree = new DecisionNode(null, null, 0);

		try (BitInputStream bitInput = new BitInputStream(input_file)) 
		{
			output_size = bitInput.readInt();
			//System.out.printf("%nGetting size %d ", output_size);
			tree = tree.rebuild(tree, bitInput);

			Iterator<Byte> keyIter = bytes.iterator();

	 		while (keyIter.hasNext())
	 		{
	 			Byte key = keyIter.next();
	 			StringBuilder code = new StringBuilder();
	 			tree.traverse(tree, key, code);
	 			String code_string = code.reverse().toString();
	 			byteDecoded.put(code_string, key);
	 			System.out.printf("%n%s has the code : %s", asASCIILiteral(key), code_string);	
	 		}
		}
		catch (Exception e)
		{
			System.err.printf(e.getMessage());
		}
		finally 
		{
			return tree;
		}
	}

	private static void decode()
	{
		bytes = new ArrayList<Byte>();

		Node tree = recoverTree();
		File output_file = new File(output);
		File input_file = new File(input);



		if (data.length != 0)
		{
			try (BitInputStream bitIn = new BitInputStream(input_file); 
					BitOutputStream bitOut = new BitOutputStream(output_file))
			{
				int iterr = bitIn.readInt();
				for (int i=0; i<tree.count+1; i++)
					bitIn.readBit();
				StringBuilder code = new StringBuilder();
				for (int i=0; i<iterr; )
				{
					code.append(bitIn.readBit());
					String code_string = code.toString();
					if (byteDecoded.containsKey(code_string))
					{	
						Byte value = byteDecoded.get(code.toString());
						bitOut.writeByte(value);
						code.setLength(0);
						i++;
					}
				}
				
			}
			catch (Exception e)
			{	
			}
		}
		else
		{
			try (BitOutputStream bitOut = new BitOutputStream(output_file))
			{
				//do nothing
			}
			catch (Exception e)
			{	
			}
		}
	}

	private static String asASCIILiteral(byte asciiValue)
	{
		String str = new String(new byte[] {asciiValue});
		if (escapedLiterals.containsKey(str))
			str = escapedLiterals.get(str);
		return String.format("\'%s\'", str);
	}

	private static void readEncodedFile()
	{
		File input_file = new File(input);
		try (BitInputStream bitInput = new BitInputStream(input_file)) 
		{
			data = bitInput.allBytes();
		}
		catch (Exception e)
		{
			System.err.printf(e.getMessage());
		}
	}

	private static void readDecodedFile()
	{
		File input_file = new File(input);
		try (BitInputStream bitInput = new BitInputStream(input_file)) 
		{
			data = bitInput.allBytes();
			for (int i=0; i<data.length; i++)
			{
				Integer value = 0;
				if (byteCount.containsKey(data[i]))
					value = byteCount.get(data[i]);
				byteCount.put(data[i], 1 + value);
			}
		}
		catch (Exception e)
		{
			System.err.printf(e.getMessage());
		}
	}

	private static void freqOpt()
	{
		int j = 0;
		sortByFreq();
		System.out.println("FREQUENCY TABLE");
		while (j<bytes.size())
		{
			System.out.printf("%s: %d%n", asASCIILiteral(bytes.get(j)), byteCount.get(bytes.get(j)));
			j++;
		}
	}

	//after this method, "bytes" arraylist will be sorted in decreasing order of its frequency
	private static void sortByFreq()
	{
		bytes = new ArrayList<Byte>(byteCount.keySet());
		Collections.sort(bytes);
		Collections.sort(bytes, new Comparator<Byte>()
			{
				public int compare(Byte b1, Byte b2)
				{
					return (byteCount.get(b2)).compareTo(byteCount.get(b1));
				}
			});
	}

	private static void codesOpt()
	{
 		Iterator<Byte> bytesIter = bytes.iterator();
 		System.out.println("CODES");
 		while (bytesIter.hasNext())
 		{
 			Byte key = bytesIter.next();
 			System.out.println("\"" + byteEncoded.get(key) + "\" -> " + asASCIILiteral(key));	
 		}
	}

	private static void encodedSeqOpt()
	{
		System.out.println("ENCODED SEQUENCE");
		for (int i=0; i<data.length; i++)
		{
			Byte ch = data[i];
			//String character = asASCIILiteral(ch);
			String code = (byteEncoded.get(ch));
			//System.out.print("{" + character + "}");
			System.out.print(code);
		}
	}

	private static void displayStats()
	{
		try (BitInputStream tempBitIn = new BitInputStream(new File(output)))
		{
			System.out.printf("%nInput: %d bytes [%d bits]", data.length, data.length * 8);
			int output_length = tempBitIn.allBytes().length;
			System.out.printf("%nOutput: %d bytes [header: x bits; encoding: x bits]%n", output_length);
		}
		catch (Exception e)
		{
			//caught;
		}
	}

	public static void main(String[] args)
	{
		arg_length = args.length;

		if (arg_length < 3)
		{
			System.err.printf("%nUsage: java HuffmanCodes MODE [OPTIONS...] IN OUT");
			System.err.printf("%nMODE is one of: %n  -e   Encodes file IN to file OUT %n  -d   Decodes file IN to file OUT %nOPTIONS are zero or more of:%n  -F   show the frequencies of each byte %n  -C   show the codes for each byte%n  -B   show the encoded sequence in binary%n%n");
		}
		else if (arg_length == 3) 
		{
			//first arg must be MODE, last two must be input and output
			mode = args[0];
			input = args[1];
			output = args[2];
			if (!checkValidity(false)) //false for whether or not options were entered
			{
				System.err.printf("One or more command line argument(s) are invalid.");
			}
			else
			{
				if (mode.equals(MODE_ENCODE))
				{
					readDecodedFile();
					encode();
				}
				else if (mode.equals(MODE_DECODE))
				{
					readEncodedFile();
					decode();
				}
				displayStats();
			}
		}
		else if (arg_length > 3 && arg_length < 7)
		{
			//first arg must be MODE, last two must be input and output and the rest can be options
			mode = args[0];
			input = args[arg_length-2];
			output = args[arg_length-1];
			options = new ArrayList<String>(arg_length - 3);
			for (int i=1, j=0; i<arg_length-2; i++, j++)
			{
				options.add(args[i]);
			}
			if (!checkValidity(true)) //true because one or more options were entered
			{
				System.err.printf("One or more command line argument(s) are invalid.");
			}
			else
			{
				if (mode.equals(MODE_ENCODE))
				{
					readDecodedFile();
					encode();
				}
				else if (mode.equals(MODE_DECODE))
				{
					readEncodedFile();
					decode();
				}
				
				//if one of the options is -F
				if (options.contains(validOptions.get(0)))
				{
					//System.out.printf("%nYup, it has F option");
					freqOpt();
				}
				//if one of the options is -C
				if (options.contains(validOptions.get(1)))
				{
					codesOpt();
				}
				//if one of the options is -B
				if (options.contains(validOptions.get(2)))
				{
					encodedSeqOpt();
				}

				displayStats();
			}
		}
		else 
		{
			System.err.printf("%nUsage: java HuffmanCodes MODE [OPTIONS...] IN OUT");
			System.err.printf("%nMODE is one of: %n  -e   Encodes file IN to file OUT %n  -d   Decodes file IN to file OUT %nOPTIONS are zero or more of:%n  -F   show the frequencies of each byte %n  -C   show the codes for each byte%n  -B   show the encoded sequence in binary%n%n");
		}
	}
}
