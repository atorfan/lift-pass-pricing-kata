package dojo.liftpasspricing.domain

interface BasePriceRepository {

    fun retrieveFor(priceType: String): Int
}
