//package com.redis
//
//
//import scala.concurrent.ExecutionContext
//import com.redis.{RedisClient, PubSubMessage,S, U, M}
//import akka.persistence.redis._
//import akka.actor.{Actor, ActorSystem, Props}
//
//
//sealed trait PubSubMessage
//case class S(channel:String, numberSubscribed : Int) extends PubSubMessage
//case class U(channel:String, numberSubscribed: Int) extends PubSubMessage
//case class M(origChannel:String,message:String) extends PubSubMessage
//
//object Pub {
//  println("starting publishing")
//  val r = new RedisClient("localhost", 6379)
//  val p = new Publisher(r)
//  p.start
//
//
//  def publish(channel: String, message: String) = {
//    println("message publish on channel")
//    p ! Publish(channel, message)
//  }
//}
//
//
//object Sub{
//  println("starting subscribing")
//  val r= new RedisClient("localhost", 6379)
//  val s =  actorOf(new Subscriber(r))
//  s.start
//  s ! Register(callback)
//
//  def sub(channels: String*) = {
//    s ! Subscribe(channels.toArray)
//  }
//  def unsub(channels: String*) = {
//    s ! Unsubscribe(channels.toArray)
//  }
//  def callback(pubsub : PubSubMessage) = pubsub match {
//    case S(channel, number) => println("subscribed to"+ channel+"number"+ number )
//    case U(channel, number) => println("unsubscribed to"+ channel+"number"+ number )
//    case M(channel, msg) =>
//      msg match {
//        case "exit" =>
//          println("exited, unsubscribe all")
//          r.unsubscribe
//      }
//    case x if x startsWith "+" =>
//      val s: Seq[Char] = x
//      s match {
//        case Seq('+', rest @ _*) => r.subscribe(rest.toString){ m => }
//      }
//    case x if x startsWith "-" =>
//      val s : Seq[Char] = x
//      s match {
//        case Seq('+', rest @ _*) => r.unsubscribe(rest.toString){ m => }
//      }
//    case x =>
//      println("received msg on "+ channel +"with : "+x)
//
//  }
//}

//
//sealed trait Msg
//case class Subscribe(channels: Array[String]) extends Msg
//case class Register(callback: PubSubMessage => Any) extends Msg
//case class Unsubscribe(channels: Array[String]) extends Msg
//case object UnsubscribeAll extends Msg
//case class Publish(channel: String, msg: String) extends Msg
//
//class Subscriber(client: RedisClient) extends Actor {
//  var callback: PubSubMessage => Any = { m => }
//
//  def receive = {
//    case Subscribe(channels) =>
//      client.subscribe(channels.head, channels.tail: _*)(callback)
//      self.reply_?(true)
//
//    case Register(cb) =>
//      callback = cb
//      self.reply_?(true)
//
//    case Unsubscribe(channels) =>
//      client.unsubscribe(channels.head, channels.tail: _*)
//      self.reply_?(true)
//
//    case UnsubscribeAll =>
//      client.unsubscribe
//      self.reply_?(true)
//  }
//}
//
//class Publisher(client: RedisClient) extends Actor {
//  def receive = {
//    case Publish(channel, message) =>
//      client.publish(channel, message)
//      self.reply_?(true)
//  }
//}

//object ChatRoomActor {
//  case object Join
//  case class ChatMessage(message: String)
//}
//
//class ChatRoomActor extends Actor {
//  implicit val executionContext: ExecutionContext = context.dispatcher
//
//  import ChatRoomActor._
//  var users: Set[ActorRef] = Set.empty
//
//
//
//  def receive = {
//    case Join =>
//      users += sender()
//      // we also would like to remove the user when its actor is stopped
//      context.watch(sender())
//
//    case Terminated(user) =>
//      users -= user
//
//    case msg: ChatMessage =>
//      users.foreach(_ ! msg)
//  }
//}


