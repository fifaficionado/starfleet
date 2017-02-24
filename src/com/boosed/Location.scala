package com.boosed

import java.util.Comparator

/** type class for values that can be added and have equality */
object Location {
  
  // type alias
  type Coordinates = Tuple3[Int, Int, Int]

  /**
   * typeclass for types which encapsulate vector characteristics
   */
  trait VectorLike[T] extends Comparator[T] {
    def add(a: T, b: T): T 				// addition of vectors
    //def delete(a: T, b: T): T			// deletion of vectors
    def negate(a: T): T					// negative of vector
    def coincident(a: T, b: T): Boolean // determine if a & b are coincident
    def getCoordinates(a: T): Tuple3[Int, Int, Int]
  }

  object VectorLike {
    /**
     * local type class implementation of Vector for Coordinates alias
     */
    implicit object VectorLikeCoorindates extends VectorLike[Coordinates] {
      override def add(a: Coordinates, b: Coordinates) = (a._1 + b._1, a._2 + b._2, a._3 + b._3)
      //override def delete(a: Coordinates, b: Coordinates) = (a._1 - b._1, a._2 - b._2, a._3 - b._3)
      override def negate(a: Coordinates) = (-a._1, -a._2, -a._3)
      override def coincident(a: Coordinates, b: Coordinates) = a._1 == b._1 && a._2 == b._2
      override def getCoordinates(a: Coordinates) = a
      // comparison of magnitude
      override def compare(a: Coordinates, b: Coordinates) = a._3.compareTo(b._3)
    }
  }
}