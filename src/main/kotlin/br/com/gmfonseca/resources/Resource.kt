package br.com.gmfonseca.resources

import br.com.gmfonseca.process.Process
import br.com.gmfonseca.util.RandomNode
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Created by Gabriel Fonseca on 18/11/2020.
 */
abstract class Resource(val name: String, private val serviceTimeInterval: IntRange, processes: List<Process> = emptyList()) {

    val processes = ConcurrentLinkedDeque(processes)

    var visits: Int = 0; protected set
    var demand: Int = 0; protected set
    var use: Int = 0; protected set
    var occupied: Boolean = false; protected set

    fun doWork(): Process? {
        // Do work only if there is any pending process
        return processes.poll()?.also {
            val sleepTime = Random.nextInt(serviceTimeInterval)
            val length = 3 - "$sleepTime".length

            if (length > 0) {
                print(" [${"%1$${length}s".format(' ')}$sleepTime] ")
            } else {
                print(" [$sleepTime] ")
            }

            Thread.sleep(sleepTime.toLong())
            visits++
        }
    }

    fun joinQueue(process: Process) {
        processes.add(process)
    }

    data class Node(
            val resource: Resource,
            val randomResource: RandomNode<Node>
    ) {
        val next: Node; get() = randomResource.take()
    }

}