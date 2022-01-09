package philomagi.dddcj.modeling.planning_poker.core.attendance.lib.extension

protected[attendance] object StringExtension {
  implicit class StringMaybeBlank(str: String) {
    lazy val nonBlank: Boolean = !"^[\\s　]+$".r.matches(str)
  }
  implicit class StringMaybeOverLimit(str: String) {
    def within(n: Int): Boolean = str.length <= n
  }
  implicit class StringMaybeContainInvalidChar(str: String) {
    def notContains(chars: Seq[Char]): Boolean = !chars.exists(str.contains(_))
  }
}
