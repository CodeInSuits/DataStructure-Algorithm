import java.util.LinkedList;
public class UnboundedDeque implements InstructuresDeque
{
  private int size = 0;
  private Node sentinel;
  
  public class Node
  {
    private Object element;
    private Node prev;
    private Node next;
    
    public Node(Object e)
    {
      this.element = e;
      size++;
      this.prev = this;
      this.next = this;
    }
    /*public String toString()
    {
      if(this.element==null)
      {
        return "";
      }
      else
      {
        return " "+this.element;
      }
    }*/
  }
  
  public UnboundedDeque()
  {
    this.sentinel = new Node(null);  
  }

  public int size()
  {
    return this.size-1;
  }
 
  public boolean isEmpty()
  {
    if(this.size-1==0)
    {
      return true;
    }
    else
    {
      return false;
    }
    
  }
  

  public void addTop(Object element)
  {
    //if(this.sentinel.next!=this.sentinel)
    //{
      Node newNode = new Node(element);
      Node xnd = this.sentinel.next;
      newNode.prev = this.sentinel;
      newNode.next = xnd;
      xnd.prev = newNode;
      this.sentinel.next = newNode; 
    //}
    /*else
    {
      Node newNode = new Node(element);
      this.sentinel.next = newNode;
      this.sentinel.prev = newNode;
      newNode.prev = this.sentinel;
      newNode.next = this.sentinel;
    }*/
  }
  public void addBottom(Object element)
  {
    Node newNode = new Node(element);
    Node xnd = this.sentinel.prev;
    newNode.prev = xnd;
    newNode.next = this.sentinel;
    xnd.next = newNode;
    this.sentinel.prev = newNode;
  }
  /*public String toString()
  {
    String output = "";
    Node current = this.sentinel.next;
    while(current!=this.sentinel)
    {
      output = output + " " + current;
      current = current.next;
    }
    return output;
  }*/
  
  public Object removeTop() throws IllegalStateException
  {
    if(this.size==1)
    {
      IllegalStateException e = new IllegalStateException();
      throw e;
    }
    else
    {
      Node xnd = this.sentinel.next;
      this.sentinel.next = xnd.next;
      xnd.next.prev = this.sentinel;
      this.size--;
      return xnd.element;
    }
  }
  public Object removeBottom() throws IllegalStateException
  {
    if(this.size==1)
    {
      IllegalStateException e = new IllegalStateException();
      throw e;
    }
    else
    {
      Node xnd = this.sentinel.prev;
      this.sentinel.prev = xnd.prev;
      xnd.prev.next = this.sentinel;
      this.size--;
      return xnd.element;
    }
  }
  public Object top()
  {
    if(this.size>1)
    {
      return this.sentinel.next.element;
    }
    else
    {
      return null;
    }
  }
  public Object bottom()
  {
    if(this.size>1)
    {
      return this.sentinel.prev.element;
    }
    else
    {
      return null;
    }
  }
  public static void main(String[] args)
  {
    //UnboundedDeque a = new UnboundedDeque();
    //System.out.println(a.size());
    //a.addTop("1231");
    //a.addTop("123123");
    //a.addBottom("12312asdasdasd");
    //a.addBottom("121eadasderqfafasf");
    //a.addTop("asdasscpioiqwe");
    //System.out.println(a.bottom());
    //System.out.println(a.top());
    //System.out.println(a.removeTop());
    //System.out.println(a.removeBottom());
    
    //a.removeBottom();
    //System.out.println(a);
    //System.out.println(a.size());
   
  }
  
  

}
