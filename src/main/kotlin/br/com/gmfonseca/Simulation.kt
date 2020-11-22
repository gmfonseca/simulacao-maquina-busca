package br.com.gmfonseca

import br.com.gmfonseca.process.Process
import br.com.gmfonseca.resources.Cpu
import br.com.gmfonseca.resources.Disk
import br.com.gmfonseca.resources.Pool
import br.com.gmfonseca.resources.Resource
import br.com.gmfonseca.util.RandomNode

/**
 * Created by Gabriel Fonseca on 18/11/2020.
 */
object Simulation {

    @JvmStatic
    fun main(args: Array<String>) {

        val cpu = Cpu("Cpu#1", 5..25)
        val disk1 = Disk("Disk#1", 0..30)
        val disk2 = Disk("Disk#2", 0..30)
        val disk3 = Disk("Disk#3", 0..30)
        val pool = Pool("Awaiting", 500..500, MutableList(5) { Process("Process#${it + 1}") })

        val cpuNode = Resource.Node(cpu, RandomNode())

        val randomNodeToCpu = RandomNode.Builder<Resource.Node>().appendResource(cpuNode, 1).build()

        val poolNode = Resource.Node(pool, randomNodeToCpu.copy())
        val diskOneNode = Resource.Node(disk1, randomNodeToCpu.copy())
        val diskTwoNode = Resource.Node(disk2, randomNodeToCpu.copy())
        val diskThreeNode = Resource.Node(disk3, randomNodeToCpu.copy())

        cpuNode.randomResource.appendResource(diskOneNode, 45)
                .appendResource(diskTwoNode, 15)
                .appendResource(diskThreeNode, 30)
                .appendResource(poolNode, 10)

        execute(poolNode, cpuNode, diskOneNode, diskTwoNode, diskThreeNode)
    }

    private fun execute(vararg nodes: Resource.Node) {
        repeat(5) {
            println("\nExecução ${it + 1}")

            nodes.forEach { node ->
                node.resource.doWork()?.run {
                    joinResourceQueue(node.next.resource)
                    println("$name -> ${node.resource.name}")
                }
            }
        }
    }

}