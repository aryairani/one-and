package oneand.scalaz

import oneand.NonEmptySet

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.{ScalaCheck, Specification}

class NonEmptySetSpec extends Specification with ScalaCheck {
  import NonEmptySetSpec.arbitraryNonEmptySet

  def is = s2"""
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
    .contains ${ prop((s: NonEmptySet[Int], a: Int) => s.contains(a) === s.toSet.contains(a)) }
    .size ${ prop((s: NonEmptySet[Int]) => s.size === s.toSet.size) }
    .head ${ prop((i: Int, is: List[Int]) => (i :: is).contains(NonEmptySet(i, is: _*).head)) }
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