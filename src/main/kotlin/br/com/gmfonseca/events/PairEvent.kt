package br.com.gmfonseca.events

/**
 * Created by Gabriel Fonseca on 25/11/2020.
 */
interface PairEvent<T, U> {

    fun eventRoutine(first: T, second: U): Boolean

}