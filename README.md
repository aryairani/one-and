# one-and
[![Circle CI](https://circleci.com/gh/refried/one-and.svg?style=shield)](https://circleci.com/gh/refried/one-and)
[![Travis CI](https://travis-ci.org/refried/one-and.svg?branch=master)](https://travis-ci.org/refried/one-and)
[![codecov.io](http://codecov.io/github/refried/one-and/coverage.svg?branch=master)](http://codecov.io/github/refried/one-and?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.arya/oneand-core_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.arya/oneand-core_2.11)


some nonempty data structures in scala

## oneand-core
```scala
libraryDependencies += "net.arya" %% "oneand-argonaut" % "0.1"
```

Features:
```scala
class NonEmptyMap[K, V] {
  def get(k: K): Option[V]
  def head: (K, V)
  def tail: Map[K, V]
  def size: Int
  def +(pair: (K, V)): NonEmptyMap[K, V]
  def -(k: K): Option[NonEmptyMap[K, V]]
  def ++(that: NonEmptyMap[K, V]): NonEmptyMap[K, V]
  def ++(that: scala.collection.GenTraversableOnce[(K, V)])

  def contains(k: K): Boolean
  def forall(p: ((K, V)) => Boolean): Boolean
  def exists(p: ((K, V)) => Boolean): Boolean
  def filter(p: ((K, V)) => Boolean): Map[K,V]
  def filterNot(p: ((K, V)) => Boolean): Map[K,V]

  def foldLeft[B](z: B)(f: (B, (K, V)) => B): B

  def map[K2,V2](f: ((K,V)) => (K2,V2)): NonEmptyMap[K2,V2]
  def mapValues[C](f: V => V2): NonEmptyMap[K, V2]

  def toMap: Map[K, V]
  def toList: List[(K, V)]
}
object NonEmptyMap {
  def apply[K, V](one: (K, V), thats: (K, V)*): NonEmptyMap[K, V]
  def fromMap[K,V](m: Map[K,V]): Option[NonEmptyMap[K,V]]
}

class NonEmptySet[A]{

  def size: Int = raw.size
  def head: A = raw.head

  def +(a: A): NonEmptySet[A]
  def -(k: A): Option[NonEmptySet[A]]
  def ++(that: NonEmptySet[A]): NonEmptySet[A]
  def ++(that: scala.collection.GenTraversableOnce[A]): NonEmptySet[A]

  def flatMap[B](f: A => NonEmptySet[B]): NonEmptySet[B]
  def map[B](f: A => B): NonEmptySet[B]

  def contains(a: A): Boolean
  def forall(p: A => Boolean): Boolean
  def exists(p: A => Boolean): Boolean
  def filter(p: A => Boolean): Set[A]
  def filterNot(p: A => Boolean): Set[A]

  def foldLeft[B](z: B)(f: (B, A) => B): B
  def foldMapRight1[B](z: A => B)(f: (A, => B) => B): B 

  def toSet: Set[A]
  def toList: List[A]
}

object NonEmptySet {
  def apply[A](one: A, others: A*): NonEmptySet[A]
  def fromSet[A](s: Set[A]): Option[NonEmptySet[A]]
}

```

