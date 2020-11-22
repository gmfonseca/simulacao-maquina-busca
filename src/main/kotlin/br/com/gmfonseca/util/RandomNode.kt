package br.com.gmfonseca.util

import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Created by Gabriel Fonseca on 20/11/2020.
 */
class RandomNode<T> private constructor(resources: List<T>, probabilities: List<IntRange>) {

    constructor() : this(emptyList(), emptyList())

    private val _items = resources.toMutableList()
    private val resources: List<T>; get() = _items

    private val _probabilities = probabilities.toMutableList()
    private val probabilities: List<IntRange>; get() = _probabilities

    private var probabilityRange: IntRange

    init {
        probabilityRange = if (probabilities.isEmpty()) {
            0..0
        } else {
            probabilities.first().first..probabilities.last().last
        }
    }

    fun take(): T {
        val randomNumber = Random.nextInt(probabilityRange)
        val index = probabilities.indexOfFirst { randomNumber in it }

        return resources[index]
    }

    fun appendResource(resource: T, probability: Int): RandomNode<T> {
        val start = probabilityRange.last + 1

        _probabilities.add(start..(start + probability))
        _items.add(resource)

        probabilityRange = probabilities.first().first..probabilities.last().last

        return this
    }

    fun copy() = RandomNode(this.resources.toList(), this.probabilities.toList())

    class Builder<N> {
        private val resources = mutableListOf<N>()
        private val probabilities = mutableListOf<IntRange>()
        private var curIndex = 0

        fun appendResource(resource: N, probability: Int): Builder<N> {
            require(probability > 0) { "Given probability must be greater than zero" }

            val maxValue = curIndex + probability
            resources.add(resource)
            probabilities.add(curIndex..maxValue)
            curIndex = maxValue + 1

            return this
        }

        fun build(): RandomNode<N> {
            require(resources.isNotEmpty()) { "You must add at least one resource to init a RandomNode instance" }

            return RandomNode(resources, probabilities)
        }
    }

}