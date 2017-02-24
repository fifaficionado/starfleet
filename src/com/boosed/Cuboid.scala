package com.boosed

import com.boosed.Location._

/** Cuboid simulation field */
object Cuboid {
  // field
  private[this] var field: Array[Array[Char]] = _

  // ship
  var ship: Coordinates = _

  // mines
  var mines: Seq[Coordinates] = _

  /** retrieve field of view where ship is translated to center */
  private[this] def fov[T: VectorLike](center: T, ship: T, mines: T*) = {
    // ship + translation = center, translation = center - ship
    val ev = implicitly[VectorLike[T]]
    val translation = ev.add(center, ev.negate(ship))
    mines.map(ev.add(translation, _))
  }

  // formatting mine output
  private[this] val format = (_: Coordinates) match {
    case (_, _, 0) => '*'
    case (_, _, z) if z < -26 => ('&' - z).asInstanceOf[Char]
    case (_, _, z) => ('`' - z).asInstanceOf[Char]
  }

  def render() = mines match {
    case _ :: _ =>
      // center of cuboid
      val center = (mines.map(l => Math.abs(l._1 - ship._1)).max,
        mines.map(l => Math.abs(l._2 - ship._2)).max,
        0)

      // full size is 2x the offset + 1, assuming (0, 0) is top-left corner
      field = Array.fill(center._2 * 2 + 1, center._1 * 2 + 1)('.')

      // render mines, place in grid
      fov(center, ship, mines: _*) foreach { mine => field(mine._2)(mine._1) = format(mine) }

    // no mines, print empty field
    case Nil => field = Array.fill(1, 1)('.')
  }

  override def toString() = field.map(new String(_)).mkString("\n")
}