package philomagi.dddcj.modeling.planning_poker.core.attendance.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.prop.TableDrivenPropertyChecks
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance

class AttendanceTest extends AnyFunSpec with TableDrivenPropertyChecks {
  describe("Id") {
    describe("generate") {
      describe("with valid id") {
        val testCases = Table(
          ("バリエーション", "ID文字列"),
          ("半角英1文字", "a"),
          ("半角数1文字", "5"),
          ("半角英のみ", "cicada"),
          ("半角数のみ", "3301"),
          ("半角英数のみ", "cicada3301"),
          ("半角英数 + ハイフン", "cicada-3301"),
          ("半角英数 + アンダーバー", "cicada_3301"),
          ("半角英数 + ハイフン + アンダーバー", "cicada-_3301"),
          ("ID長上限", "a" * Attendance.Id.MaxLength),
        )

        forAll(testCases) { (caseLabel, idValue) =>
          describe(s"$caseLabel の場合") {
            describe(s"id = \"$idValue\"") {
              it("should generate id object") {
                assert(Attendance.Id(idValue).value == idValue)
              }
            }
          }
        }
      }
      describe("with invalid id") {
        val testCases = Table(
          ("バリエーション", "ID文字列"),
          ("空ID", ""),
          ("ID長違反", "a" * (Attendance.Id.MaxLength + 1)),
          ("半角空白のみ", "  "),
          ("全角を含む", "cicadaてすと3310"),
          ("半角空白を含む", "cicada 3310"),
          ("全角空白を含む", "cicada　3310"),
          ("記号を含む", "cicada/3310"),
        )

        forAll(testCases) { (caseLabel, invalidId) =>
          describe(s"$caseLabel の場合") {
            describe(s"id = \"$invalidId\"") {
              it("should raise exception") {
                assertThrows[IllegalArgumentException] {
                  Attendance.Id(invalidId)
                }
              }
            }
          }
        }
      }
    }
  }

  describe("Name") {
    describe("generate") {
      describe("with valid name") {
        val testCases = Table(
          ("バリエーション", "名前"),
          ("半角1文字", "a"),
          ("全角1文字", "松"),
          ("半角上限", "a" * Attendance.Name.MaxLength),
          ("全角上限", "あ" * Attendance.Name.MaxLength),
          ("半角空白あり", "長宗我部 元親"),
          ("全角空白あり", "長宗我部　元親"),
          ("半角 + 全角", "伊藤 Mancio"),
        )

        forAll(testCases) { (caseLabel, name) =>
          describe(s"$caseLabel") {
            describe(s"name = $name") {
              it("should generate name object") {
                assert(Attendance.Name(name).value == name)
              }
            }
          }
        }
      }
      describe("with invalid name") {
        val testCases = Table(
          ("バリエーション", "名前"),
          ("空文字", ""),
          ("半角上限超", "a" * (Attendance.Name.MaxLength + 1)),
          ("全角上限超", "あ" * (Attendance.Name.MaxLength + 1)),
          ("半角空白のみ", "  "),
          ("全角空白のみ", "　　"),
          ("<混入", "伊藤 < Mahcio"),
          (">混入", "伊藤 > Mahcio"),
        )

        forAll(testCases) { (caseLabel, name) =>
          describe(s"$caseLabel") {
            describe(s"name = $name") {
              it("should not generate name object") {
                assertThrows[IllegalArgumentException] {
                  Attendance.Name(name)
                }
              }
            }
          }
        }
      }
    }
  }
}
