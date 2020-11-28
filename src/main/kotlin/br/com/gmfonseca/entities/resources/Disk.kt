package br.com.gmfonseca.entities.resources

import br.com.gmfonseca.util.Distribution

/**
 * Created by Gabriel Fonseca on 18/11/2020.
 */
class Disk(name: String, distribution: Distribution, listener: ResourceListener) : Resource(name, distribution, listener)