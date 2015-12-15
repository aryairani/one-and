package oneand.cats

import algebra.{Eq, Semigroup}
import cats.data.NonEmptyList
import cats._
import oneand.NonEmptyMap
import cats.Reducible.ops._
import cats.Foldable.ops._

trait NonEmptyMapSupport {

  /** Add scalaz-related methods to NonEmptyMap */
  implicit class NonEmptyMapOps[K, V](m: NonEmptyMap[K, V]) {
    private def raw = m.toMap

    def ++[F[_]: Foldable](that: F[(K, V)]): NonEmptyMap[K, V] = m ++ that.toList.toMap

    def foldMap1[B](f: ((K, V)) => B)(implicit B: Semigroup[B]): B =
      raw.tail.foldLeft(f(raw.head))((b, kv) => B.combine(b, f(kv)))

    def values: NonEmptyList[V] = Functor[NonEmptyList].map(toNel)(_._2)

    def toNel: NonEmptyList[(K, V)] = (raw.toList: @unchecked) match {
      case head :: tail => NonEmptyList(head, tail)
    }
  }

  /** scalaz-related static methods for NonEmptyMap */
  implicit class NonEmptyMapCompanionOps(c: NonEmptyMap.type) {
    def fromReducible[F[_]: Reducible, K, V](fa: F[(K, V)]): NonEmptyMap[K, V] =
      fa.reduceLeftTo[NonEmptyMap[K, V]](c.apply(_))(_ + _)
  }

  //// Instances

  // natural equals for natural map
  implicit def NonEmptyMapEqual[K, V] = Eq.fromUniversalEquals[Map[K, V]].on[NonEmptyMap[K,V]](_.toMap)


  implicit def NonEmptyMapSemigroup[K, V: Semigroup]: Semigroup[NonEmptyMap[K, V]] =
    new Semigroup[NonEmptyMap[K, V]] {
      override def combine(x: NonEmptyMap[K, V], y: NonEmptyMap[K, V]): NonEmptyMap[K, V] =
        new NonEmptyMap(_root_.cats.std.map.mapMonoid[K,V].combine(x.toMap, y.toMap))
    }

  implicit def NonEmptyMapInstance[K, V]: Reducible[NonEmptyMap[K, ?]] with Traverse[NonEmptyMap[K, ?]] with Functor[NonEmptyMap[K, ?]] with SemigroupK[NonEmptyMap[K, ?]] =
    new Reducible[NonEmptyMap[K, ?]] with Traverse[NonEmptyMap[K, ?]] with Functor[NonEmptyMap[K, ?]] with SemigroupK[NonEmptyMap[K, ?]] {
      override def combine[A](x: NonEmptyMap[K, A], y: NonEmptyMap[K, A]): NonEmptyMap[K, A] =
        new NonEmptyMap[K,A](x.toMap ++ y.toMap)

      // delegated instances
      private def catsMap = _root_.cats.std.map.mapInstance[K]

      override def reduceLeftTo[A, B](fa: NonEmptyMap[K, A])(f: (A) ⇒ B)(g: (B, A) ⇒ B): B =
        catsMap.reduceLeftToOption(fa.toMap)(f)(g).get

      override def reduceRightTo[A, B](fa: NonEmptyMap[K, A])(f: (A) ⇒ B)(g: (A, Eval[B]) ⇒ Eval[B]): Eval[B] =
        catsMap.reduceRightToOption(fa.toMap)(f)(g).map(_.get)

      override def traverse[G[_], A, B](fa: NonEmptyMap[K, A])(f: (A) ⇒ G[B])(implicit evidence$1: Applicative[G]): G[NonEmptyMap[K, B]] = {
        val G = Applicative[G]
        val gba: G[NonEmptyMap[K,B]] = G.map(f(fa.head._2))(b ⇒ NonEmptyMap(fa.head._1 → b))
        fa.tail.foldLeft(gba) { (buf, a) ⇒
          G.map2(buf, f(a._2)) { case (x, y) ⇒ x + (a._1 → y) }
        }
      }

      override def foldLeft[A, B](fa: NonEmptyMap[K, A], b: B)(f: (B, A) ⇒ B): B =
        catsMap.foldLeft(fa.toMap, b)(f)

      override def foldRight[A, B](fa: NonEmptyMap[K, A], lb: Eval[B])(f: (A, Eval[B]) ⇒ Eval[B]): Eval[B] =
        catsMap.foldRight(fa.toMap, lb)(f)

      override def map[A, B](fa: NonEmptyMap[K, A])(f: (A) ⇒ B): NonEmptyMap[K, B] = fa.mapValues(f)
    }

  // todo: how do you want to merge them?
  // Bind[NonEmptyMap[K, ?]]
  // Traverse1[NonEmptyMap[K, ?]]
}
