/*
 * An interface describing the operations to support the "double-ended
 * queue" abstract data type, known as the deque and pronounced as
 * "deck". Elements can be added to and removed from the head and the
 * tail of the deque.
 *
 * In this interface, elements can be any Object type or null.
 */
public interface InstructuresDeque
{
  /*
   * Returns the number of elements in the deque.
   */
  int size();

  /*
   * Returns `true` if there are no elements in the deque.
   */
  boolean isEmpty();

  /*
   * Adds the given element to the top of the deque. (This operation
   * is known as "push" for stacks, and "unshift" for sequences.)
   *
   * Throws IllegalStateException if this method is called when the
   * deque is full.
   */
  void addTop(Object element);

  /*
   * Adds the given element to the bottom of the deque. (This
   * operation is known as "enqueue [as a verb]" for queues.)
   *
   * Throws IllegalStateException if this method is called when the
   * deque is full.
   */
  void addBottom(Object element);

  /*
   * Removes the deque's top-most element, returning its value. (This
   * operation is known as "pop" for stacks, "shift" for sequences,
   * and "dequeue [as a verb]" for queues.)
   *
   * Throws IllegalStateException if this method is called when the
   * deque is empty.
   */
  Object removeTop();

  /*
   * Removes the deque's bottom-most element, returning its value.
   *
   * Throws IllegalStateException if this method is called when the
   * deque is empty.
   */
  Object removeBottom();

  /*
   * If the deque is non-empty, returns the value of the deque's
   * top-most element without removing it; returns null
   * otherwise. (This operation is known as "peek" for stacks, and
   * "first" for sequences.)
   *
   * Unlike `removeTop`, `top` does not throw an exception.
   */
  Object top();

  /*
   * If the deque is non-empty, returns the value of the deque's
   * bottom-most element without removing it; returns null
   * otherwise. (This operation is known as "last" for sequences.)
   *
   * Unlike `removeBottom`, `bottom` does not throw an exception.
   */
  Object bottom();
}
