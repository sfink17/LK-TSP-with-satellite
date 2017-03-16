/**
 * Created by Simon on 3/6/2017.
 */
/**
 * Child node in two level tree.
 * @property index Relative index within segment.
 * @property next Directional "next" pointer.
 * @property prev Directional "previous" pointer.
 * @property parent Parent pointer.
 * @property city Encapsulated city.
 */
class ChildNode(var index: Int, var city: Point,
                var next: ChildNode? = null, var prev: ChildNode? = null, var parent: Int) {
    fun next() = if (parents[parent].reverse) prev!! else next!!
    fun prev() = if (parents[parent].reverse) next!! else prev!!
}

/**
 * Parent node in two level tree.
 * @property index Relative index within tour.
 * @property next Directional "next" pointer.
 * @property prev Directional "previous" pointer.
 * @property reverse The point of the whole thing. Flipping this reverses the child segment with constant complexity.
 */

class ParentNode(var index: Int, var next: ParentNode? = null, var prev: ParentNode? = null, var reverse: Boolean = false,
                 var size: Int, var minIndex: ChildNode? = null, var maxIndex: ChildNode? = null){
    fun minIndex(): ChildNode = if (reverse) maxIndex!! else minIndex!!
    fun maxIndex(): ChildNode = if (reverse) minIndex!! else maxIndex!!
}

var tourByIndex: Array<ChildNode?> = arrayOf()
var parents: Array<ParentNode> = arrayOf()

/**
 * I need to trim/rethink this. Messily initiates the tree and fills an array to link tree
 * with point data.
 */
fun makeTree(firstTour: Array<Int>): MutableList<ChildNode>{
    val tree: MutableList<ChildNode> = mutableListOf()
    val segmentSize = Math.floor(Math.sqrt(N.toDouble())).toInt()
    tourByIndex = Array(N, {null})

    parents = Array(N / segmentSize, {ParentNode(it, size = segmentSize)})
    for (i in 1 until parents.lastIndex){
        parents[i].next = parents[i+1]
        parents[i].prev = parents[i-1]
    }
    parents[0].prev = parents.last(); parents[0].next = parents[1]
    parents.last().prev = parents[parents.lastIndex - 1]; parents.last().next = parents[0]
    parents.last().size = N - (segmentSize * (parents.size - 1))

    var parentNum = 0
    tree.add(ChildNode(0, pointList[firstTour[0]], parent = 0))
    tourByIndex[firstTour[0]] = tree[0]
    for (i in 1 until N-1){
        if (i % segmentSize == 0) parentNum++
        tree.add(ChildNode(i, pointList[firstTour[i]], prev = tree[i-1], parent = parentNum))
        tourByIndex[firstTour[i]] = tree[i]
    }
    tree.add(ChildNode(N-1, pointList[firstTour[N-1]], tree[0], tree[N-2], parentNum))
    tourByIndex[firstTour[N-1]] = tree[N-1]
    tree[0].prev = tree.last(); tree[0].next = tree[1]
    for (i in 1 until N-1) tree[i].next = tree[i+1]
    return tree
}

fun setNext(city: ChildNode, node: ChildNode) = if (parent(city).reverse) city.prev = node else city.next = node
fun setPrev(city: ChildNode, node: ChildNode) = if (parent(city).reverse) city.next = node else city.prev = node
fun next(city: Int) = tour[city].next()
fun prev(city: Int) = tour[city].next()
fun parent(city: ChildNode) = parents[city.parent]
var tour: MutableList<ChildNode> = mutableListOf()

fun reverse(city: ChildNode){
    val temp = city.next
    city.next = city.prev
    city.prev = temp
}

fun reverseParent(city: ParentNode){
    val temp = city.next
    city.next = city.prev!!
    city.prev = temp
}

fun flipChildren(a: ChildNode, b: ChildNode){
    val t1: ChildNode; val t2: ChildNode
    if (a.index > b.index) {t2 = a; t1 = b} else {t1 = a; t2 = b}
    var i = a
    val range = t2.index - t1.index - 1
    for (j in -range..range step 2){
        reverse(i)
        i.index -= j
        i = i.prev!!
    }
}

fun flipParents(a: ParentNode, b: ParentNode){
    if (a == b) {reverseParent(a); return}
    val t1: Int; val t2: Int
    if (a.index > b.index) {t2 = a.index; t1 = b.index} else {t1 = a.index; t2 = b.index}
    var i = a
    val range = t2 - t1
    for (j in -range..range step 2){
        reverseParent(i)
        i.index -= j
        i = i.prev!!
    }
}

fun splitAndMerge(c1: ChildNode, c2: ChildNode) {
    val t1: ChildNode; val t2: ChildNode
    val p = parent(c1)
    if (c1.next() == c2) {t1 = c1; t2 = c2} else {t2 = c1; t1 = c2}
    val subsize = Math.abs(p.maxIndex().index - t2.index)
    if (subsize >= p.size / 2) {
        var i = t1
        if (p.prev!!.reverse != p.reverse){
            while (i.index != p.minIndex().index){
                reverse(i)
                i = i.next()
            }
        }
        merge(-1, t1, p.prev!!)
        p.prev!!.size += p.size - subsize
        p.size -= p.size - subsize
    }
    else {
        var i = t2
        if (p.next!!.reverse != p.reverse){
            while (i.index != p.maxIndex().index){
                reverse(i)
                i = i.prev()
            }
        }
        merge(1, t2, p.next!!)
        p.next!!.size += subsize
        p.size -= subsize
    }

}

fun merge(dir: Int, city: ChildNode, parent: ParentNode){
    val start: ChildNode
    if (dir > 0) start = parent.minIndex().prev() else start = parent.maxIndex().next()
    var curr = start
    if (start == parent.minIndex!!.prev) {
        var i = parent.minIndex!!.index - 1
        while (curr != city){
            curr.parent = parent.index
            curr.index = i--
            curr = curr.prev!!
        }
    }
    else {
        var i = parent.maxIndex!!.index + 1
        while (curr != city){
            curr.parent = parent.index
            curr.index = i++
            curr = curr.next!!
        }
    }
}