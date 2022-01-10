package philomagi.dddcj.modeling.planning_poker.core.domain.table.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.prop.TableDrivenPropertyChecks
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.card.model.Card
import philomagi.dddcj.modeling.planning_poker.core.domain.table
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table.CardOnTable
import philomagi.dddcj.modeling.planning_poker.core.test_helper.TestHelper.{AfterJoinToTable, UseDummyPlayer, UseDummyTable, UseDummyTableOwner}

class TableTest extends AnyFunSpec with TableDrivenPropertyChecks {
  describe("Id") {
    describe("generate") {
      describe("with valid id") {
        it("should generate id object") {
          val idValue = "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"

          assert(table.model.Table.Id(idValue).value == idValue)
        }
      }
      describe("with invalid id") {
        val testCases = Table(
          ("バリエーション", "ID文字列"),
          ("空ID", ""),
          ("半角空白のみ", "  "),
          ("全角を含む", "cicadaてすと3310"),
          ("半角空白を含む", "cicada 3310"),
          ("全角空白を含む", "cicada　3310"),
          ("-以外の記号を含む", "cicada/3310"),
          ("非UUID形式", "f8-7-1-a-00"),
        )

        forAll(testCases) { (caseLabel, invalidId) =>
          describe(s"$caseLabel の場合") {
            describe(s"id = \"$invalidId\"") {
              it("should raise exception") {
                assertThrows[IllegalArgumentException] {
                  table.model.Table.Id(invalidId)
                }
              }
            }
          }
        }
      }
    }
  }

  describe("テーブルへの参加") {
    describe("未参加") {
      it("参加できること") {
        new UseDummyTable with UseDummyTableOwner with UseDummyPlayer {
          val newAttendance: Attendance = player

          assert(table.accept(newAttendance).isRight)
        }
      }
      it("参加後のテーブルで人数が増えていること") {
        new UseDummyTable with UseDummyTableOwner with UseDummyPlayer {
          val newAttendance: Attendance = player

          table.accept(newAttendance).fold(
            e => fail(e.getMessage),
            tableAfterAccept => {
              assert(tableAfterAccept.attendances == Seq(tableOwner, newAttendance))
            }
          )
        }
      }
    }
    describe("参加済み") {
      it("二重に参加できないこと") {
        new UseDummyTable with UseDummyTableOwner {
          assert(table.accept(tableOwner).isLeft)
        }
      }
    }
  }

  describe("カードを置く") {
    val newCard: Card = Card(Card.Suite("1"))

    describe("テーブル未参加") {
      it("カードを置くことができないこと") {
        new UseDummyTable with UseDummyTableOwner with UseDummyPlayer {
          assert(table.put(newCard, by = player).isLeft)
        }
      }
    }
    describe("テーブル参加済み") {
      describe("初めて置く") {
        it("カードを置くことができること") {
          new UseDummyTable with UseDummyTableOwner with UseDummyPlayer with AfterJoinToTable {
            assert(tableAfterJoin.put(newCard, by = player).isRight)
          }
        }
        it("置いた後はカードの枚数が増えること") {
          new UseDummyTable with UseDummyTableOwner with UseDummyPlayer with AfterJoinToTable {
            tableAfterJoin.put(newCard, by = player).fold(
              e => fail(e.getMessage),
              tableAfterPut => {
                assert(tableAfterPut.cards == Seq(CardOnTable(player, newCard)))
              }
            )
          }
        }
      }
      describe("2回目に置く") {
        it("カードを置くことができること") {
          new UseDummyTable with UseDummyTableOwner with UseDummyPlayer with AfterJoinToTable {
            for {
              t <- tableAfterJoin.put(Card(Card.Suite("8")), by = player)
            } {
              assert(t.put(newCard, by = player).isRight)
            }
          }
        }
        it("1回分しかカードの枚数が増えないこと") {
          new UseDummyTable with UseDummyTableOwner with UseDummyPlayer with AfterJoinToTable {
            for {
              t <- tableAfterJoin.put(Card(Card.Suite("8")), by = player)
              t <- t.put(newCard, by = player)
            } {
              assert(
                t.cards == Seq(CardOnTable(player, newCard))
              )
            }
          }
        }
      }
    }
  }
}
