package br.com.gmfonseca.entities.external

/**
 * Created by Gabriel Fonseca on 20/11/2020.
 */
class Process {

    var finished = true; private set
    var clients = 0
    var client: Client? = null; private set

    fun addClient(client: Client): Boolean {
        return synchronized(this) {
            if (finished) {
                clients++
                finished = false
                this.client = client
                true
            } else {
                false
            }
        }
    }

    fun removeClient(): Boolean {
        return synchronized(this) {
            if (finished) {
                false
            } else {
                client = null
                finished = true
                true
            }
        }
    }
}