package br.com.gmfonseca.util

import br.com.gmfonseca.entities.resources.Resource
import java.io.File

/**
 * Created by Gabriel Fonseca on 28/11/2020.
 */
class CsvFile(private val resource: Resource) {

    private val file: File

    init {
        val dir = File(FILES_PATH, "third")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        file = File(dir, "${resource.resourceName}.csv")

        if (!file.exists()) {
            file.createNewFile()
            file.writeText("N\texecutionTime\tsolvedRequests\tthroughput\t${resource.csvHeader()}\tusage\n")
        }
    }

    fun append(n: Int, executionTime: Long, solvedRequests: Int) {
        file.appendText("$n	$executionTime\t$solvedRequests\t0\t$resource\t0\n".replace(".", ","))
    }

    companion object {
        private const val FILES_PATH = "simulation-results"
    }

}