module model {
    requires ini4j;
    requires static lombok;
    requires com.google.auto.service;
    requires io.netty.handler;
    requires io.netty.codec;
    requires io.netty.transport;
    requires io.netty.buffer;
    requires io.netty.common;
    requires org.apache.logging.log4j;

    uses master.handler.parser.Parser;
    provides master.handler.parser.Parser
            with master.handler.parser.impl.controlParser.cIcNa1.IcNa1AckConParser,
                    master.handler.parser.impl.controlParser.cIcNa1.IcNa1ActTermParser,
                    master.handler.parser.impl.controlParser.cIcNa1.MeNc1IntrogenParser,
                    master.handler.parser.impl.controlParser.cIcNa1.SpNa1IntrogenParser,
                    master.handler.parser.impl.monitoringParser.MeNc1SpontParser,
                    master.handler.parser.impl.monitoringParser.SpNa1SpontParser;

    exports master;
}