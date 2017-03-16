import java.io.File
import java.io.FileNotFoundException

/**
 * Created by Simon on 2/16/2017.
 */

/**
 * Module driver. Currently calls GUI, but this can be replaced with the commented section if input from command line is
 * desired.
 */
fun main(args: Array<String>) {
    SimpleGUI()

    /*
    val heuristics = listOf("nn", "ni", "greedy")
    try {
        File(args[0])
        if (args[1] in heuristics) toPoints(args[0], args[1])
        else println("Must enter a valid heuristic")
    }
    catch (f: FileNotFoundException) {
        println("Must enter a valid filename")
    }
    */


}