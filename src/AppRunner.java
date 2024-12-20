import enums.ActionLetter;
import model.*;
import util.UniversalArray;
import util.UniversalArrayImpl;

import java.util.Scanner;

public class AppRunner {

    private final UniversalArray<Product> products = new UniversalArrayImpl<>();

    private static CoinAcceptor coinAcceptor;

    private PaymentMethod paymentMethod;

    private static String chosePayMethod;

    private static int minValue;

    private static boolean isExit = false;

    private AppRunner() {
        products.addAll(new Product[]{
                new Water(ActionLetter.B, 20),
                new CocaCola(ActionLetter.C, 50),
                new Soda(ActionLetter.D, 30),
                new Snickers(ActionLetter.E, 80),
                new Mars(ActionLetter.F, 80),
                new Pistachios(ActionLetter.G, 130)
        });
    }

    private static void getPaymentMethod() {
        if (chosePayMethod.equalsIgnoreCase("n")) {
            coinAcceptor = new CashAcceptor(200);
            minValue = 20;
        } else if (chosePayMethod.equalsIgnoreCase("m")) {
            coinAcceptor = new CoinAcceptor(100);
            minValue = 10;
        }
    }

    private static void choosePayMethod() {
        print("Выберите способ оплаты");
        String action;
        while (true) {
            print(" n - оплата купюрами");
            print(" m - оплата монетами");
            print(" h - выйти");
            action = fromConsole().substring(0, 1).toLowerCase();
            if (action.equalsIgnoreCase("n") || action.equalsIgnoreCase("m") || action.equalsIgnoreCase("h")) {
                break;
            }
        }
        switch (action) {
            case "n":
                chosePayMethod = "n";
                break;
            case "m":
                chosePayMethod = "m";
                break;
        }
    }


    public static void run() {
        AppRunner app = new AppRunner();
        choosePayMethod();
        try {
            getPaymentMethod();
        } catch (NullPointerException npe) {
            return;
        }
        while (!isExit) {
            app.startSimulation();
        }
    }

    private void startSimulation() {
        print("В автомате доступны:");
        showProducts(products);

        switch (chosePayMethod) {
            case "n":
                print("Денег на сумму: " + coinAcceptor.getAmount());
                break;
            case "m":
                print("Монет на сумму: " + coinAcceptor.getAmount());
                break;
            case "h":
                return;
        }

        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        allowProducts.addAll(getAllowedProducts().toArray());
        chooseAction(allowProducts);

    }

    private UniversalArray<Product> getAllowedProducts() {
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        for (int i = 0; i < products.size(); i++) {
            if (coinAcceptor.getAmount() >= products.get(i).getPrice()) {
                allowProducts.add(products.get(i));
            }
        }
        return allowProducts;
    }

    private void chooseAction(UniversalArray<Product> products) {
        print(" a - Пополнить баланс");
        showActions(products);
        print(" h - Выйти");
        String action = fromConsole().substring(0, 1);
        if ("a".equalsIgnoreCase(action)) {
            coinAcceptor.setAmount(coinAcceptor.getAmount() + minValue);
            print("Вы пополнили баланс на " + minValue);
            return;
        }
        try {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getActionLetter().equals(ActionLetter.valueOf(action.toUpperCase()))) {
                    coinAcceptor.setAmount(coinAcceptor.getAmount() - products.get(i).getPrice());
                    print("Вы купили " + products.get(i).getName());
                    break;
                } else if ("h".equalsIgnoreCase(action)) {
                    isExit = true;
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            print("Недопустимая буква. Попробуйте еще раз.");
            chooseAction(products);
        }


    }

    private void showActions(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(String.format(" %s - %s", products.get(i).getActionLetter().getValue(), products.get(i).getName()));
        }
    }

    private static String fromConsole() {
        return new Scanner(System.in).nextLine();
    }

    private void showProducts(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(products.get(i).toString());
        }
    }

    private static void print(String msg) {
        System.out.println(msg);
    }
}
