package chat

import akka.actor.{Actor, ActorRef}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object User {
  case class Connected(outgoing: ActorRef)
  case class IncomingMessage(text: String)
  case class OutgoingMessage(text: String)
}

class User(chatRoom: ActorRef) extends Actor {
  implicit val executionContext: ExecutionContext = context.dispatcher
  import User._

  def receive = {
    case Connected(outgoing) =>
      context.become(connected(outgoing))
  }

  def connected(outgoing: ActorRef): Receive = {
    chatRoom ! ChatRoom.Join

    {
      case IncomingMessage(text) =>
        chatRoom ! ChatRoom.ChatMessage(text)

      case ChatRoom.ChatMessage(text) =>
        Future {
        outgoing ! OutgoingMessage(text)
        }
    }
  }

}

