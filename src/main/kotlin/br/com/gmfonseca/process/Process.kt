package br.com.gmfonseca.process

import br.com.gmfonseca.resources.Resource

/**
 * Created by Gabriel Fonseca on 20/11/2020.
 */
class Process(val name: String) {
    fun joinResourceQueue(resource: Resource) {
        resource.joinQueue(this)
    }
}