package oneand

import scala.annotation.tailrec

trait ListLike[F[_]] {
  def nil[A]: F[A]
  def cons[A](a: A, as: F[A]): F[A]
  def unsafeHead[A](fa: F[A]): A
  def unsafeTail[A](fa: F[A]): F[A]

  def foldLeft[A,B](fa: F[A])(z: B)(f: (B, A) => B): B

  // derived methods, override for efficiency if desired
  def naturalContains[A](fa: F[A])(a: A): Boolean = foldLeft(fa)(false)( (found, b) => found || (a == b) )
  def naturalDistinct[A](fa: F[A]): F[A] = foldRight(fa)((Set.empty[A], nil[A])) {
      case (a, (set, list)) =>
        if (set.contains(a)) (set, list)
        else (set + a, cons(a, list))
    }._2

  def size[A](fa: F[A]): Int = foldLeft(fa)(0)( (size, _) => size+1 )
  def reverse[A](fa: F[A]): F[A] =
    foldLeft(fa)(nil[A])( (b, a) => cons(a, b) )

  def foldRight[A,B](fa: F[A])(z: B)(f: (A, B) => B): B =
    foldLeft(reverse(fa))(z)( (b, a) => f(a, b) )

  def append[A](left: F[A], right: F[A]): F[A] = foldRight(left)(right)(cons)

  def toList[A](fa: F[A]): scala.List[A] = foldRight(fa)(scala.List.empty[A])(scala.::.apply)

  def apply[A](as: A*): F[A] = as.foldRight(nil[A])(cons)

  def map[A,B](fa: F[A])(f: A => B): F[B] = foldRight(fa)(nil[B])( (a, b) => cons(f(a), b) )
  def flatMap[A,B](fa: F[A])(f: A => F[B]): F[B] = foldRight(fa)(nil[B])( (a, b) => append(f(a), b) )
  def fill[A](n: Int)(a: A): F[A] = {
    @tailrec def loop(n: Int, fa: F[A]): F[A] = if (n < 1) fa else loop(n-1, cons(a, fa))
    loop(n, nil)
  }
}

trait ConsUnapply[F[_]] {
  def unapply[A](fa: F[A]): Option[(A, F[A])]
}

trait NilUnapply[F[_]] {
  def unapply[A](fa: F[A]): Boolean
}

trait ListUnapplyLike[F[_]] extends ListLike[F] {
  val XCons: ConsUnapply[F]
  val XNil: NilUnapply[F]

  override def foldLeft[A,B](fa: F[A])(z: B)(f: (B, A) => B): B = {
    @tailrec def loop(fa: F[A], acc: B): B = fa match {
      case XCons(a, as) => loop(as, f(acc, a))
      case XNil() => acc
    }
    loop(fa, z)
  }
}

object ListLike {
  implicit val scalaListListLike: ListUnapplyLike[scala.List] = new ListUnapplyLike[scala.List] {
    def nil[A]: List[A] = Nil
    def cons[A](a: A, as: List[A]): List[A] = a :: as

    override def size[A](fa: List[A]): Int = fa.size

    object XCons extends ConsUnapply[scala.List] {
      override def unapply[A](fa: List[A]): Option[(A, List[A])] = fa match {
        case a :: as => Some((a, as))
        case Nil => None
      }
    }
    object XNil extends NilUnapply[scala.List] {
      override def unapply[A](fa: List[A]): Boolean = fa.isEmpty
    }

    override def unsafeHead[A](fa: List[A]): A = fa.head

    override def toList[A](fa: List[A]): List[A] = fa

    override def unsafeTail[A](fa: List[A]): List[A] = fa.tail
  }
}
