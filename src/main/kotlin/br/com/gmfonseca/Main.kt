package br.com.gmfonseca

/**
 * Created by Gabriel Fonseca on 28/11/2020.
 */
object Main : Simulation.SimulationListener {
    private const val MAX_CLIENT_COUNT = 50
    private var clientCount = 0

    @JvmStatic
    fun main(args: Array<String>) {
        runSimulation()
    }

    private fun runSimulation() {
        if (clientCount <= MAX_CLIENT_COUNT) {
            Simulation(clientCount++, this).runSimulation()
        }
    }

    override fun onFinish() {
        runSimulation()
    }

}