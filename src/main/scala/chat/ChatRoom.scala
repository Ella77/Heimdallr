package chat

import akka.actor._
import chat.Server.system
import com.typesafe.config.ConfigFactory

class ChatRoom(roomId: Int) {
  val system = ActorSystem("heimdallr", ConfigFactory.load())


//

  def get(): ActorRef = {
   system.actorOf(Props(classOf[ChatRoomActor],roomId), roomId.toString)
//    println("ACTor"+ chatRoomActor)
//    println("pathname"+chatRoomActor.path.name)
  }

//
//  def SendMessage(message: ChatRoomActor.ChatMessage) : Unit  = chatRoomActor ! message
}

object ChatRoom{
  def apply(roomId:Int) = new ChatRoom(roomId)


}

