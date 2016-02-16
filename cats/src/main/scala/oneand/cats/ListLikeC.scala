package oneand.cats

import _root_.cats.{Eq, Order}

trait ListLikeC[F[_]] {
  def contains[A: Eq](fa: F[A])(a: A): Boolean
  def distinct[A: Eq](fa: F[A])(a: A): F[A]
  def distinct[A: Order](fa: F[A])(a: A): F[A]
}
