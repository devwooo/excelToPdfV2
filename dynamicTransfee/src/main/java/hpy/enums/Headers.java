package hpy.enums;

import com.itextpdf.text.Header;

import java.util.ArrayList;
import java.util.List;

public enum Headers {
    TRANSACTION_TYPE("거래종류"),
    TRANSACTION_DATETIME("거래일시"),
    AMOUNT("사용금액"),
    MERCHANT("거래처"),
    BUS_ROUTE("버스노선"),
    BOARDING_STATION("승차역"),
    ALIGHTING_STATION("하차역");

    private String name;
    Headers(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> getNames() {
        List<String> list = new ArrayList<>();
        for (Headers header : Headers.values()) {
            list.add(header.getName());
        }
        return list;
    }

    public static Headers get(String name) {
        for (Headers header : Headers.values()) {
            if (header.getName().equals(name)) {
                return header;
            }
        }
        return null;
    }
}
