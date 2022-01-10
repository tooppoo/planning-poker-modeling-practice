package philomagi.dddcj.modeling.planning_poker.core
package lib.extension

protected[core] object StringExtension {
  implicit class StringMaybeBlank(str: String) {
    lazy val nonBlank: Boolean = !"^[\\s　]+$".r.matches(str)
  }

  implicit class StringMaybeOverLimit(str: String) {
    def within(n: Int): Boolean = str.length <= n
  }

  implicit class StringMaybeContainInvalidChar(str: String) {
    def notContains(chars: Seq[Char]): Boolean = !chars.exists(str.contains(_))
  }

  /**
   * @see https://datatracker.ietf.org/doc/html/rfc4122#section-3
   */
  implicit class StringLikeUUID(str: String) {
    private[this] val hexDigit = "[0-9a-fA-F]"
    private[this] val hexOctet = s"($hexDigit$hexDigit)"

    private[this] val timeLow = s"$hexOctet{4}"
    private[this] val timeMid = s"$hexOctet{2}"
    private[this] val timeHighAndVersion = s"$hexOctet{2}"

    private[this] val clockSeqAndReserved = hexOctet
    private[this] val clockSeqLow = hexOctet

    private[this] val node = s"$hexOctet{6}"

    private[this] val format = s"^$timeLow-$timeMid-$timeHighAndVersion-$clockSeqAndReserved$clockSeqLow-$node$$"

    lazy val likeUUID: Boolean = format.r.matches(str)
  }
}
