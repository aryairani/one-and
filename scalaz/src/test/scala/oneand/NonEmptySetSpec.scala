package oneand.scalaz

import oneand.NonEmptySet
import oneand.scalaz.syntax.NonEmptySetOps

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.{ScalaCheck, Specification}

import _root_.scalaz.scalacheck.ScalazProperties
import _root_.scalaz.std.AllInstances._
import _root_.scalaz.syntax.std.list._

class NonEmptySetSpec extends Specification with ScalaCheck {
  import NonEmptySetSpec.arbitraryNonEmptySet

  def is = s2"""
    Semigroup laws ${ ScalazProperties.semigroup.laws[NonEmptySet[Int]] }
    Monad laws ${ ScalazProperties.monad.laws[NonEmptySet] }
    Foldable1 laws ${ ScalazProperties.foldable1.laws[NonEmptySet] }
    removing the only element leaves nothing ${ prop((i: Int) => (NonEmptySet(i) - i).isEmpty) }
    removing 2nd elements leaves something ${
    prop((i1: Int, i2: Int) =>
      (NonEmptySet(i1, i2) - i1).isEmpty === (i1 == i2))
  }
    removing non-member element leaves something ${
    prop((i1: Int, i2: Int) =>
      (NonEmptySet(i1) - i2).isEmpty === (i1 == i2))
  }
    ++(NonEmptySet) ${ prop((a: NonEmptySet[Int], b: NonEmptySet[Int]) => (a ++ b).toSet === (a.toSet ++ b.toSet)) }
    ++(Set) ${ prop((a: NonEmptySet[Int], b: Set[Int]) => (a ++ b).toSet === (a.toSet ++ b)) }
    .toNel consistent with .toList.toNel ${ prop((a: NonEmptySet[Int]) => a.toNel === a.toList.toNel.get) }
    .contains ${ prop((s: NonEmptySet[Int], a: Int) => s.contains(a) === s.toSet.contains(a)) }
    .size ${ prop((s: NonEmptySet[Int]) => s.size === s.toSet.size) }
  """
}

object NonEmptySetSpec {
  implicit def arbitraryNonEmptySet[A: Arbitrary]: Arbitrary[NonEmptySet[A]] = Arbitrary {
    val arb = Arbitrary.arbitrary[A]
    for {
      first <- arb
      rest <- Gen.listOf(arb)
    } yield rest.foldLeft(NonEmptySet(first))((l, a) => l + a)
  }
}