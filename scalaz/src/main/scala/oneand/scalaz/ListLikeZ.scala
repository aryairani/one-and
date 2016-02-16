package oneand
package scalaz

import _root_.scalaz.{Equal, Order}

trait ListLikeZ[F[_]] extends ListLike[F] {
  def contains[A: Equal](fa: F[A])(a: A): Boolean
  def distinct[A: Equal](fa: F[A])(a: A): F[A]
  def distinct[A: Order](fa: F[A])(a: A): F[A]
}

