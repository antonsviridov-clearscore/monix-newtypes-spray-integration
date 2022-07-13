//> using scala "2.13"
//> using lib "io.monix::newtypes-core:0.2.3"
//> using lib "io.spray::spray-json:1.3.6"
//> using option "-Vimplicits"
//> using option "-Vtype-diffs"

package monix.newtypes.integrations.spray

import spray.json._
import monix.newtypes._
import java.util.UUID

private[spray] trait NewtypeReader[T] extends JsonReader[T]
private[spray] trait NewtypeWriter[T] extends JsonWriter[T]
private[spray] trait NewtypeFormat[T] extends JsonFormat[T]

trait DeriveSprayCodec extends DeriveSprayEncoder with DeriveSprayDecoder {
  implicit def jsonFormat[T](implicit
      r: NewtypeReader[T],
      w: NewtypeWriter[T]
  ): JsonFormat[T] =
    new NewtypeFormat[T] {
      override def read(json: JsValue): T = r.read(json)

      override def write(obj: T): JsValue = w.write(obj)
    }
}

trait DeriveSprayDecoder {
  implicit def jsonDecoder[T, S](implicit
      builder: HasBuilder.Aux[T, S],
      dec: JsonReader[S]
  ): NewtypeReader[T] = new NewtypeReader[T] {
    override def read(json: JsValue): T =
      builder.build(dec.read(json)) match {
        case Left(failure) =>
          val msg = failure.message.fold("")(m => s" — $m")
          throw new DeserializationException(
            s"Invalid ${failure.typeInfo.typeLabel}$msg"
          )
        case Right(value) =>
          value
      }
  }
}

trait DeriveSprayEncoder {
  implicit def jsonEncoder[T, S](implicit
      extractor: HasExtractor.Aux[T, S],
      enc: JsonWriter[S]
  ): NewtypeWriter[T] = new NewtypeWriter[T] {
    override def write(a: T): JsValue =
      enc.write(extractor.extract(a))
  }
}
