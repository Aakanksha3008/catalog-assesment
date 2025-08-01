import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONObject;

public class Main {

    static class Root {
        int x;
        BigInteger y;

        Root(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("👋 Hello from Main");

        String jsonContent = new String(Files.readAllBytes(Paths.get("input.json")));
        JSONObject obj = new JSONObject(jsonContent);

        int n = obj.getJSONObject("keys").getInt("n");
        int k = obj.getJSONObject("keys").getInt("k");

        List<Root> roots = new ArrayList<>();

        for (String key : obj.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JSONObject point = obj.getJSONObject(key);
            int base = point.getInt("base");
            String value = point.getString("value");

            BigInteger y = new BigInteger(value, base);
            roots.add(new Root(x, y));
        }

        roots.sort(Comparator.comparingInt(r -> r.x));
        List<Root> selectedRoots = roots.subList(0, k);

        BigDecimal secret = lagrangeInterpolationAtZero(selectedRoots);
        System.out.println("Secret c = " + secret.toBigInteger());
    }

    private static BigDecimal lagrangeInterpolationAtZero(List<Root> roots) {
        BigDecimal result = BigDecimal.ZERO;

        for (int j = 0; j < roots.size(); j++) {
            BigDecimal numerator = BigDecimal.ONE;
            BigDecimal denominator = BigDecimal.ONE;

            int xj = roots.get(j).x;

            for (int m = 0; m < roots.size(); m++) {
                if (m == j) continue;

                int xm = roots.get(m).x;
                numerator = numerator.multiply(BigDecimal.valueOf(-xm));
                denominator = denominator.multiply(BigDecimal.valueOf(xj - xm));
            }
            
            System.out.println("✔ Computed result:");
            System.out.println("Secret c = " + secret.toBigInteger());

            BigDecimal term = new BigDecimal(roots.get(j).y).multiply(numerator).divide(denominator, 20, BigDecimal.ROUND_HALF_UP);
            result = result.add(term);
        }

        return result.setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
