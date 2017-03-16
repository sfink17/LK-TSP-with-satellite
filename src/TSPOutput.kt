import java.io.FileWriter

/**
 * Created by Simon on 2/16/2017.
 */

/**
 * Used for Greedy Tour output. This could easily be consolidated with
 * [Tour.draw], but there are some slight differences in the data structures,
 * and it is very low on my priority list.
 */
fun PrintTour(tour: Array<Int>) {


    StdDraw.enableDoubleBuffering()
    StdDraw.setPenRadius(.005)
    StdDraw.setXscale(0.0, bounds[0])
    StdDraw.setYscale(0.0, bounds[1])
    var totalDist: Double = 0.0

    for (item in pointList) {
        StdDraw.point(item.coords[0], item.coords[1])
    }

    StdDraw.setPenRadius(.002)

    for (j in 0 until N - 1) {
        val c1 = pointList[tour[j + 1]].coords[0]
        val c2 = pointList[tour[j + 1]].coords[1]
        val d1 = pointList[tour[j]].coords[0]
        val d2 = pointList[tour[j]].coords[1]
        totalDist += dist(tour[j], tour[j+1])



        StdDraw.line(c1, c2, d1, d2)
    }
    totalDist += dist(tour[N - 1], tour[0])
    StdDraw.line(pointList[tour[N-1]].coords[0], pointList[tour[N-1]].coords[1], pointList[tour[0]].coords[0], pointList[tour[0]].coords[1])


    StdDraw.show()

    println("Time to draw = ${getDeltaT(ttemp)}")
    println("Construction time: $t1 ms, total distance, $totalDist")
}