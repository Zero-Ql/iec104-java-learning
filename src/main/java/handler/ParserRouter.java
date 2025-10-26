package handler;

import enums.IEC104_TypeIdentifier;
import enums.parser.GeneralCallAckParser;
import enums.parser.MeNc1SpontParser;

import java.util.HashMap;
import java.util.Map;

public final class ParserRouter {
    private static final ParserRouter INSTANCE = new ParserRouter();
    private final Map<Integer, Parser> table = new HashMap<>();

    private ParserRouter() {
        register(IEC104_TypeIdentifier);
    }

    public static ParserRouter getInstance() {
        return INSTANCE;
    }

    private void register(byte typeIdentifier, short cause, Parser parser) {
        // 将类型标识符和传送原因组合成唯一键（通过 0xFF 和 0xFFFF 分别只取低 8 位和 低 16位）
        // 将类型标识左移 16 位同传送原因错开
        int key = (typeIdentifier & 0xFF) << 16 | (cause & 0xFFFF);
        table.put(key, parser);
    }

    public Parser lookup(byte typeIdentifier, short cause) {
        int key = (typeIdentifier & 0xFF) << 16 | (cause & 0xFFFF);
        return table.get(key);
    }
}
