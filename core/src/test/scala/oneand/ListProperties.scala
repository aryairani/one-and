package oneand

import org.specs2.Specification

class ListSpec[F[_]](implicit F: ListLike[F]) extends Specification {
  import F._
  def is = s2"""
    ${ F(1,2) must_== cons(1, cons(2, nil)) }
    ${ reverse(F(1,2)) must_== F(2,1) }
    ${ F.foldLeft(F(1,2,3,4,5))(0)(-_ + _) must_== 3 }
    ${ F.foldRight(F(1,2,3,4,5))(0)(-_ + _) must_== -15}
    ${ F.append(F(1,2), F(3,4)) must_== F(1,2,3,4) }
    ${ F.map(F(1,2))(1+) must_== F(2,3) }
    ${ F.flatMap(F(0,1,2))(n => F.fill(n)(n)) must_== F(1,2,2) }
  """
}

class ListListSpec extends ListSpec[List]
