import com.sun.org.apache.xpath.internal.operations.Bool
import java.util.*

/**
 * Created by Simon on 2/17/2017.
 */

/**
 * Lin Kernighan chain ejection heuristic. Based partially on leading implementation by Keld Helsgaun.
 * This initial function acts as a driver for inner functions, calling [initKOpt] until no improvements can
 * be made.
 *
 * @property dontLookBits Used to speed up tour. The driver function won't try to improve on nodes that didn't yield
 *                        improvements before.
 */

fun LinKernighan(): Double {
    var doneIfZero = N
    var t2: ChildNode?
    var t1: ChildNode = tourByIndex[0]!!
    var place = 0
    var bestGain = 0.0
    var tourGain = 0.0

    val currentEdges: Array<Pair<Int, Int>> = Array(N, { Pair(0, 0) })
    for (item in tourByIndex) currentEdges[item!!.index] = Pair(item.next!!.index, item.prev!!.index)
    while(doneIfZero > 0) {
        if (dontLookBits[t1.city.index]){
            N--
            if (t1.index == N-1) {place = 0; t1 = tourByIndex[0]!!} else t1 = tourByIndex[++place]!!
            continue
        }

        //Calls initKOpt for each edge connecting current node. Will try K-opt moves up to K = 28.
        for (j in 0..1){
            if (j == 0) t2 = t1.next else t2 = t1.prev
            var tries = 0
            do {
                runningGain = dist(t1.city, t2!!.city)
                t2 = initKOpt(t1, t2)
                tries++
            }
                while (runningGain <= 0 && t2 != null && tries < 6)
        }
        //Setter for dontLookBit. If no improvement, sets true.
        tourGain += runningGain
        if (bestGain < tourGain) bestGain = tourGain
        if (runningGain <= 0) {dontLookBits[place] = true; doneIfZero--} else doneIfZero = N
        runningGain = 0.0
        t1 = tourByIndex[++place]!!
    }
    return bestGain
}

var bestGain: Double = 0.0
var runningGain: Double = 0.0
var nodeList: Array<ChildNode?> = arrayOf()
var bestNodeList: Array<ChildNode?> = arrayOf()
var addedInChain: MutableList<ChildNode?> = mutableListOf()

/*
These properties are used to find valid K opt moves and to make them.
p is a list of used nodes in order of their encounter "next"-wise about the tour.
incl returns the adjacent node on the in-edge in the current move.
q is the inverse of p. q[p[i]] == i.
 */
var p: Array<Int> = arrayOf()
var incl: Array<Int> = arrayOf()
var q: Array<Int> = arrayOf()
var seq: Array<Int> = arrayOf()

/**
 * Checks if current K opt move is valid.
 *
 * @property p A list of used nodes sorted in order of their encounter "next"-wise about the tour.
 * @property incl Returns the adjacent node on the in-edge in the current move.
 * @property q The inverse of p. q\[p\[i\]\] == i.
 */

fun isValidMove(k: Int) : Boolean {
    sortNodes(k)
    //Traversal operation.
    var place = k*2; var count = 0
    seq = Array(k*2, {0})
    while (place != 0) {
        seq[k*2 - 1 - count*2] = place
        seq[k*2 - 2 - count*2] = q[incl[p[place - 1]]]
        place = (q[incl[p[place - 1]]] + 1) xor 1
        count++
    }
    return (count == k)
}

/**
 * Sorts nodes in order of their encounter "next"-wise about the tour.
 */

fun sortNodes(k: Int){
    p = Array(k*2, {
        if (it % 2 == 0) {
            if (nodeList[it*2]!!.next == nodeList[it*2 + 1]) it*2 else it*2 + 1
        }
        else p[it-1] xor 1
    })
    p.sortWith(Comparator { t1, t2 ->  between(0, t1, t2)})
    incl = Array(k*2, { if (it % 2 != 0 && it != k*2-1) it + 1 else it - 1 })
    incl[0] = k*2-1; incl[k*2-1] = 0
    q = Array(k*2, { p.indexOf(it) })
}

fun Array<Int>.after(a: Int, b: Int): Boolean {
    for (i in a..this.lastIndex) if (this[i] == b) return true
    return false
}

fun between(a: Int, b: Int, c: Int): Int {
    val t1 = parent(nodeList[a]!!).index; val i1 = parent(nodeList[b]!!).index; val i2 = parent(nodeList[c]!!).index
    var t2: Int; var t3: Int
    if (i1 >= t1) t2 = i1 - t1 else t2 = i1 + (tour.size - t1)
    if (i2 >= t1) t3 = i2 - t1 else t3 = i2 + (tour.size - t1)
    if (t2 == t3) {t2 = nodeList[a]!!.index; t3 = nodeList[b]!!.index}
    if (t3 > t2) return -1 else return 1
}

fun flip(a: ChildNode, b: ChildNode){
    var aSameParent: Boolean = false; var bSameParent: Boolean = false
    if (a.prev().parent == a.parent) aSameParent = true
    if (b.prev().parent == b.parent) bSameParent = true

    when {
        aSameParent && bSameParent && a.parent != b.parent -> { splitAndMerge(a.prev(), a); splitAndMerge(b, b.next()) }
        !aSameParent && bSameParent -> splitAndMerge(b, b.next())
        !bSameParent && aSameParent -> splitAndMerge(a.prev(), a)
    }
    if (parent(a) == parent(b) && parent(a.prev()) == parent(b.next())) {
        if (Math.abs(b.index - a.index) < 3 * parent(a).size / 4) {
            flipChildren(a, b)
        }
        else {
            splitAndMerge(a.prev(), a)
            splitAndMerge(b, b.next())
            flipParents(parent(a), parent(b))
        }
    } else flipParents(parent(a), parent(b))
}

fun connect(a: ChildNode, b: ChildNode, nextwise: Boolean) {
    if (nextwise){
        setNext(a, b)
        setPrev(b, a)
    }
    else {
        setPrev(a, b)
        setNext(b, a)
    }
}

/**
 * Makes K opt move.
 */

fun makeKOptMove(k: Int){
    val subpathPolarity: MutableList<Int> = mutableListOf()
    for (i in 1 until seq.lastIndex step 2){
        val polarity: Int
        if (p.after(seq[i], seq[i+1])) polarity = 1
        else polarity = -1
        subpathPolarity.add(polarity)
    }

    for (i in 0..subpathPolarity.size){
        if (subpathPolarity[i] == -1){
            flip(nodeList[p[i*2 + 1]]!!, nodeList[p[i*2 + 2]]!!)
        }
    }
    val nextwise: Boolean
    if (nodeList[1] == nodeList[0]!!.next()) nextwise = true else nextwise = false
    for (i in 0 until k) connect(nodeList[seq[i*2]]!!, nodeList[seq[i*2 + 1]]!!, nextwise)
}

/**
 * Function that initiates K opt and sets up for further k opt moves.
 *
 * @return Returns the last node of the populated k opt move. [LinKernighan] uses this
 *         to check whether a move has been made.
 */
fun initKOpt(s1: ChildNode, s2: ChildNode): ChildNode? {
    nodeList = Array(8, {null})
    nodeList[0] = s1; nodeList[1] = s2
    bestGain = Double.NEGATIVE_INFINITY
    bestNodeList =  Array(8, {null})
    runningGain = kOptRec(runningGain, 2)
    addedInChain.addAll(bestNodeList)

    /*
    This is only called if no improving moves have been found. Performs best 4-opt move found and
    subsequently recalls function, deleting the most recent edge and continuing the chain of moves.
     */
    if (runningGain <= 0 && bestNodeList[7] != null) {
        nodeList = bestNodeList
        makeKOptMove(4)
    }
    return bestNodeList[7]
}

/**
 * K opt move generator. Calls until a valid move has been found that results in
 * positive gain, or until all potential moves are exhausted. This version of
 * Lin Kernighan uses a 4-opt as its basic move.
 *
 * @property g0 Running gain within the current move.
 */

tailrec fun kOptRec(g0: Double, k: Int): Double {
    val t1 = nodeList[2*k - 4]!!; val t2 = nodeList[2*k - 3]!!
    var t3: ChildNode; var t4: ChildNode
    var g1: Double; var g2: Double; var g3: Double
    for (item in t2.city.nearestK.filter { it.first != t1.city.index }){
        t3 = tourByIndex[item.first]!!
        g1 = g0 - item.second
        if (g1 > 0 && !Added(t2, t3, k)) {
            nodeList[2*k - 2] = t3
            for (i in 0..1){
                if (i == 0) t4 = t3.next!! else t4 = t3.prev!!
                if (!Deleted(t3, t4, k)) {
                    nodeList[2*k - 1] = t4
                    g2 = g1 + dist(t3.city, t4.city)
                    g3 = g2 - dist(t4.city, t1.city)
                    if (isValidMove(k)) {
                        if (g3 > 0) {
                            makeKOptMove(k)
                            return g3
                        }
                        if (k == 4 && bestGain < g3 && Excludable(t3, t4)) {
                            bestGain = g3
                            bestNodeList = nodeList
                        }
                    }
                    else if (k < 4) return kOptRec(g2, k + 1)
                }
            }
        }
    }
    return bestGain
}

/**
 * Function to check if edge to be added has been added previously in current move.
 * Called during [kOptRec] to disqualify edges.
 */

fun Added(t1: ChildNode, t2: ChildNode, k: Int): Boolean {
    var i = 2 * k - 5
    while (i > 0) {
        if (t1 == nodeList[i] && t2 == nodeList[i + 1] || t1 == nodeList[i + 1] && t2 == nodeList[i])
            return true
        i -= 2
    }
    return false
}

/**
 * Function to check if edge to be removed has been removed previously in current move.
 * Called during [kOptRec] to disqualify edges.
 */

fun Deleted(t1: ChildNode, t2: ChildNode, k: Int): Boolean {
    var i = 2 * k - 3
    while (i > 0) {
        if (t1 == nodeList[i] && t2 == nodeList[i - 1] || t1 == nodeList[i - 1] && t2 == nodeList[i])
            return true
        i -= 2
    }
    return false
}

/**
 * Function to check if last edge to be removed has been added in the current sequence of moves.
 * Called during [kOptRec] to prevent repeating moves.
 */

fun Excludable(t1: ChildNode, t2: ChildNode): Boolean {
    for (i in 1..addedInChain.lastIndex step 2)
        if ((t1 == addedInChain[i] && t2 == addedInChain[(i + 1) % addedInChain.size]) ||
                t2 == addedInChain[i] && t1 == addedInChain[(i + 1) % addedInChain.size])
            return false
    return true
}