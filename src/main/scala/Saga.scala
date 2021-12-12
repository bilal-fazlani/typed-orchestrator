package saga1

trait Saga[+E, +A] {
  def andThen[E2, B](s: Saga[E2, B]): Saga[E | E2, A & B] = ???
  
  def decide[E2, B, E3, C](
      predicate: A => Boolean
  )(s1: Saga[E2, B], s2: Saga[E3, C]): Saga[E | E2 | E3, (A & B) | (A & C)] =
    ???
}
