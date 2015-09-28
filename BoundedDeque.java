import java.util.LinkedList;
public class BoundedDeque implements InstructuresDeque
{
  private Object[] buff;
  private int capacity, size;
  private int front, rear;

  public BoundedDeque(int capacity) throws IllegalArgumentException
  {
    if(capacity<=0)
    {
      IllegalArgumentException e = new IllegalArgumentException("capacity must be positive");
      throw e;
    }
    this.capacity= capacity;
    buff = new Object[capacity];
    front = rear = -1;
    size = 0;
  }
  public int size()
  {
    return this.size;
  }
  
  public boolean isEmpty()
  {
    if(size==0)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  public void addTop(Object element) throws IllegalStateException
  {
    if(this.rear==-1&&this.front==-1)
    {
      this.rear = this.front = 0;
    }
    else
    {
      this.front = (this.front+this.capacity-1)%(this.capacity);
      if(this.front == this.rear)
      {
        IllegalStateException e = new IllegalStateException();
        throw e;
      }
    
    }
    buff[front] = element;
    this.size++;
    
  }
  
  public String toString()
  {
    String output = "";
    boolean loop = true;
    int counter = this.front;
    while(loop)
    {
      if(this.buff[counter]!=null)
      {
        output = output + "  [" + counter + "]" + buff[counter];
      }
      if(counter==this.rear)
      {
        loop = false;
      }
      counter = (counter+1)%capacity;
      
    }
    return output;
    
  }
  public void addBottom(Object element) throws IllegalStateException
  {
    if(this.rear==-1&&this.front==-1)
    {
      this.rear = this.front = 0;
    }
    else
    {
      this.rear = (this.rear+1)%(this.capacity);
      if(this.front == this.rear)
      {
        IllegalStateException e = new IllegalStateException();
        throw e;
      }
    
    }
    buff[rear] = element;
    this.size++;
  }
  
  public Object removeTop()
  {
    if(this.size==0)
    {
      IllegalStateException e = new IllegalStateException();
      throw e;
    }
    else if(this.size==1)
    {
      Object temp = this.buff[front];
      this.buff[front] = null;
      this.front = this.rear = -1;
      this.size--;
      return temp;
    }
    else
    {
      Object temp = this.buff[front];
      this.buff[this.front] = null;
      this.front = (this.front+1)%capacity;
      this.size--;
      return temp;
    }
  }
  
  public Object removeBottom()
  {
    if(this.size==0)
    {
      IllegalStateException e = new IllegalStateException();
      throw e;
    }
    else if(this.size==1)
    {
      Object temp = this.buff[rear];
      this.buff[rear] = null;
      this.front = this.rear = -1;
      this.size--;
      return temp;
    }
    else
    {
      Object temp = this.buff[rear];
      this.buff[this.rear] = null;
      this.rear = (this.rear+this.capacity-1)%capacity;
      this.size--;
      return temp;
    }
  }
  
  public Object top()
  {
    if(this.front!=-1)
    {
      return this.buff[front];
    }
    else
    {
      return null;
    }
  }
  
  public Object bottom()
  {
    if(this.rear!=-1)
    {
      return this.buff[rear];
    }
    else
    {
      return null;
    }
  }
  
  /*public static void main(String[] args)
  {
    BoundedDeque a = new BoundedDeque(10);
    a.addTop("1");
    a.addTop("2");
    a.addBottom("3");
    a.addTop("5");
    a.addBottom("4");
    a.removeTop();
    a.removeBottom();
    System.out.println(a);
  }*/
  
    
}
