package com.dp

import org.apache.spark.{LogUtils, Logging}

/**
  * Created by dongp on 2017/7/19.
  */
trait Logging2 extends Logging {
  final val BEGIN = "Begin"
  final val END = "End"
  LogUtils.initLog(this)
}
