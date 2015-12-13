package oneand.scalaz

import oneand.NonEmptyMap

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.{ScalaCheck, Specification}
import org.specs2.{Specification, ScalaCheck}

class NonEmptyMapSpec extends Specification with ScalaCheck {
  import NonEmptyMapSpec.arbitraryNonEmptyMap

  def is = s2"""
    removing the only element leaves nothing ${ prop((k: Int, v: Int) => (NonEmptyMap(k -> v) - k).isEmpty) }
    removing 2nd elements leaves something ${
      prop((k1: Int, v1: Int, k2: Int, v2: Int) =>
        (NonEmptyMap(k1 -> v1, k2 -> v2) - k1).isEmpty === (k1 == k2))
    }
    removing non-member element leaves something ${
      prop((k1: Int, v1: Int, k2: Int) =>
        (NonEmptyMap(k1 -> v1) - k2).isEmpty === (k1 == k2))
    }
    ++(NonEmptyMap) ${ prop((a: NonEmptyMap[Int,String], b: NonEmptyMap[Int,String]) => (a ++ b).toMap === (a.toMap ++ b.toMap)) }
    ++(Map) ${ prop((a: NonEmptyMap[Int,String], b: Map[Int,String]) => (a ++ b).toMap === (a.toMap ++ b)) }
    .mapValues.toMap consistent with .toMap.mapValues ${
      prop((a: NonEmptyMap[Int,Int]) => a.mapValues(_+1).toMap === a.toMap.mapValues(_+1))
    }
    .contains ${ prop((m: NonEmptyMap[Int,String], a: Int) => m.contains(a) === m.toMap.contains(a)) }
    .get ${ prop((m: NonEmptyMap[Int,String], a: Int) => m.get(a) === m.toMap.get(a)) }
    .size ${ prop((m: NonEmptyMap[Int,String]) => m.size === m.toMap.size) }
    .head ${ prop((kv: (Int, String)) => NonEmptyMap(kv).head === kv)
  }
  """
}

object NonEmptyMapSpec {
  implicit def arbitraryNonEmptyMap[K: Arbitrary, V: Arbitrary]: Arbitrary[NonEmptyMap[K, V]] = Arbitrary {
    val pair: Gen[(K, V)] = Arbitrary.arbitrary[(K, V)]
    for {
      first <- pair
      rest <- Gen.listOf(pair)
    } yield rest.foldLeft(NonEmptyMap(first))((l, kv) => l + kv)
  }
}