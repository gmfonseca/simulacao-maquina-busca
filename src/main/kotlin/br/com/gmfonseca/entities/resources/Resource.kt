package br.com.gmfonseca.entities.resources

import br.com.gmfonseca.entities.external.Process
import br.com.gmfonseca.util.Distribution
import br.com.gmfonseca.util.RandomNode
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * Created by Gabriel Fonseca on 18/11/2020.
 */
abstract class Resource(
        val resourceName: String,
        private val distribution: Distribution,
        private val listener: ResourceListener,
        processes: List<Process> = emptyList()
) : Thread() {

    protected val mProcesses = ConcurrentLinkedDeque(processes)
    val processes: List<Process>; get() = mProcesses.toList()
    val processesCount: Int; get() = mProcesses.size

    private var visits: Int = 0
    private var demand: Long = 0
    private var serviceTime: Long = 0

    override fun run() {
        while (!isInterrupted) {
            doWork()
        }
    }

    protected open fun doWork() {
        mProcesses.peek()?.also {
            if (!it.finished) {
                val sleepTime = distribution.generate()

                mProcesses.poll()
                serviceTime += sleepTime
                visits++

                sleep(sleepTime)

                listener.onFinish(this, it)
            }
        }
    }

    fun addProcess(process: Process) {
        mProcesses.add(process)
    }

    fun addAllProcess(processes: List<Process>) {
        mProcesses.addAll(processes)
    }

    fun csvHeader(): String {
        return "demand\tserviceTime\tvisits"
    }

    override fun toString(): String {
        return "$demand\t$serviceTime\t$visits"
    }

    data class Node(val resource: Resource, val randomResource: RandomNode<Node>) {
        val next: Node; get() = randomResource.take()
    }

    interface ResourceListener {
        fun onFinish(resource: Resource, process: Process)
    }
}