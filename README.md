## monix-newtypes-spray-integration

Integration between [monix-newtypes](https://newtypes.monix.io/) and spray-json.

## Run example

1. Install [scala-cli](https://scala-cli.virtuslab.org/)
2. Run `scala-cli run .`
3. That's it

## Usage

```scala 
import monix.newtypes._
import monix.newtypes.integrations.spray._

type EmailAddress = EmailAddress.Type
object EmailAddress extends NewtypeWrapped[String] with DeriveSprayCodec

type UserId = UserId.Type
object UserId extends NewtypeWrapped[UUID] with DeriveSprayCodec
```
