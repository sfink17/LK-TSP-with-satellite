import java.util.*

/**
 * Created by Simon on 2/20/2017.
 */

/**
 * Generic tour object with output functions. Inherits from [MutableList]
 *
 * @property pointList [Point] list the tour is based upon.
 * @property masterKD KD tree used in nearest neighbor.
 * @property distance Running measure of tour value.
 */

class Tour(pointList: MutableList<Point>, var masterKD: kdNode) : MutableList<Point> by pointList{
    val inList: BitSet = BitSet(N)
    var distance: Double = 0.0

    /*
    var plane = kdRect(point = this[0])


    fun updatePlane(coords: Pair<Double, Double>){
        if (coords.first > plane.maxX) plane.maxX = coords.first
        else if (coords.first < plane.minX) plane.minX = coords.first

        if (coords.second > plane.maxY) plane.maxY = coords.second
        else if (coords.first < plane.minY) plane.minY = coords.second
    }
    */

    /**
     * Displays timing information and outputs visual tour representation using [StdDraw].
     */

    fun show() {
        println("Time to draw = ${getDeltaT(ttemp)}")
        println("Total distance: $distance")
        StdDraw.show()
    }

    /**
     * Draws the tour. Currently only called once, at end of tour generation. Will update for dynamic drawing later.
     */

    fun draw() {
        println("Time to make tour = ${getDeltaT(ttemp)}")

        ttemp = System.currentTimeMillis()
        t1 = ttemp - t1

        StdDraw.enableDoubleBuffering()
        StdDraw.setPenRadius(.005)
        StdDraw.setXscale(0.0, bounds[0])
        StdDraw.setYscale(0.0, bounds[1])

        for (item in this) StdDraw.point(item.coords[0], item.coords[1])

        StdDraw.setPenRadius(.002)
        val bigEdges: MutableList<Triple<Point, Point, Double>> = mutableListOf()

        var curr = this[0]
        for (j in 0 until N) {
            val next = curr.next
            val c1 = next!!.coords[0]
            val c2 = next.coords[1]
            val d1 = curr.coords[0]
            val d2 = curr.coords[1]

            StdDraw.line(c1, c2, d1, d2)
            curr = next
        }
        /*

        for (j in 0 until N - 1) {
            val c1 = this[j + 1].coords[0]
            val c2 = this[j + 1].coords[1]
            val d1 = this[j].coords[0]
            val d2 =  this[j].coords[1]

            StdDraw.line(c1, c2, d1, d2)



            if (bigEdges.size < 3) {bigEdges.add(Triple(this[j], this[j+1], dist(this[j], this[j+1]))); bigEdges.sortBy { it.third }}
            else if (dist(this[j], this[j+1]) > bigEdges.last().third) {
                bigEdges.add(Triple(this[j], this[j+1], dist(this[j], this[j+1])))
                bigEdges.removeAt(0)
                bigEdges.sortBy { it.third }
            }



        }

        if (dist(this[N-1], this[0]) > bigEdges[0].third) {
            bigEdges.add(Triple(this[N-1], this[0], dist(this[N-1], this[0])))
            bigEdges.removeAt(0)
            bigEdges.sortBy { it.third }
        }


        StdDraw.line(this[N - 1].coords[0], this[N - 1].coords[1], this[0].coords[0], this[0].coords[1])


        StdDraw.setPenRadius(.008)
        for (element in bigEdges){
            StdDraw.line(element.first.coords[0], element.first.coords[1], element.second.coords[0], element.second.coords[1])
            println("${element.first.index}, ${element.second.index}, ${element.third}")
        }
        */



    }

     fun addPoint(point: Point) {
         this.add(point)
         inList.flip(point.index)
     }

    fun addPoint(index: Int, point: Point) {
        this.add(index, point)
        inList.flip(point.index)
    }
}

