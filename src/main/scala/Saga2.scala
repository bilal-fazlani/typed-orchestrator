package saga2

trait Saga[-I, +E, A] {
  def andThen[I2 <: A, E2, B](s: Saga[I2, E2, B]): Saga[I, E | E2, A & B] = ???

  def decide[I2, E2, B, I3, E3, C](
      predicate: A => Boolean
  )(
      s1: Saga[I2, E2, B],
      s2: Saga[I3, E3, C]
  ): Saga[I & I2 & I3, E | E2 | E3, (A & B) | (A & C)] = ???

  def failIf[E2](predicate: A => Boolean)(error: E2): Saga[I, E | E2, A] = ???

  def recover[I2, E2, B](f: E => Saga[I2, E2, B]): Saga[I & I2, E2, A | B] = ???

  def requires[I2]: Saga[I & I2, E, A & I2] = ???

  def provide(i: I): Saga[Nothing, E, A] = ???
}

object Saga {
  def unit: InMem[Nothing, Nothing, Unit] = InMem.Success(())
  def requires[I]: InMem[I, Nothing, I] = ???
  def success[A](v: A): InMem[Nothing, Nothing, A] = InMem.Success(v)
  def fail[E](e: E): InMem[Nothing, E, Nothing] = InMem.Error(e)
  def fromFunction[I, E, A](f: I => Either[E, A]): Saga[I, E, A] = ???
}

enum InMem[-I, +E, A] extends Saga[I, E, A]:
  case Success(a: A)
  case Error(e: E)
