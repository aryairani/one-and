package oneand
package scalaz

import oneand.NonEmptySet

import org.specs2.{ScalaCheck, Specification}

import _root_.scalaz.scalacheck.ScalazProperties
import _root_.scalaz.std.AllInstances._
import _root_.scalaz.syntax.std.list._

class NonEmptySetScalazSpec extends Specification with ScalaCheck {
  import NonEmptySetSpec.arbitraryNonEmptySet

  def is = s2"""
    Semigroup laws ${ ScalazProperties.semigroup.laws[NonEmptySet[Int]] }
    Monad laws ${ ScalazProperties.monad.laws[NonEmptySet] }
    Foldable1 laws ${ ScalazProperties.foldable1.laws[NonEmptySet] }
    .toNel consistent with .toList.toNel ${ prop((a: NonEmptySet[Int]) => a.toNel === a.toList.toNel.get) }
  """
}
