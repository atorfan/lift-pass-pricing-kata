package dojo.liftpasspricing;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        Prices.createApp();

        System.out.println("""
                LiftPassPricing Api started on 4567,
                you can open http://localhost:4567/prices?type=night&age=23&date=2019-02-18 in a navigator
                and you'll get the price of the list pass for the day.""");
    }
}
