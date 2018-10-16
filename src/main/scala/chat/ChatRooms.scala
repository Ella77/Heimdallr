package chat
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory



object ChatRooms {

  var  chatRooms : Map[Int, ChatRoom] = Map.empty[Int,ChatRoom]

  def findOrCreate(number:Int):
    ChatRoom = chatRooms.getOrElse(number, createNewChatRoom(number))

  private def createNewChatRoom(number: Int): ChatRoom = {
    val chatroom = ChatRoom(number)
    chatRooms += number -> chatroom
    chatroom

  }

}
