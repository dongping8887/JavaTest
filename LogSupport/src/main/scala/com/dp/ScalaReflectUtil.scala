package com.dp

/**
  * scala反射工具类
  * Created by dongp on 2017/7/18.
  */
object ScalaReflectUtil {
  val ru = scala.reflect.runtime.universe
  val m = ru.runtimeMirror(getClass.getClassLoader)

//
//  def getFieldValue(obj: Any, fieldName: String): Any ={
//    val im = m.reflect(obj)
//    val fieldX = ru.typeOf[TestScala1].declaration(ru.newTermName(fieldName)).asTerm.accessed.asTerm
//    val fmX = im.reflectField(fieldX)
//    fmX.get
//  }
}
