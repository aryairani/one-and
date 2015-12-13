package oneand.scalaz

import oneand.NonEmptySet

import _root_.scalaz._
import _root_.scalaz.Scalaz._

trait NonEmptySetSupport {

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
    def fromFoldable1[F[_]: Foldable1, A](fa: F[A]): NonEmptySet[A] =
      fa.foldMapLeft1[NonEmptySet[A]](c.apply(_))(_ + _)
  }

  /// Instances

  implicit def NonEmptySetSemigroup[A]: Semigroup[NonEmptySet[A]] =
    Semigroup.instance[NonEmptySet[A]] { (a, b) => a ++ b.toSet }

  // natural equals for natural set
  implicit def NonEmptySetEqual[A]: Equal[NonEmptySet[A]] = Equal.equalA[Set[A]].contramap(_.toSet)

  implicit def NonEmptySetShow[A](implicit A: Show[A]): Show[NonEmptySet[A]] =
    Show.show(set => Cord("NonEmptySet(", Cord.mkCord(",", set.toList.map(A.show): _*), ")"))

  implicit def NonEmptySetInstance: Monad[NonEmptySet] with Foldable1[NonEmptySet] =
    new Monad[NonEmptySet] with Foldable1[NonEmptySet] {
      override def foldLeft[A, B](fa: NonEmptySet[A], z: B)(f: (B, A) => B): B = fa.foldLeft(z)(f)
      def foldMap1[A, B](fa: NonEmptySet[A])(f: (A) => B)(implicit F: Semigroup[B]): B = fa foldMap1 f
      def foldMapRight1[A, B](fa: NonEmptySet[A])(z: (A) => B)(f: (A, => B) => B): B = fa.foldMapRight1(z)(f)

      override def map[A, B](fa: NonEmptySet[A])(f: (A) => B): NonEmptySet[B] = fa map f
      def bind[A, B](fa: NonEmptySet[A])(f: (A) => NonEmptySet[B]): NonEmptySet[B] = fa flatMap f
      def point[A](a: => A): NonEmptySet[A] = NonEmptySet(a)
    }
}