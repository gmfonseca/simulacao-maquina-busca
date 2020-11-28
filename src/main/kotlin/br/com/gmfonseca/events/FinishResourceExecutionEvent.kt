package br.com.gmfonseca.events

import br.com.gmfonseca.Simulation
import br.com.gmfonseca.entities.external.Process
import br.com.gmfonseca.entities.resources.Pool
import br.com.gmfonseca.entities.resources.Resource

/**
 * Created by Gabriel Fonseca on 25/11/2020.
 */
class FinishResourceExecutionEvent(
        pool: Pool,
        private val maxClients: Int,
        private val mappedNodes: Map<Resource, Resource.Node>
) : PairEvent<Resource, Process> {

    private val clientJoinEvent = ClientJoinEvent(pool)
    private var addedCount = 0

    override fun eventRoutine(first: Resource, second: Process): Boolean {
        val nextNode = mappedNodes[first]?.next ?: return true
        nextNode.resource.addProcess(second)

        synchronized(this) {
            return when (nextNode.resource) {
                is Pool -> {
                    second.removeClient()
                    if (addedCount < maxClients) {
                        clientJoinEvent.eventRoutine()
                        addedCount++
                        true
                    } else {
                        nextNode.resource.processesCount != Simulation.PROCESSES_COUNT
                    }
                }

                else -> {
                    true
                }
            }
        }
    }

}
