package oneand

/** An immutable Set with at least one element */
class NonEmptySet[A] private[oneand](raw: Set[A]) {
  assert(raw.nonEmpty)

  def contains(a: A): Boolean = raw.contains(a)

  def size: Int = raw.size

  /** Remove an element from the set. Returns None if the removed element was the only one. */
  def -(k: A): Option[NonEmptySet[A]] =
    if (raw.size > 1) Some(new NonEmptySet(raw - k))
    else if (raw.contains(k)) None
    else Some(this)

  /** Add an element to the set */
  def +(a: A): NonEmptySet[A] =
    new NonEmptySet(raw + a)

  def ++(that: NonEmptySet[A]): NonEmptySet[A] = new NonEmptySet(this.toSet ++ that.toSet)
  def ++(that: scala.collection.GenTraversableOnce[A]): NonEmptySet[A] = new NonEmptySet(this.toSet ++ that)

  def map[B](f: A => B): NonEmptySet[B] = new NonEmptySet(raw map f)

  def flatMap[B](f: A => NonEmptySet[B]): NonEmptySet[B] =
    new NonEmptySet(raw flatMap (f andThen (_.toSet)))

  def foldLeft[B](z: B)(f: (B, A) => B): B = raw.foldLeft(z)(f)
  def foldMapRight1[B](z: A => B)(f: (A, => B) => B): B = {
    val reversed = toList.reverse
    reversed.tail.foldLeft(z(reversed.head))((b,a) => f(a,b))
  }

  def toSet: Set[A] = raw
  def toList: List[A] = raw.toList

  override def toString: String = raw.mkString("NonEmptySet(", ",", ")")
}

object NonEmptySet {
  def apply[A](one: A, others: A*): NonEmptySet[A] =
    new NonEmptySet[A](Set(others: _*) + one)
}