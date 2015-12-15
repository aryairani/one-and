package oneand.scalaz

import oneand.NonEmptyMap

import org.specs2.{ScalaCheck, Specification}

import _root_.scalaz._
import _root_.scalaz.Scalaz._
import _root_.scalaz.scalacheck.ScalazProperties

class NonEmptyMapScalazSpec extends Specification with ScalaCheck {
  import NonEmptyMapSpec.arbitraryNonEmptyMap

  def is = s2"""
    Semigroup laws ${ ScalazProperties.semigroup.laws[NonEmptyMap[Int, Int]] }
    Foldable1 laws ${ ScalazProperties.foldable1.laws[NonEmptyMap[Int, ?]] }
    foldMap1 consistent with NonEmptyList ${
      prop((m: NonEmptyMap[Int,String]) =>
        m.foldMap1(Vector(_)) === Foldable1[NonEmptyList].foldMap1(m.toNel)(Vector(_))
      )
    }
    .toNel consistent with .toList.toNel ${ prop((a: NonEmptyMap[Int,String]) => a.toNel === a.toList.toNel.get) }
  }
  """
}
