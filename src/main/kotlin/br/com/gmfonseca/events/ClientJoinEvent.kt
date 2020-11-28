package br.com.gmfonseca.events

import br.com.gmfonseca.entities.external.Client
import br.com.gmfonseca.entities.resources.Pool

/**
 * Created by Gabriel Fonseca on 25/11/2020.
 */
class ClientJoinEvent(private val pool: Pool) : ExternalEvent {

    override fun eventRoutine() {
        pool.addClient(Client())
    }

}