package oneand
package scalaz.syntax

import _root_.scalaz._
import _root_.scalaz.Scalaz._

trait NonEmptySetSyntax {

  /** Add scalaz-related methods to NonEmptySet */
  implicit class NonEmptySetOps[A](s: NonEmptySet[A]) {
    private def raw = s.toSet

    // todo: https://issues.scala-lang.org/browse/SI-9588
    def +++[F[_] : Foldable](that: F[A]): NonEmptySet[A] = s ++ that.toSet

    def foldMap1[B: Semigroup](f: A => B): B = raw.tail.foldLeft(f(raw.head))((b, a) => Semigroup[B].append(b, f(a)))

    def toNel: NonEmptyList[A] = NonEmptyList(raw.head, raw.tail.toSeq: _*)
  }

  /** scalaz-related static methods for NonEmptySet */
  implicit class NonEmptySetCompanionOps(c: NonEmptySet.type) {
    def fromFoldable1[F[_] : Foldable1, A](fa: F[A]): NonEmptySet[A] =
      fa.foldMapLeft1[NonEmptySet[A]](c.apply(_))(_ + _)
  }
}
