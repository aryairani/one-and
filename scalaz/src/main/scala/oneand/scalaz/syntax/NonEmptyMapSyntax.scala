package oneand
package scalaz.syntax

import _root_.scalaz._
import _root_.scalaz.Scalaz._

trait NonEmptyMapSyntax {

  /** Add scalaz-related methods to NonEmptyMap */
  implicit class NonEmptyMapOps[K, V](m: NonEmptyMap[K, V]) {
    private def raw = m.toMap

    def ++[F[_] : Foldable](that: F[(K, V)]): NonEmptyMap[K, V] = m ++ that.toList.toMap

    def foldMap1[B](f: ((K, V)) => B)(implicit B: Semigroup[B]): B =
      raw.tail.foldLeft(f(raw.head))((b, kv) => B.append(b, f(kv)))

    def values: NonEmptyList[V] = toNel.map(_._2)

    def toNel: NonEmptyList[(K, V)] = (raw.toList: @unchecked) match {
      case head :: tail => NonEmptyList.nel(head, tail)
    }
  }

  /** scalaz-related static methods for NonEmptyMap */
  implicit class NonEmptyMapCompanionOps(c: NonEmptyMap.type) {
    def apply[F[_] : Foldable1, K, V](fa: F[(K, V)]): NonEmptyMap[K, V] =
      fa.foldMapLeft1[NonEmptyMap[K, V]](c.apply(_))(_ + _)
  }
}
