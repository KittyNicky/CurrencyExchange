package app.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
public class Currency {
    public static final int NAME_LENGTH = 100;
    public static final int CODE_LENGTH = 3;
    public static final int SIGN_LENGTH = 2;
    private int id;

    private String name;

    private String code;

    private String sign;

    public Currency(String name, String code, String sign) {
        this.name = name;
        this.code = code;
        this.sign = sign;
    }
}
