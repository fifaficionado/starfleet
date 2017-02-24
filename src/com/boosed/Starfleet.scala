/**
 * programming exercise
 */
package com.boosed

import com.boosed.Location._
import scala.collection.immutable.Range
import scala.io.Source
import java.io.FileNotFoundException
import java.io.IOException

object Starfleet {
  
  // movements (each one includes drop)
  val north = (0, -1, -1)
  val south = (0, 1, -1)
  val east = (1, 0, -1)
  val west = (-1, 0, -1)
  val drop = (0, 0, -1)
  
  def getMovement(move: String) = move match {
    case "north" => north
    case "south" => south
    case "east" => east
    case "west" => west
    // default case is drop
    case _ => drop
  }

  /** calculate one or more offsets from given location(s) */
  def move[T: VectorLike](offset: T, locations: T*) = {
    val ev = implicitly[VectorLike[T]]
    locations.map(ev.add(offset, _))
  }
  
  /** collision occurs when vectors are coincident and projectile > magnitude to target */
  def collision[T: VectorLike](projectile: T, target: T): Boolean = {
    val ev = implicitly[VectorLike[T]]
    ev.coincident(projectile, target) && (ev.compare(projectile, target) > 0)
  }
  
  // firing patterns
  val alpha = List((-1, -1, 0), (-1, 1, 0), (1, -1, 0), (1, 1, 0))	// x
  val beta = List((-1, 0, 0), (0, -1, 0), (0, 1, 0), (1, 0, 0))		// +
  val gamma = List((-1, 0, 0), (0, 0, 0), (1, 0, 0))				// center & side-to-side
  val delta = List((0, -1, 0), (0, 0, 0), (0, 1, 0))				// center & up-and-down
  
  def getPattern(pattern: String) = pattern match {
    case "alpha" => alpha
    case "beta" => beta
    case "gamma" => gamma
    case "delta" => delta
    // default case is no pattern offset
    case _ => Nil
  }

  /** partition mines into cleared and remaining groups */
  def fire[T: VectorLike](ship: T)(pattern: T*)(mines: T*) = {
    // get firing pattern
    val torpedoes = move(ship, pattern: _*)
    
    // partition mines into those cleared by torpedoes and those remaining (4*n -> linear performance)
    mines.partition(mine => torpedoes.exists(collision(_, mine)));
  }

  /** display simulation state */
  def display(ship: Coordinates, mines: Coordinates*) {
    Cuboid.ship = ship
    Cuboid.mines = mines
    Cuboid.render()
    println(Cuboid + "\n")
  }

  /** returns a list of mines as Coordinates */
  def ingestField(file: String) = try {
    val lines = Source.fromFile(file).getLines.zipWithIndex.toList
    
    // create mines
    val mines = for (line <- lines; char <- line._1.zipWithIndex; if char._1 != '.')
      yield (line._2, char._2, if (char._1.isUpper) -(char._1 - '&') else -(char._1 - '`'))
      
    // ship is at center of field
    val ship = (lines(0)._1.length/2, lines.length/2, 0)
    
    // return as a tuple
    ship -> mines
  } catch {
    case ex: IOException => println(s"""could not access file: "$file""""); throw ex
    case ex: FileNotFoundException => println(s"""could not find file: "$file""""); throw ex
  }

  /** returns (volley, movement) pair zipped with the named instruction */
  def ingestScript(file: String) = try {
    val lines = Source.fromFile(file).getLines.toList
    // movements and volleys
    (for (line <- lines) yield line.trim.split("\\s+") match {
      // blank line
      case Array("") => Nil -> drop
      
      // single argument (optional move or volley)
      case Array(arg) => arg match {
        case "alpha" => alpha -> drop
        case "beta" => beta -> drop
        case "gamma" => gamma -> drop
        case "delta" => delta -> drop
        case "north" => Nil -> north
        case "south" => Nil -> south
        case "east" => Nil -> east
        case "west" => Nil -> west
        case _ => Nil -> drop
      }
      
      // both arguments
      case Array(arg0, arg1) => getPattern(arg0) -> getMovement(arg1)
        
      // too many args, both no-ops
      case _ => Nil -> drop
    }).toList.zip(lines)
  } catch {
    case ex: IOException => println(s"""could not access file: "$file""""); throw ex
    case ex: FileNotFoundException => println(s"""could not find file: "$file""""); throw ex
  }
  
  val usage = """usage: Starfleet <field file> <script file>"""

  /**
   * main
   */
  def main(args: Array[String]): Unit = args match {
    case Array(field, script) =>
      var (ship, mines) = ingestField(field)
      val movements = ingestScript(script)
      
      // scoring
      val count = mines.length
      var shots = 0
      var moves = 0
      
      for ((((volley, movement), instruction), index) <- movements.zipWithIndex) {        
        // display step
        println(s"Step ${index + 1}\n")
        
        // display (before)
        display(ship, mines: _*)
        
        // display instruction
        println(s"$instruction\n")
        
        // partition mines into those cleared by torpedoes and those remaining (4*n -> linear performance)
        if (!volley.isEmpty) shots += 1
        val (_, remaining) = fire(ship)(volley: _*)(mines: _*)
        mines = remaining.toList
        
        // move ship
        if (movement != drop) moves += 1
        ship = move(movement, ship).head

        // display (after)
        display(ship, mines: _*)
        
        // check game state
        mines match {
          // all mines cleared, steps remaining
          case Nil if (index < (movements.length - 1)) => println("pass (1)"); return
          
          // passed a mine
          case rv if rv.exists(_._3 == ship._3) => println("fail (0)"); return
          
          // continue
          case _ => /* do nothing */
        }
      }
      
      // final score
      if (mines.isEmpty) {
        var score = count * 10
        score -= Math.min(count * 5, shots * 5)
        score -= Math.min(count * 3, moves * 2)
        println(s"pass ($score)")
      } else println("fail (0)")
      
    case _ => println(usage)
  }
}