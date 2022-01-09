package philomagi.dddcj.modeling.planning_poker.core.domain.card.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.prop.TableDrivenPropertyChecks

class CardTest extends AnyFunSpec with TableDrivenPropertyChecks {
  describe("Suite") {
    describe("generate") {
      describe("with valid name") {
        val testCases = Table(
          ("バリエーション", "スート"),
          ("半角1文字", "8"),
          ("全角1文字", "大"),
          ("半角上限", "S" * Card.Suite.MaxLength),
          ("全角上限", "小" * Card.Suite.MaxLength),
          ("半角空白あり", "H 1"),
          ("全角空白あり", "H　1"),
          ("半角 + 全角", "大 8"),
        )

        forAll(testCases) { (caseLabel, suite) =>
          describe(s"$caseLabel") {
            describe(s"name = $suite") {
              it("should generate name object") {
                assert(Card.Suite(suite).label == suite)
              }
            }
          }
        }
      }
      describe("with invalid name") {
        val testCases = Table(
          ("バリエーション", "スート"),
          ("空文字", ""),
          ("半角上限超", "a" * (Card.Suite.MaxLength + 1)),
          ("全角上限超", "あ" * (Card.Suite.MaxLength + 1)),
          ("半角空白のみ", "  "),
          ("全角空白のみ", "　　"),
          ("<混入", "大 < 8"),
          (">混入", "大 > 8"),
        )

        forAll(testCases) { (caseLabel, suite) =>
          describe(s"$caseLabel") {
            describe(s"name = $suite") {
              it("should not generate name object") {
                assertThrows[IllegalArgumentException] {
                  Card.Suite(suite)
                }
              }
            }
          }
        }
      }
    }
  }
}
