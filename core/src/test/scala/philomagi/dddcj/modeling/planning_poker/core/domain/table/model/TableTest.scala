package philomagi.dddcj.modeling.planning_poker.core.domain.table.model

import org.scalatest.funspec.AnyFunSpec
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.role.model.Role

class TableTest extends AnyFunSpec {
  describe("テーブルへの参加") {
    describe("未参加") {
      val newAttendance: Attendance = Attendance(
        Attendance.Id("sut"),
        Attendance.Name("SUT"),
        Seq.empty
      )

      it("参加できること") {
        new UseDummyTable with UseDummyTableOwner {
          assert(table.accept(newAttendance).isRight)
        }
      }
      it("参加後のテーブルで人数が増えていること") {
        new UseDummyTable with UseDummyTableOwner {
          table.accept(newAttendance).fold(
            e => fail(e.getMessage),
            tableAfterAccept => {
              assert(tableAfterAccept.attendances.length == table.attendances.length + 1)
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
}
