# starfleet
programming exercise

# narrative
I read the synopsis several times in order to understand conceptually the mechanics of the simulation. I found this problem easier to approach once I had a good (visual) model off of which to base my assumptions. Since the problem domain reflected a coordinate/vector aspect I naturally gravitated towards this representation as a potential solution.

After reading the requirements I was able to break down the exercise into several tasks, which included the following:
* modeling of the simulation data
* ingesting file input
* performing a single update of simulation state
* displaying the current state
* assessing script/score

I prefer to begin with the more difficult aspects of a probem, so modeling the state was the first task at hand. I elected to devise a typeclass design/implementation in order to generalize the management of coordinate data. This would permit simple operations such as composition, negation and comparison to be performed regardless of how the coordinate data is encapsulated. In theory this would make the solution extensible to types other than the nominal tuple/triple.

The next task I took on was updating the simulation state, namely tracking the movements of the vessel with respect to the mine field. Since the mines never move/mutate, it made sense to perform translations of existing objects with respect to the vessel's field of view. The translation is idempotent and no state is maintained on the mines, ensuring that the calculation is consistent and free of side effects. Although the ship moves after each step, I chose to also treat the vessel in an immutable fashion and generate a new location/vessel for each iteration.

The "collision detection" uses the methods available in the typeclass to determine coincidence and compare magnitude. Once the firing pattern is mapped as offsets to the ship location, these values are compared against the immutable mine values. Since this operation does not change state it can be repeated on the same value set and returns the same results. This allowed me to check the correctness of the simulation by being able to replay intermediate states and viewing the results.

# building & running
The project is not set up with a proper build configuration (e.g., sbt) but can simple be built on the command line using scalac and ran with scala binaries. For compiling:

`scalac -d classes src/com/boosed/*`

The main routine can be run using the command:

`scala -classpath classes com.boosed.Starfleet <field file> <script file>`
