import java.io.*;
import java.util.*;
import java.lang.*;
public class ListFiles
{
  ArrayList<FileInfo> list;
  public ListFiles()
  {
    this.list = new ArrayList<FileInfo>();
  }
  public static void main(String[] args)
  {
    /*try
        {
          toList.sizeSort(args[1]);
        }
        catch(IOException e)
        {
          System.err.printf("Error: %s%n",e.getMessage());
          System.exit(1);
        }*/
    ListFiles toList = new ListFiles();
    
    if(args[0].equals("-size"))
    {
      if(args[1].equals("-gather"))
      {
        String[] dirs = new String[args.length-2];
        for(int i=0;i<dirs.length;i++)
        {
          dirs[i] = args[2+i];
        }
        try
        {
          toList.sizeSort(dirs);
        }
        catch(IOException e)
        {
          System.err.printf("Error: %s%n",e.getMessage());
          System.exit(1);
        }
      }
      else
      {
        String[] dirs = new String[args.length-1];
        for(int i=0;i<dirs.length;i++)
        {
          dirs[i] = args[1+i];
        }
        try
        {
          for(int i=0;i<dirs.length;i++)
          {
            toList.sizeSort(dirs[i]);
            toList.list = new ArrayList<FileInfo>();
            System.out.println("-----------------");
          }
        }
        catch(IOException e)
        {
          System.err.printf("Error: %s%n",e.getMessage());
          System.exit(1);
        }
        
      }
    }
    else if(args[0].equals("-gather"))
    {
      String[] dirs = new String[args.length-1];
      for(int i=0;i<dirs.length;i++)
      {
        dirs[i] = args[1+i];
      }
      System.out.println(args.length);

      try
        {
         
          toList.nameSort(dirs);
          
        }
        catch(IOException e)
        {
          System.err.printf("Error: %s%n",e.getMessage());
          System.exit(1);
        }

    }
    else
    {
      String[] dirs = new String[args.length];
      System.out.println(args.length);
      for(int i=0;i<dirs.length;i++)
      {
        dirs[i] = args[i];
      }
      try
        {
          for(int i=0;i<dirs.length;i++)
          {
            toList.nameSort(dirs[i]);
            toList.list = new ArrayList<FileInfo>();
          }
        }
        catch(IOException e)
        {
          System.err.printf("Error: %s%n",e.getMessage());
          System.exit(1);
        }
      
    } 
    
  }
  public void nameSort(String dir) throws IOException
  {
    File[] fileList = listFiles(new File(dir));
    for(int i=0;i<fileList.length;i++)
    {
      if(fileList[i].isFile())
      {
        this.list.add(new FileInfo(fileList[i].getName(),fileList[i].length()));
      }
    }
    Collections.sort(this.list);
    for(int i=0;i<this.list.size();i++)
    {
      System.out.println(this.list.get(i));
    }
    
  }
  
  public void nameSort(String[] dirs) throws IOException
  {
    for(int i=0;i<dirs.length;i++)
    {
      File[] files = listFiles(new File(dirs[i]));
      for(int z=0;z<files.length;z++)
      {
        if(files[z].isFile())
        {
          this.list.add(new FileInfo(files[z].getName(),files[z].length()));
        }
      }
    }
    Collections.sort(this.list);
    for(int i=0;i<this.list.size();i++)
    {
      System.out.println(this.list.get(i));
    }
  }
  public void sizeSort(String dir) throws IOException
  {
    File[] fileList = listFiles(new File(dir));
    for(int i=0;i<fileList.length;i++)
    {
      if(fileList[i].isFile())
      {
        this.list.add(new FileInfo(fileList[i].getName(),fileList[i].length()));
      }
    }
    Collections.sort(this.list);
    for(int i=0;i<this.list.size();i++)
    {
      long max = this.list.get(i).getFileSize();
      int maxIndex = i;
      for(int j=i+1;j<this.list.size();j++)
      {
        if(this.list.get(j).getFileSize()>max)
        {
          max = this.list.get(j).getFileSize();
          maxIndex = j;
        }
      }
      long currLong = this.list.get(i).getFileSize();
      this.list.get(i).setFileSize(max);
      this.list.get(maxIndex).setFileSize(currLong);
      String currString = this.list.get(i).getFileName();
      this.list.get(i).setFileName(this.list.get(maxIndex).getFileName());
      this.list.get(maxIndex).setFileName(currString);
    }
    for(int i=0;i<this.list.size();i++)
    {
      System.out.println(this.list.get(i));
    }
    
  }
  
  public void sizeSort(String[] dirs) throws IOException
  {
    for(int i=0;i<dirs.length;i++)
    {
      File[] files = listFiles(new File(dirs[i]));
      for(int z=0;z<files.length;z++)
      {
        if(files[z].isFile())
        {
          this.list.add(new FileInfo(files[z].getName(),files[z].length()));
        }
      }
    }
    Collections.sort(this.list);
    for(int i=0;i<this.list.size();i++)
    {
      long max = this.list.get(i).getFileSize();
      int maxIndex = i;
      for(int j=i+1;j<this.list.size();j++)
      {
        if(this.list.get(j).getFileSize()>max)
        {
          max = this.list.get(j).getFileSize();
          maxIndex = j;
        }
      }
      long currLong = this.list.get(i).getFileSize();
      this.list.get(i).setFileSize(max);
      this.list.get(maxIndex).setFileSize(currLong);
      String currString = this.list.get(i).getFileName();
      this.list.get(i).setFileName(this.list.get(maxIndex).getFileName());
      this.list.get(maxIndex).setFileName(currString);
    }
    for(int i=0;i<this.list.size();i++)
    {
      System.out.println(this.list.get(i));
    }
    
  }
  
  private String[] listFileNames(File directory) throws IOException 
  {
    if(!directory.exists()||!directory.isDirectory())
    {
      throw new IOException(directory + "is not a directory");
    }
    else 
    {
      String[] fileNames = directory.list();
      return fileNames;
    }
  }
  
  private File[] listFiles(File directory) throws IOException 
  {
    if(!directory.exists()||!directory.isDirectory())
    {
      throw new IOException(directory + "is not a directory");
    }
    else 
    {
      File[] files = directory.listFiles();
      return files;
    }
  }
  
  
  
  private int[] numSort(int[] unsort)
  {
    for(int i=0;i<unsort.length;i++)
    {
      int min = unsort[i];
      int minIndex = i;
      for(int j=i;j<unsort.length;j++)
      {
        if(unsort[j]<min)
        {
          min = unsort[j];
          minIndex = j;
        }
      }
      int curr = unsort[i];
      unsort[i] = min;
      unsort[minIndex] = curr;
    }
    return unsort;
  }
  
  
  private class FileInfo implements Comparable<FileInfo>
  {
    private String fileName;
    private long fileSize;
    public FileInfo(String name, long size)
    {
      this.fileName = name;
      this.fileSize = size;
    }
    public String getFileName()
    {
      return this.fileName;
    }
    public long getFileSize()
    {
      return this.fileSize;
    }
    public void setFileName(String newName)
    {
      this.fileName = newName;
    }
    public void setFileSize(long newSize)
    {
      this.fileSize = newSize;
    }
    public String toString()
    {
      String output = String.format("%12d  %s", this.fileSize, this.fileName);
      return output;
    }
    @Override
    public int compareTo(FileInfo that)
    {
      return this.getFileName().compareTo(that.getFileName());
    }
  }
}


