import com.dp.LogSupport.ProxyBuilder
import com.dp.{Logging2, MbbService}
import org.apache.spark.LogMethodIntoAndLeave;

/** *
  * 测试scala
  */

object TestScala1 extends Logging2 {


  def main(args: Array[String]): Unit = {
    LogMethodIntoAndLeave.loadPackages();

//    val sparkConf = new SparkConf().setMaster("local").setAppName("sdf")
//    val sc = new SparkContext(sparkConf)
//    var cloudAirService = new CloudAirService()
//    cloudAirService = new ProxyBuilder().buildProxy(cloudAirService).asInstanceOf[CloudAirService]
//    cloudAirService.resetTask()
//    cloudAirService.stopTask()

    var mbbservice = new MbbService()
    mbbservice = new ProxyBuilder().buildProxy(mbbservice).asInstanceOf[MbbService]
//    mbbservice.resetTask()
    mbbservice.startTask()
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

