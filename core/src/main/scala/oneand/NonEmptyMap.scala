package oneand

/** An immutable Map with at least one entry */
class NonEmptyMap[K,V] private[oneand](raw: Map[K,V]) {
  assert(raw.nonEmpty)

  def get(k: K): Option[V] = raw.get(k)
  def head: (K,V) = raw.head
  def tail: Map[K,V] = raw.tail
  def size: Int = raw.size

  /** Remove an element from the map. Returns None if the removed element was the only one. */
  def -(k: K): Option[NonEmptyMap[K, V]] =
    if (raw.size > 1) Some(new NonEmptyMap(raw - k))
    else if (raw.contains(k)) None
    else Some(this)

  /** Add an element to the map, or replace an existing mapping */
  def +(pair: (K, V)): NonEmptyMap[K, V] =
    new NonEmptyMap(raw + pair)

  def ++(that: NonEmptyMap[K, V]): NonEmptyMap[K, V] = new NonEmptyMap(this.raw ++ that.toMap)
  def ++(that: scala.collection.GenTraversableOnce[(K, V)]): NonEmptyMap[K,V] = new NonEmptyMap(this.raw ++ that)

  def contains(k: K): Boolean = raw.contains(k)

  def forall(p: ((K, V)) => Boolean): Boolean = raw.forall(p)
  def exists(p: ((K, V)) => Boolean): Boolean = raw.exists(p)
  def filter(p: ((K, V)) => Boolean): Map[K,V] = raw.filter(p)
  def filterNot(p: ((K, V)) => Boolean): Map[K,V] = raw.filterNot(p)

  def foldLeft[B](z: B)(f: (B, (K, V)) => B): B = raw.foldLeft(z)(f)

  def map[K2,V2](f: ((K,V)) => (K2,V2)): NonEmptyMap[K2,V2] = new NonEmptyMap[K2,V2](raw.map(f))
  def mapValues[C](f: V => C): NonEmptyMap[K, C] = new NonEmptyMap[K, C](raw.mapValues(f))

  def toMap: Map[K, V] = raw
  def toList: List[(K, V)] = raw.toList

  override def toString: String = raw.mkString("NonEmptyMap(", ",", ")")
}

object NonEmptyMap {
  def apply[K, V](one: (K, V), thats: (K, V)*): NonEmptyMap[K, V] =
    new NonEmptyMap[K, V](Map(thats: _*) + one)

  def fromMap[K,V](m: Map[K,V]): Option[NonEmptyMap[K,V]] =
    if (m.isEmpty) None
    else Some(new NonEmptyMap(m))
}