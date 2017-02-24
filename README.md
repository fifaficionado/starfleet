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

The "collision detection" uses the methods available in the typeclass to determine coincidence and compare magnitude. Once the firing pattern is mapped as offsets to the ship location, these values are compared against the immutable mine values. Since this operation does not change state it can be repeated on the same value set and returns the same results. This allowed me to check the correctness of the simulation by being able to replay intermediate states and viewing the results. I entertained the idea of storing the mine values in a grid to manage collisions, but this proved cumbersome and didn't effectively improve performance (linear).

Once the collision was established I could easily filter the mine elements per iteration. Since the firing sequences are not depicted in the display, the collisions are computed for the current volley and the remaining mines with respect to the vessel's new position are captured in the cuboid. The cuboid was designed as a singleton to represent a single output component for the purposes of rendering a field of view based on current inputs.

Next came the ingest of file data representing the field state and script. These were pretty straightforward transformations, though the field ingestion does not handle "out of range" characters. This could likely be improved by either assuming that the unknown symbol represents space or throwing an ingest error. The script ingest was more straightforward in that unrecognized tokens were converted to no-ops (i.e., drop for movement and empty pattern for volley). 

Once the inputs were successfully parsed, the final task involved performing a single update cycle and script assessment. From the output specification the update implementation was straightforward, with a minor detail added to track the number of volleys and movements made. To keep track within the loop without using a counter variable, I was able to conveniently leverage zipWithIndex.

Though I spent close to 6 hours coding, I did spend time throughout the day considering different ways to structure and process the simulation. It is my tendency to muse on about problems as I go about my day to arrive at novel solutions and test these theories through code. Though I may often arrive at a workable solution I typically refactor to achieve satisfactory readability and concision.

# building & running
The project is not set up with a proper build configuration (e.g., sbt) but can simple be built on the command line using scalac and ran with scala binaries. For compiling:

`scalac -d classes src/com/boosed/*`

The main routine can be run using the command:

`scala -classpath classes com.boosed.Starfleet <field file> <script file>`
