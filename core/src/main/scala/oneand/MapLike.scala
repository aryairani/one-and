package oneand

trait MapLike[F[_,_]] {
  def get[K,V](map: F[K,V])(k: K): Option[V]

  def head[K,V](map: F[K,V]): (K,V)
  def tail[K,V](map: F[K,V]): F[K,V]

  def add[K,V](map: F[K,V])(k: K, v: V): F[K,V]
  def remove[K,V](map: F[K,V])(k: K): F[K,V]

  def addAll[K,V](a: F[K,V], b: F[K,V]): F[K,V]

  def foldKeys[K,V,B](m: Map[K, V])(z: B)(f: (B, K) => B): B
  def foldKeyValues[K,V,B](m: Map[K, V])(z: B)(f: (B, (K,V)) => B): B

  def contains[K,V](map: F[K,V])(k: K): Boolean = get(map)(k).isDefined
}

object MapLike {
  implicit val scalaMapMapLike: MapLike[scala.Predef.Map] = new MapLike[Map] {
    def get[K, V](map: Map[K, V])(k: K): Option[V] = map.get(k)
    def head[K, V](map: Map[K, V]): (K,V) = map.head
    def tail[K, V](map: Map[K, V]): Map[K, V] = map.tail
    def add[K, V](map: Map[K, V])(k: K, v: V): Map[K, V] = map + ((k,v))
    def addAll[K, V](a: Map[K, V], b: Map[K, V]): Map[K, V] = a ++ b
    def remove[K, V](map: Map[K, V])(k: K): Map[K, V] = map - k

    def foldKeys[K, V, B](m: Map[K, V])(z: B)(f: (B, K) => B): B = m.keys.foldLeft(z)(f)
    def foldKeyValues[K, V, B](m: Map[K, V])(z: B)(f: (B, (K, V)) => B): B = m.foldLeft(z)(f)
  }
}
