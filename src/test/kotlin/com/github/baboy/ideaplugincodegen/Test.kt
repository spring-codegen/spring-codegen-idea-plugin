package com.github.baboy.ideaplugincodegen

import com.github.baboy.ideaplugincodegen.gen.FieldUtils

/**
 *
 * @author zhangyinghui
 * @date 2023/8/7
 */
class Test {
}
fun main(args:Array<String>){
    var s = FieldUtils.propertyName("api_ex_code")
    System.out.println("s:"+s)
    s = FieldUtils.getter("api")
    System.out.println("s:"+s)
    s = FieldUtils.setter("api_ex_code")
    System.out.println("s:"+s)
}