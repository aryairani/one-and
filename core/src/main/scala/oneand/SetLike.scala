package oneand

trait SetLike[F[_]] {
  def contains[A](a: A, fa: F[A]): Boolean
  def add[A](a: A, fa: F[A]): F[A]
  def remove[A](a: A, fa: F[A]): F[A]
  def union[A](a: F[A], b: F[A]): F[A]
  def intersect[A](a: F[A], b: F[A]): F[A]
  def diff[A](a: F[A], b: F[A]): F[A]
}

object SetLike {
  implicit val scalaSetSetLike: SetLike[scala.Predef.Set] = new SetLike[Set] {
    def contains[A](a: A, fa: Set[A]): Boolean = fa.contains(a)
    def remove[A](a: A, fa: Set[A]): Set[A] = fa - a
    def add[A](a: A, fa: Set[A]): Set[A] = fa + a
    def union[A](a: Set[A], b: Set[A]): Set[A] = a union b
    def diff[A](a: Set[A], b: Set[A]): Set[A] = a diff b
    def intersect[A](a: Set[A], b: Set[A]): Set[A] = a intersect b
  }
}