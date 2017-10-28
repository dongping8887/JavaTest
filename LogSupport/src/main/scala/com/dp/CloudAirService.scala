package com.dp

import com.dp.LogSupport.ILogMethod

/**
  * Created by dongp on 2017/7/19.
  */
class CloudAirService extends Logging2 {

  @ILogMethod
  def startTask() {}

  def stopTask() {logInfo("this is stopTask")}

  @ILogMethod
  def resetTask(){logInfo("this is resetTask")}

  def close() {}

}
