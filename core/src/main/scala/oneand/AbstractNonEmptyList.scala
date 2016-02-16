package oneand

class AbstractNonEmptyList[F[_], A](val raw: F[A])(implicit F: ListLike[F]) {
  private def fromRaw[B](raw: F[B]): AbstractNonEmptyList[F, B] = new AbstractNonEmptyList(raw)

  def size: Int = F.size(raw)
  def head: A = F.unsafeHead(raw)
  def tail: F[A] = F.unsafeTail(raw)
  def reverse: AbstractNonEmptyList[F, A] = fromRaw(F.reverse(raw))

  def append(that: AbstractNonEmptyList[F, A]): AbstractNonEmptyList[F, A] =
    new AbstractNonEmptyList(F.append(this.raw, that.raw))

  def map[B](f: A => B): AbstractNonEmptyList[F, B] = fromRaw(F.map(raw)(f))
  def flatMap[B](f: A => F[B]): AbstractNonEmptyList[F, B] = fromRaw(F.flatMap(raw)(f))

  def naturalDistinct: AbstractNonEmptyList[F, A] =
    new AbstractNonEmptyList(F.naturalDistinct(raw))

  def foldLeft[B](z: B)(f: (B, A) => B): B = F.foldLeft(raw)(z)(f)
  def foldMapLeft1[B](z: A => B)(f: (B, A) => B): B = F.foldLeft(tail)(z(head))(f)

  def foldRight[B](z: B)(f: (A, B) => B): B = F.foldRight(raw)(z)(f)
  def foldMapRight1[B](z: A => B)(f: (A, B) => B): B = {
    val reversed = this.reverse
    reversed.foldMapLeft1(z)( (b, a) => f(a, b) )
  }

  def naturalContains(a: A): Boolean = F.naturalContains(raw)(a)

  def toList: List[A] = F.toList(raw)
}

abstract class AbstractNonEmptyListCompanion[F[_]](implicit F: ListLike[F]) {
  def apply[A](as: A*) = F.apply(as: _*)

  import scala.language.implicitConversions
  implicit def toRaw[A](l: AbstractNonEmptyList[F,A]): F[A] = l.raw
}


class NonEmptyList[A] private(raw: List[A]) extends AbstractNonEmptyList[scala.List, A](raw) {
  override def reverse: NonEmptyList[A] = new NonEmptyList(raw.reverse)
}

object NonEmptyList extends AbstractNonEmptyListCompanion[scala.List]




object OneAnd {

//  def apply[F[_], A](one: A, and: A*): NonEmptyMap[K, V] =
//    new NonEmptyMap[K, V](Map(thats: _*) + one)

//  def fromMap[K,V](m: Map[K,V]): Option[NonEmptyMap[K,V]] =
//    if (m.isEmpty) None
//    else Some(new NonEmptyMap(m))
}
