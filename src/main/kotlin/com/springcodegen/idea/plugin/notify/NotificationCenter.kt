package com.springcodegen.idea.plugin.notify

import kotlin.concurrent.thread

object NotificationCenter {
    var events:MutableMap<String, MutableList<Handler>> = HashMap()
    @JvmStatic fun register(event:String, handler: Handler):Boolean{
        if ( events[event] == null ){
            events[event] = ArrayList()
        }
        events[event]!!.add(handler)
        return true
    }
    @JvmStatic fun unregister(event:String, handler: Handler){
        if ( events[event] == null ){
            return
        }
        events[event]!!.remove(handler)
    }
    @JvmStatic @Synchronized fun sendMessage(event:String, data:Any?){
        println("sendMessage:$event")
        if ( events[event] == null ){
            return
        }
        thread {
            events[event]!!.forEach{ it.handleMessage(Message(event, data))}
        }
    }
    public interface Handler{
        public fun handleMessage(msg:Message);
    }
    public class Message(var envent:String, var data:Any?){
    }
}