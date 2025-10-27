package master.handler.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ParserMeta注解接口，用于定义解析器的元数据信息
 * 该注解可用于标识解析器类型及其相关的事务处理信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ParserMeta {
    /**
     * 获取解析器类型标识符
     * @return 解析器类型的字节标识符
     */
    byte typeIdentifier();

    /**
     * 获取引起事务传输的原因代码
     * @return 事务传输原因的短整型代码
     */
    short causeTx();
}

