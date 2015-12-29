package oneand
package cats

import _root_.cats.data.{NonEmptyList}
import _root_.cats.std.all._
import _root_.cats.Reducible
import org.specs2.{ScalaCheck, Specification}

class NonEmptyMapCatsSpec extends Specification with ScalaCheck {
  import NonEmptyMapSpec.arbitraryNonEmptyMap
  import MissingInstances.{oneAndReducible, vectorSemigroup}

  def is = s2"""
    Semigroup laws ${ algebra.laws.GroupLaws[NonEmptyMap[Int, Int]].semigroup.all }
    foldMap1 consistent with NonEmptyList ${
      prop((m: NonEmptyMap[Int,String]) =>
        m.foldMap1(Vector(_)) === Reducible[NonEmptyList].reduceMap(m.toNel)(Vector(_))
      )
    }
    .toNel consistent with NonEmptyList.fromList ${
      implicit val pairEq: _root_.cats.Eq[(Int, String)] = _root_.cats.Eq.fromUniversalEquals
      prop((a: NonEmptyMap[Int,String]) =>
        a.toNel === NonEmptyList.fromList(a.toList).get
      )
    }
  }
  """
}

//todo Reducible / Foldable1 laws ${ _root_.cats.laws. ScalazProperties.foldable1.laws[NonEmptyMap[Int, ?]] }
