package dojo.liftpasspricing

fun main(args: Array<String>) {
    PricesApp.start()

    println(
        """
                LiftPassPricing Api started on 4567,
                you can open http://localhost:4567/prices?type=night&age=23&date=2019-02-18 in a navigator
                and you'll get the price of the list pass for the day.
                """
    )
}
