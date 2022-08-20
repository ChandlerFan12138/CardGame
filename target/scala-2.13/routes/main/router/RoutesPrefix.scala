// @GENERATOR:play-routes-compiler
// @SOURCE:D:/ITSD-DT2022-Template/conf/routes
// @DATE:Sun Feb 06 10:47:30 GMT 2022


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
