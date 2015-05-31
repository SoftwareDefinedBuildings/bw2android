package edu.berkeley.cs.sdb.bosswave;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

enum Command {
    PUBLISH         ("publ"),
    PERSIST         ("pers"),
    SUBSCRIBE       ("subs"),
    LIST            ("list"),
    QUERY           ("quer"),
    TAP_SUBSCRIBE   ("tsub"),
    TAP_QUERY       ("tque"),
    PUT_DOT         ("putd"),
    PUT_ENTITY      ("pute"),
    PUT_CHAIN       ("putc"),
    MAKE_DOT        ("makd"),
    MAKE_ENTITY     ("make"),
    MAKE_CHAIN      ("makc"),
    BUILD_CHAIN     ("bldc"),
    ADD_PREF_DOT    ("adpd"),
    ADD_PREF_CHAIN  ("adpc"),
    DEL_PREF_CHAIN  ("dlpc"),
    SET_ENTITY      ("sete"),

    HELLO           ("helo"),
    RESPONSE        ("resp"),
    RESULT          ("rslt");

    private final String code;

    Command(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    private static final Map<String, Command> COMMANDS_BY_CODE;
    static {
        HashMap<String, Command> codeMap = new HashMap<>();
        for (Command bwc : Command.values()) {
            codeMap.put(bwc.getCode(), bwc);
        }
        COMMANDS_BY_CODE = Collections.unmodifiableMap(codeMap);
    }

    public static Command fromCode(String code) {
        return COMMANDS_BY_CODE.get(code);
    }
}
