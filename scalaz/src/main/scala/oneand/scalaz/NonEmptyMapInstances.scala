package oneand.scalaz

import oneand.NonEmptyMap
import oneand.scalaz.foo._

import _root_.scalaz._
import _root_.scalaz.Scalaz._

trait NonEmptyMapInstances {
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

