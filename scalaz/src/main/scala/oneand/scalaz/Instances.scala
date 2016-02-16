package oneand
package scalaz

import _root_.scalaz.{IList, ICons, INil}

trait Instances {
  implicit val IListListLike: ListLike[IList] = new ListUnapplyLike[IList] {
    def nil[A]: IList[A] = INil[A]
    def cons[A](a: A, as: IList[A]): IList[A] = ICons(a, as)

    object XCons extends ConsUnapply[IList] {
      override def unapply[A](fa: IList[A]): Option[(A, IList[A])] = fa match {
        case ICons(a, as) => Some((a, as))
        case INil() => None
      }
    }

    object XNil extends NilUnapply[IList] {
      override def unapply[A](fa: IList[A]): Boolean = fa.isEmpty
    }

    override def unsafeHead[A](fa: IList[A]): A = fa.headOption.get

    override def unsafeTail[A](fa: IList[A]): IList[A] = fa.tailOption.get
  }
}
