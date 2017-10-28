package com.dp

import com.dp.LogSupport.{ILogClass, ILogMethodNot}

/**
  * Created by dongp on 2017/7/19.
  */
@ILogClass
class MbbService extends Logging2 {

  @ILogMethodNot
  def startTask(){logInfo("this is startTask")}

  def stopTask(){}

  def resetTask(){logInfo("this is resetTask")}

  def close(){}

}
