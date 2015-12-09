package oneand

import scalaz._
import scalaz.syntax.foldable._


/** An immutable Set with at least one element */
class NonEmptySet[A] private(raw: Set[A]) {
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

  def ++[F[_]: Foldable](that: F[A]): NonEmptySet[A] = this ++ that.toSet
  def ++(that: scala.collection.GenTraversableOnce[A]): NonEmptySet[A] = new NonEmptySet(this.toSet ++ that)

  def map[B](f: A => B): NonEmptySet[B] = new NonEmptySet(raw map f)

  def flatMap[B](f: A => NonEmptySet[B]): NonEmptySet[B] =
    new NonEmptySet(raw flatMap (f andThen (_.toSet)))

  def foldLeft[B](z: B)(f: (B, A) => B): B = raw.foldLeft(z)(f)
  def foldMap1[B: Semigroup](f: A => B): B = raw.tail.foldLeft(f(raw.head))((b, a) => Semigroup[B].append(b, f(a)))
  def foldMapRight1[B](z: A => B)(f: (A, => B) => B): B = {
    val reversed = toList.reverse
    reversed.tail.foldLeft(z(reversed.head))((b,a) => f(a,b))
  }

  def toSet: Set[A] = raw
  def toList: List[A] = raw.toList
  def toNel: NonEmptyList[A] = NonEmptyList(raw.head, raw.tail.toSeq: _*)

  override def toString: String = raw.mkString("NonEmptySet(",",",")")
}

object NonEmptySet {
  def apply[A](one: A, others: A*): NonEmptySet[A] =
    new NonEmptySet[A](Set(others: _*) + one)

  implicit def semigroup[A]: Semigroup[NonEmptySet[A]] =
    Semigroup.instance[NonEmptySet[A]] { (a, b) => a ++ b }

  // natural equals for now
  implicit def equal[A]: Equal[NonEmptySet[A]] = Equal.equalA[Set[A]].contramap(_.toSet)
  implicit def show[A](implicit A: Show[A]): Show[NonEmptySet[A]] =
    Show.show(set => Cord("NonEmptySet(", Cord.mkCord(",", set.toList.map(A.show): _*), ")"))

  implicit def nonEmptySetInstance: Monad[NonEmptySet] with Foldable1[NonEmptySet] =
    new Monad[NonEmptySet] with Foldable1[NonEmptySet] {
      override def foldLeft[A, B](fa: NonEmptySet[A], z: B)(f: (B, A) => B): B = fa.foldLeft(z)(f)
      def foldMap1[A, B](fa: NonEmptySet[A])(f: (A) => B)(implicit F: Semigroup[B]): B = fa foldMap1 f
      def foldMapRight1[A, B](fa: NonEmptySet[A])(z: (A) => B)(f: (A, => B) => B): B = fa.foldMapRight1(z)(f)

      override def map[A, B](fa: NonEmptySet[A])(f: (A) => B): NonEmptySet[B] = fa map f
      def bind[A, B](fa: NonEmptySet[A])(f: (A) => NonEmptySet[B]): NonEmptySet[B] = fa flatMap f
      def point[A](a: => A): NonEmptySet[A] = NonEmptySet(a)
    }
}