package oneand.scalaz

import oneand.ListSpec

import scalaz.IList

class ListSpecZ[F[_]](implicit F: ListLikeZ[F])

class IListListSpec extends ListSpec[IList]