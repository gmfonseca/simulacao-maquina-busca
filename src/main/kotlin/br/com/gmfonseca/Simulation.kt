package br.com.gmfonseca

import br.com.gmfonseca.entities.external.Client
import br.com.gmfonseca.entities.external.Process
import br.com.gmfonseca.entities.resources.Cpu
import br.com.gmfonseca.entities.resources.Disk
import br.com.gmfonseca.entities.resources.Pool
import br.com.gmfonseca.entities.resources.Resource
import br.com.gmfonseca.events.FinishResourceExecutionEvent
import br.com.gmfonseca.util.CsvFile
import br.com.gmfonseca.util.Distribution
import br.com.gmfonseca.util.RandomNode
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Gabriel Fonseca on 18/11/2020.
 */
class Simulation(private val clientCount: Int, private val listener: SimulationListener) : Resource.ResourceListener {

    private val startTime: Long = System.currentTimeMillis()

    private val cpu = Cpu("Cpu#1", Distribution.Uniform(5, 25), this)
    private val disk1 = Disk("Disk#1", Distribution.Uniform(0, 30), this)
    private val disk2 = Disk("Disk#2", Distribution.Uniform(0, 30), this)
    private val disk3 = Disk("Disk#3", Distribution.Exponential(25), this)
    private val pool = Pool("Pool#1", Distribution.Exponential(500), this)
    private val nodes = mutableListOf<Resource.Node>()
    private val mappedNodes = mutableMapOf<Resource, Resource.Node>()
    private val threads = mutableListOf<Thread>(cpu, disk1, disk2, disk3, pool)
    private val csvFiles = listOf(CsvFile(cpu), CsvFile(disk1), CsvFile(disk2), CsvFile(disk3), CsvFile(pool))

    private var finishEvent = FinishResourceExecutionEvent(pool, max(clientCount - PROCESSES_COUNT, 0), mappedNodes)
    private var solvedRequests = 0

    override fun onFinish(resource: Resource, process: Process) {
        solvedRequests++

        if (!finishEvent.eventRoutine(resource, process)) {
            if (pool.processes.all { it.finished }) {
                processFinish()
            }
        }
    }

    fun runSimulation() {
        /*
        * Setup all nodes of a closed circuit, where the starter node is the POOL
        *
        *                   | ⇆ DISK 1
        *  ⇆ POOL ⇄ CPU ⇆ | ⇄ DISK 2
        *                   | ⇆ DISK 3
        */
        val cpuNode = Resource.Node(cpu, RandomNode())

        val randomNodeToCpu = RandomNode.Builder<Resource.Node>().appendResource(cpuNode, 1).build()

        val poolNode = Resource.Node(pool, randomNodeToCpu.copy())
        val diskOneNode = Resource.Node(disk1, randomNodeToCpu.copy())
        val diskTwoNode = Resource.Node(disk2, randomNodeToCpu.copy())
        val diskThreeNode = Resource.Node(disk3, randomNodeToCpu.copy())

        // Attaching to the CPU node the probabilities to navigate to other resources, since we needed to create these
        // nodes after created the CPU node itself.
        cpuNode.randomResource.appendResource(diskOneNode, 45)
                .appendResource(diskTwoNode, 15)
                .appendResource(diskThreeNode, 30)
                .appendResource(poolNode, 10)

        // Setup threads and nodes list to have a better control regarding the execution flow
        val nodesList = listOf(poolNode, cpuNode, diskOneNode, diskTwoNode, diskThreeNode)
        nodes.addAll(nodesList)
        mappedNodes.putAll(nodesList.map { it.resource to it })

        // start execution, as the setup is done
        execute()
    }

    private fun execute() {
        println("Running for $clientCount clients")

        val processes = MutableList(PROCESSES_COUNT) { Process() }
        val clientsCount = min(PROCESSES_COUNT, clientCount)
        val clients = MutableList(clientsCount) { Client() }

        clients.forEachIndexed { index, client ->
            processes[index].addClient(client)
        }

        pool.addAllProcess(processes)
        threads.forEach { it.start() }

        if (clientCount == 0) {
            processFinish()
        }
    }

    private fun processFinish() {
        println("Ending for $clientCount clients\n")
        stopAll()
        saveData()
        listener.onFinish()
    }

    private fun stopAll() {
        threads.forEach { it.interrupt() }
    }

    private fun saveData() {
        val executionTime = System.currentTimeMillis() - startTime
        csvFiles.forEach {
            it.append(clientCount, executionTime, solvedRequests)
        }
    }

    interface SimulationListener {
        fun onFinish()
    }

    companion object {
        const val PROCESSES_COUNT = 10
    }

}