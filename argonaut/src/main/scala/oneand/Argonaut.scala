package oneand

import argonaut.{DecodeJson, EncodeJson}

object Argonaut {
  implicit def NonEmptySetEncodeJson[A:EncodeJson]: EncodeJson[NonEmptySet[A]] =
    EncodeJson.of[List[A]].contramap(_.toList)
  implicit def NonEmptySetDecodeJson[A:DecodeJson]: DecodeJson[NonEmptySet[A]] =
    DecodeJson.of[List[A]].map(l => NonEmptySet(l.head, l.tail: _*)).setName("NonEmptySet")

  implicit def NonEmptyMapEncodeJson[V:EncodeJson]: EncodeJson[NonEmptyMap[String,V]] =
    EncodeJson.of[Map[String,V]].contramap(_.toMap)
  implicit def NonEmptyMapDecodeJson[V:DecodeJson]: DecodeJson[NonEmptyMap[String,V]] =
    DecodeJson.of[Map[String,V]].map(m => NonEmptyMap(m.head, m.tail.toSeq: _*)).setName("NonEmptyMap")
}
