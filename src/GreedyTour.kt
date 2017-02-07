/**
 * Created by Simon on 2/2/2017.
 */

fun getSortedConnectedGraph(nodes: Array<Point>): MutableMap<Int, MutableMap<Int, Float>>{
    val graph: MutableMap<Int, MutableMap<Int, Float>> = mutableMapOf()
    val last: Int = nodes.lastIndex
    var max: Int = 0
    var locMax: Float = 0F

    for (node in nodes) {
        val i = nodes.indexOf(node)
        var size = 0
        val locGraph: MutableMap<Int, Float> = mutableMapOf()
        for (j in i+1..last) {
            val d = Pythagoras(nodes[j].coords.second, node.coords.second,
                    nodes[j].coords.first, node.coords.first)
            size++
            when {
                size <= 20 ->  {
                    locGraph.put(j, d)
                }
                else -> {
                    if (size == 21) {
                        max = locGraph.maxBy { it.value }!!.key
                        locMax = locGraph[max]!!
                    }
                    if (d < locMax) {
                        locGraph.put(j, d)
                        locGraph.remove(max)
                        max = locGraph.maxBy { it.value }!!.key
                        locMax = locGraph[max]!!
                    }
                }
            }
        }
        node.edges = locGraph
        graph.put(i, locGraph)
    }
    return graph
}

fun getMin(edges: MutableMap<Int, MutableMap<Int, Float>>): Pair<Int, Int>{
    var min: Pair<Int, Int> = Pair(0, 0)
    val runningMin: Float = 0F
    for (i in edges){
        val temp = i.value.minBy { it.value }
        if (runningMin == 0F || temp!!.value < runningMin) {
            min = Pair(i.key, temp!!.key)
        }
    }
    return min
}

fun GreedyTour(edges: MutableMap<Int, MutableMap<Int, Float>>) {
    val firstTour: Array<Int> = Array(N, {0})
    var tourComplete: Boolean = false
    val 
    firstTour[0] = getMin(edges).first
    firstTour[1] = getMin(edges).second
    while(!tourComplete){

    }
}

fun Pythagoras(x2: Double, x1: Double, y2: Double, y1: Double): Float {
    return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)).toFloat()
}