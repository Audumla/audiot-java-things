package net.audumla.bean;

public class SafeParse {

    static public Double parseDouble(Object data) {
        if (data == null || "-".equals(data)) {
            return null;
        }
        if (data instanceof Double) {
            return (Double) data;
        }
        if (data instanceof Integer) {
            return ((Integer) data).doubleValue();
        }
        if (data.toString().length() == 0) {
            return null;
        }
        try {
            return Double.parseDouble(data.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    static public Integer parseInteger(Object data) {
        if (data == null || "-".equals(data)) {
            return null;
        }
        if (data instanceof Integer) {
            return (Integer) data;
        }
        if (data.toString().length() == 0) {
            return null;
        }
        try {
            return Integer.parseInt(data.toString());
        } catch (Exception ex) {
            return null;
        }
    }
}
