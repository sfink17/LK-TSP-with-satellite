/**
 * Created by Simon on 2/2/2017.
 */
import java.io.*
import java.util.*

var N: Int = 0
var bounds: Pair<Double, Double> = Pair(0.0, 0.0)
var t1 = System.currentTimeMillis()
var ttemp = t1
var pointList: List<Point> = mutableListOf()

fun getDeltaT(t: Long): Long = System.currentTimeMillis() - t

/**
 * Converts point data into complete initial list of points. Adds some memory inefficiency,
 * but keeping a fixed list of all [Point]s proves helpful in many of the heuristics.
 */
fun toPoints(instance: String, heuristic: String){
    StdDraw.clear()
    val tempList: MutableList<Point> = mutableListOf()
    N = instance.filter { it.isDigit() }.toInt()
    if (instance.contains(Regex("""\dk"""))) N *= 1000
    val reader: BufferedReader = BufferedReader(FileReader(instance))
    var lines = false
    var i = 0
    reader.forEachLine {
        if (it != "") {
            val stringPair = it.trim().replace(Regex("\\s+"), " ").split(Regex("""\s"""))
            if (lines == false) {
                bounds = Pair(stringPair[0].toDouble(), stringPair[1].toDouble())
                lines = true
            }
            else {
                tempList.add(Point(Pair(stringPair[0].toDouble(), stringPair[1].toDouble()), i))
                i++
            }
        }
    }
    println("Time to read = ${getDeltaT(ttemp)}")
    ttemp = System.currentTimeMillis()
    t1 = ttemp
    pointList = tempList.toList()


    //Initial driver for all contained heuristics. LK currently not operational (sorry).
    when (heuristic) {
        "greedy" -> {
            initSatellites()
            PrintTour(constructGreedyTour())
        }
        "nn" -> {
            val tour: Tour = Tour(mutableListOf(), kdTree(pointList))

            tour.insertNearest(pointList[0])

            tour.draw()
            tour.show()
        }

        "ni" -> {
            val tour: Tour = Tour(mutableListOf(), kdTree(pointList))
            tour.inList.flip(0, N)

            tour.insertSmallest(pointList[0])

            tour.draw()
            tour.show()
        }
        /*
        "lk" -> {
            tour = makeTree(constructGreedyTour())
            LinKernighan()
            var place = 0
            val finalTour = Array(N, {val temp = place; place = tourByIndex[place]!!.next().city; temp})
            PrintTour(finalTour)
        }
        */
    }

}