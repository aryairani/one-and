package oneand.cats

import cats.data.OneAnd
import cats.{SemigroupK, Eval, Reducible, Foldable}
import cats.std.all._


object MissingInstances {
  implicit def vectorSemigroup[A] = SemigroupK[Vector].algebra[A]

  // todo: remove if https://github.com/non/cats/pull/772 is accepted
  implicit def oneAndReducible[F[_]](implicit F: Foldable[F]): Reducible[OneAnd[F,?]] =
    new Reducible[({type Λ$[β] = OneAnd[F, β]})#Λ$] {

      override def reduceRightTo[A, B](fa: OneAnd[F, A])(f: (A) ⇒ B)(g: (A, Eval[B]) ⇒ Eval[B]): Eval[B] =
        F.reduceRightToOption(fa.tail)(f)(g).flatMap(_.fold(Eval.now(f(fa.head)))(b ⇒ g(fa.head, Eval.now(b))))

      override def reduceLeftTo[A, B](fa: OneAnd[F, A])(f: (A) ⇒ B)(g: (B, A) ⇒ B): B =
        F.foldLeft[A, B](fa.tail, f(fa.head))(g)

      override def foldLeft[A, B](fa: OneAnd[F, A], b: B)(f: (B, A) ⇒ B): B =
        F.foldLeft[A, B](fa.tail, f(b, fa.head))(f)

      override def foldRight[A, B](fa: OneAnd[F, A], lb: Eval[B])(f: (A, Eval[B]) ⇒ Eval[B]): Eval[B] =
        f(fa.head, F.foldRight[A, B](fa.tail, lb)(f))
    }

}
