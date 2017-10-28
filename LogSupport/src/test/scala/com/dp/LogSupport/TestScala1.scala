package com.dp.LogSupport

import com.dp.{CloudAirService, Logging2}
import org.apache.spark.LogMethodIntoAndLeave;

/** *
  * 测试scala
  */

object TestScala1 extends Logging2 {


  def main(args: Array[String]): Unit = {
    LogMethodIntoAndLeave.loadPackages();

//    val sparkConf = new SparkConf().setMaster("local").setAppName("sdf")
//    val sc = new SparkContext(sparkConf)
    var mbbservice = new CloudAirService()
    mbbservice = new ProxyBuilder().buildProxy(mbbservice).asInstanceOf[CloudAirService]
    mbbservice.resetTask()
//    logInfo(sc.toString)
//    logInfo("hello world", new IllegalArgumentException)
//    logInfo("hello world")
//
//    logDebug("hello world")
//    logError("hello world")

//   ss("")
  }

  def ss( aa: String): Unit = {
    logInfo("我是ss")
  }

}

