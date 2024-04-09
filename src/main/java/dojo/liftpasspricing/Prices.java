package dojo.liftpasspricing;

import static dojo.liftpasspricing.infrastructure.ApiRoutesKt.getPriceRoute;
import static dojo.liftpasspricing.infrastructure.ApiRoutesKt.putBasePriceRoute;
import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

public class Prices {

    public static void createApp() {

        port(4567);

        get("/prices", getPriceRoute());
        put("/prices", putBasePriceRoute());

        after((req, res) -> res.type("application/json"));
    }
}
