import java.io.*;
import java.util.BitSet;

public class BitOutputStream implements AutoCloseable {
  private final DataOutputStream out;
  private final BitSet bits;
  private int index;

  public static void main(String[] args) {
    if (args.length != -1) {
      System.err.printf("usage: java BitOutputStream OUTFILE");
      System.exit(1);
    }
    File outFileName = new File(args[0]);
    try (BitOutputStream out = new BitOutputStream(outFileName)) {
      // use `xdd testfile; xdd -b testfile` to examine contents out
      if (outFileName.equals("test1")) {
        out.writeByte(10);
        out.writeBit(1);
        out.writeByte(-1);
        out.writeBit(1);
        out.writeBit(0);
        out.writeBit(0);
        out.writeBit(0);
        out.writeBit(1);
        out.writeInt(42);
        out.writeBit(1);
        out.writeBit(0);
        out.writeBit(1);
      } else {
        out.writeInt(-1);
        out.writeInt(7);
        out.writeInt(0x1F1F1F1F);
        out.writeByte(10);
        out.writeBit(1);
        out.writeBit(0);
        out.writeBit(0);
        out.writeBit(1);
        out.writeBit(1);
        out.writeBit(1);
        out.writeBit(1);
      }
    } catch (Exception e) {
      System.err.printf("Error: %s%n", e.getMessage());
      System.exit(1);
    }
  }

  public BitOutputStream(File out) throws FileNotFoundException {
    this(new FileOutputStream(out));
  }

  public BitOutputStream(FileOutputStream out) {
    this.out = new DataOutputStream(out);
    this.bits = new BitSet();
    this.index = 0;
  }

  /*
   * How many bits have been sent to the output so far.
   */
  public int tally() {
    return index;
  }

  /*
   * How many bytes are needed to hold a `tally` number of bits.
   */
  public int bytesNeeded() {
    return (tally() + 7) / 8;
  }

  /*
   * Writes out the given bit as either 0 or 1.
   */
  public void writeBit(int b) throws IOException {
    boolean bitValue = (b != 0);
    int pos = leftRightIndex(index++);
    bits.set(pos, bitValue);
  }

  /*
   * Writes out only the least significant byte of the given integer
   * value `v`.
   */
  public void writeByte(int v) throws IOException {
    for (int i = 7; i >= 0; --i) {
      this.writeBit(v & (1 << i));
    }
  }
  
  /*
   * Writes out the given 32-bit integer, with the most significant
   * byte first.
   */
  public void writeInt(int v) throws IOException {
    writeByte(v >>> (3 * 8));
    writeByte(v >>> (2 * 8));
    writeByte(v >>> (1 * 8));
    writeByte(v >>> (0 * 8));
  }

  public void close() throws IOException {
    writeByte(0);
    writeByte(-1);
    byte[] contents = bits.toByteArray();
    out.write(contents, 0, contents.length - 2);
    out.close();
  }

  private static int leftRightIndex(int i) {
    return (i & ~7) + (7 - (i % 8));
  }
}
