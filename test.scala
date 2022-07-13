package test

import monix.newtypes._
import monix.newtypes.integrations.spray._
import java.util.UUID
import spray.json.DefaultJsonProtocol._
import spray.json._

object Exa extends App {
  type EmailAddress = EmailAddress.Type
  object EmailAddress extends NewtypeWrapped[String] with DeriveSprayCodec

  type UserId = UserId.Type
  object UserId extends NewtypeWrapped[UUID] with DeriveSprayCodec

  case class MyStuff(userId: UserId, emails: List[EmailAddress])

  val json =
    """
    {
      "userId": "1d831156-c2c8-43ed-8e5d-da5e02a188ae",
      "emails": ["bla@bla.com", "hello@hello.com"]
    }
    """

  implicit val uuidFormat = new JsonFormat[UUID] {
    override def read(json: JsValue): UUID =
      UUID.fromString(StringJsonFormat.read(json))

    override def write(obj: UUID): JsValue = JsString(obj.toString)
  }

  assert(
    EmailAddress("hello@hello.com").toJson == JsString("hello@hello.com")
  )
  assert(UserId(UUID.randomUUID()).toJson.isInstanceOf[JsString])

  implicit val format = jsonFormat2(MyStuff)
  println(json.parseJson.convertTo[MyStuff])
}
