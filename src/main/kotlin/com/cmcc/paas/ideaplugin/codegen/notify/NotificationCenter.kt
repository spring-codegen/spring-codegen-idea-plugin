package com.cmcc.paas.ideaplugin.codegen.notify

import kotlin.concurrent.thread

object NotificationCenter {
    var events:MutableMap<String, MutableList<Handler>> = HashMap()
    fun register(event:String, handler: Handler):Boolean{
        if ( events[event] == null ){
            events[event] = ArrayList()
        }
        events[event]!!.add(handler)
        return true
    }
    fun unregister(event:String, handler: Handler){
        if ( events[event] == null ){
            return
        }
        events[event]!!.remove(handler)
    }
    fun sendMessage(event:String, msg:Any){
        if ( events[event] == null ){
            return
        }
        thread {
            events[event]!!.forEach{ it.handleMessage(msg)}
        }
    }
    public interface Handler{
        public fun handleMessage(msg:Any);
    }
}