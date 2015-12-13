package oneand.scalaz

import oneand.NonEmptyMap

import _root_.scalaz._
import _root_.scalaz.Scalaz._

trait NonEmptyMapSupport {

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
    def fromFoldable1[F[_] : Foldable1, K, V](fa: F[(K, V)]): NonEmptyMap[K, V] =
      fa.foldMapLeft1[NonEmptyMap[K, V]](c.apply(_))(_ + _)
  }

  //// Instances

  // natural equals for natural map
  implicit def NonEmptyMapEqual[K, V] = Equal.equalA[Map[K, V]].contramap[NonEmptyMap[K,V]](_.toMap)

  // delegated instances
  private def szMap = _root_.scalaz.std.map

  implicit def NonEmptyMapSemigroup[K, V: Semigroup]: Semigroup[NonEmptyMap[K, V]] =
    Semigroup.instance[NonEmptyMap[K, V]]( (a, b) => new NonEmptyMap(szMap.mapMonoid[K, V].append(a.toMap, b.toMap)) )

  implicit def NonEmptyMapInstance[K, V]: Foldable1[NonEmptyMap[K, ?]] =
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

