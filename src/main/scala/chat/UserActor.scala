package chat

import akka.actor.{Actor, ActorRef}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object UserActor {
  case class Connected(outgoing: ActorRef)
  case class IncomingMessage(text: String)
  case class OutgoingMessage(text: String)
}

class UserActor(chatRoom: ActorRef) extends Actor {
  implicit val executionContext: ExecutionContext = context.dispatcher
  import UserActor._

  def receive = {
    case Connected(outgoing) =>
      context.become(connected(outgoing))
  }

  def connected(outgoing: ActorRef): Receive = {
    chatRoom ! chatroom.Join

    {
      case IncomingMessage(text) =>
        chatRoom ! chatroom.ChatMessage(text)

      case chatroom.ChatMessage(text) =>
        Future {
          outgoing ! OutgoingMessage(text)
        }
    }
  }

}