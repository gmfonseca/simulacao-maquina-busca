package br.com.gmfonseca.entities.resources

import br.com.gmfonseca.entities.external.Client
import br.com.gmfonseca.entities.external.Process
import br.com.gmfonseca.util.Distribution

/**
 * Created by Gabriel Fonseca on 20/11/2020.
 */
class Pool(name: String, distribution: Distribution, listener: ResourceListener, processes: List<Process> = emptyList()) :
        Resource(name, distribution, listener, processes) {

    fun addClient(client: Client) {
        synchronized(this) {
            mProcesses.first.addClient(client)
        }
    }

}