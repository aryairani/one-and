package oneand

import scalaz._
import scalaz.syntax.foldable1._

/** An immutable Map with at least one entry */
class NonEmptyMap[K,V] private(raw: Map[K,V]) {
  assert(raw.nonEmpty)

  def get(k: K): Option[V] = raw.get(k)
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
  def ++[F[_]: Foldable](that: F[(K, V)]): NonEmptyMap[K, V] = this ++ that.toList.toMap

  def contains(k: K): Boolean = raw.contains(k)

  def forall(p: ((K, V)) => Boolean) = raw.forall(p)
  def exists(p: ((K, V)) => Boolean) = raw.exists(p)

  def foldLeft[B](z: B)(f: (B, (K, V)) => B): B = raw.foldLeft(z)(f)
  def foldMap1[B](f: ((K,V)) => B)(implicit B: Semigroup[B]): B =
    raw.tail.foldLeft(f(raw.head))((b, kv) => B.append(b, f(kv)))

  def map[K2,V2](f: ((K,V)) => (K2,V2)): NonEmptyMap[K2,V2] = new NonEmptyMap[K2,V2](raw.map(f))
  def mapValues[C](f: V => C): NonEmptyMap[K, C] = new NonEmptyMap[K, C](raw.mapValues(f))
  def values: NonEmptyList[V] = toNel.map(_._2)

  def toMap: Map[K, V] = raw
  def toList: List[(K, V)] = raw.toList
  def toNel: NonEmptyList[(K, V)] = (raw.toList: @unchecked) match {
    case head :: tail => NonEmptyList.nel(head, tail)
  }

  override def toString: String =
    raw.mkString("NonEmptyMap(", ",", ")")
}

object NonEmptyMap {
  def apply[K, V](one: (K, V), thats: (K, V)*): NonEmptyMap[K, V] =
    new NonEmptyMap[K, V](Map(thats: _*) + one)

  // natural instance
  implicit def equal[K, V] = Equal.equalA[Map[K, V]].contramap[NonEmptyMap[K,V]](_.toMap)

  // delegated instances
  private def szMap = scalaz.std.map
  implicit def semigroup[K, V: Semigroup]: Semigroup[NonEmptyMap[K, V]] =
    Semigroup.instance[NonEmptyMap[K, V]]( (a, b) => new NonEmptyMap(szMap.mapMonoid[K, V].append(a.toMap, b.toMap)) )

  implicit def mapInstance[K, V]: Foldable1[NonEmptyMap[K, ?]] =
    new Foldable1[NonEmptyMap[K, ?]] {
      override def foldMap1[A, B: Semigroup](fa: NonEmptyMap[K, A])(f: (A) => B): B =
        fa.foldMap1 { case (k, a) => f(a) }

      override def foldMapRight1[A, B](fa: NonEmptyMap[K, A])(z: (A) => B)(f: (A, => B) => B): B =
        fa.values.foldMapRight1(z)(f)
    }

  // todo: how do you want to merge them?
  // Plus[NonEmptyMap[K, ?]]
  // Bind[NonEmptyMap[K, ?]]
  // Traverse1[NonEmptyMap[K, ?]]
}