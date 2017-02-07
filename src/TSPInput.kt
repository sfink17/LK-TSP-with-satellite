/**
 * Created by Simon on 2/2/2017.
 */
import java.io.*

class Point(val coords: Pair<Double, Double>) {
    var edges: MutableMap<Int, Float>? = null
}

var N: Int = 0

fun main(args: Array<String>){
    val pointList: MutableList<Point> = mutableListOf()
    val instance = args[0]
    N = instance.filter { it.equals("\\d") }.toInt()
    if (instance[instance.lastIndexOf("\\d")+1].equals("k")) N *= 1000
    val reader: BufferedReader = BufferedReader(FileReader(args[0]))
    var lines = false
    reader.forEachLine {
        if (it != "") {
            val stringPair = it.split(" ")
            if (lines == false) lines = true
            else {
                pointList.add(Point(Pair(stringPair[0].toDouble(), stringPair[1].toDouble())))
            }
        }
    }
    val pointArray: Array<Point> = pointList.toTypedArray()
    val graph = getSortedConnectedGraph(pointArray)
    if (File("graph.txt").exists()) File("graph.txt").delete()
    else File("graph.txt").createNewFile()
    val writer = FileWriter("graph.txt")
    for ((index, edgelist) in graph){
        for (item in edgelist){
            writer.appendln(item.value.toString())
        }
    }
}