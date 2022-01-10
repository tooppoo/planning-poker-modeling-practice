package philomagi.dddcj.modeling.planning_poker.core.domain.table.model

import org.scalatest.funspec.AnyFunSpec
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.card.model.Card
import philomagi.dddcj.modeling.planning_poker.core.domain.role.model.Role
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table.CardOnTable

class TableTest extends AnyFunSpec {
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
          override def tableOwner: Attendance = Attendance(
            Attendance.Id("sut"),
            Attendance.Name("SUT"),
            rolesOfOwner
          )

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
  trait UseDummyTable {
    def tableId: Table.Id = Table.Id("dummy-table")
    def tableOwner: Attendance

    def table: Table = Table(tableId, tableOwner)
  }
  trait UseDummyTableOwner {
    def rolesOfOwner: Seq[Role] = Seq(Role.Facilitator)

    def tableOwner: Attendance = Attendance(
      Attendance.Id("dummy-owner-id"),
      Attendance.Name("dummy-owner-name"),
      rolesOfOwner
    )
  }
  trait UseDummyPlayer {
    def rolesOfPlayer: Seq[Role] = Seq(Role.Player)

    def player: Attendance = Attendance(
      Attendance.Id("dummy-player"),
      Attendance.Name("dummy-player"),
      rolesOfPlayer
    )
  }
  trait AfterJoinToTable {
    def player: Attendance
    def table: Table

    def tableAfterJoin: Table = table.accept(player).fold(
      e => fail(e.getMessage),
      t => t
    )
  }
}
