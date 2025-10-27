package master.handler.parser;

import java.util.Map;

/**
 * ParserRouter类用于根据类型标识符和原因码查找对应的解析器
 * 这是一个单例模式的实现，提供全局唯一的解析器路由功能
 */
public final class ParserRouter {
    private static final ParserRouter INSTANCE = new ParserRouter();
    private static final Map<Integer, Parser> table = ParserBootstrap.CACHED;

    /**
     * 获取ParserRouter的单例实例
     * @return 返回ParserRouter的唯一实例
     */
    public static ParserRouter getInstance() {
        return INSTANCE;
    }

    /**
     * 根据类型标识符和原因码查找对应的解析器
     * @param typeIdentifier 类型标识符，用于区分不同的数据类型
     * @param causeTx 原因码，用于进一步细化解析器的选择
     * @return 返回对应的解析器实例，如果未找到则返回null
     */
    public Parser lookup(byte typeIdentifier, short causeTx) {
        // 使用ParserBootstrap生成的键值从缓存表中查找解析器
        return table.get(ParserBootstrap.key(typeIdentifier, causeTx));
    }
}

