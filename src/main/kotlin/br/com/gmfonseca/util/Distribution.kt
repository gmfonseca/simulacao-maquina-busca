package br.com.gmfonseca.util

import kotlin.math.abs
import kotlin.math.log10
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Created by Gabriel Fonseca on 25/11/2020.
 */
interface Distribution {

    fun generate(): Long

    class Exponential(private val mean: Int) : Distribution {

        override fun generate(): Long {
            val probability = Random.nextInt(0..100) / 100.0

            return if (probability == 0.0) {
                0L
            } else {
                -(log10(probability) * mean).toLong()
            }
        }

    }

    class Uniform(private val minTime: Int, private val maxTime: Int) : Distribution {
        init {
            require(minTime < maxTime) { "The given maxTime should be greater that minTime." }
        }

        override fun generate(): Long {
            val probability = Random.nextInt(0..100) / 100.0

            return abs(minTime + ((maxTime - minTime) * probability)).toLong()
        }
    }

}