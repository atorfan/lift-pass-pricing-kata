package dojo.liftpasspricing

import dojo.liftpasspricing.PricesApp.create

fun main(args: Array<String>) {
    create()

    println(
        """
                LiftPassPricing Api started on 4567,
                you can open http://localhost:4567/prices?type=night&age=23&date=2019-02-18 in a navigator
                and you'll get the price of the list pass for the day.
                """
    )
}
