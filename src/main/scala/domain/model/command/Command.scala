package philomagi.dddcj.modeling.planning_poker
package domain.model.command

import domain.model.member.Member
import domain.model.member.Member.Role
import domain.model.table.Table

trait Command {
  val actor: Member
  val requiredRole: Option[Role]

  final def run(at: Table): Either[Exception, Table] = requiredRole.fold[Either[Exception, Table]](
     runImpl(at)
  )(
    (required: Role) => if (actor.has(required)) {
      runImpl(at)
    } else {
      Left(
        new Exception(
          s"$actor not has role ${requiredRole.getClass.toString} to run ${getClass.toString}"
        )
      )
    }
  )

  protected[this] def runImpl(at: Table): Either[Exception, Table]
}
object Command {
  def dispatch(command: Command, table: Table): Either[Exception, Table] = command.run(table)

  trait Dispatcher {
    def dispatch(command: Command, table: Table): Either[Exception, Table]
  }
  object Dispatcher {
    object NoPersistenceDispatcher extends Dispatcher {
      override def dispatch(command: Command, table: Table): Either[Exception, Table] = command.run(table)
    }
  }
}
