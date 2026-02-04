import cloud.yunyat.model.master.handler.parser.Parser;
import cloud.yunyat.model.master.handler.parser.impl.controlParser.cIcNa1.IcNa1AckConParser;
import cloud.yunyat.model.master.handler.parser.impl.controlParser.cIcNa1.IcNa1ActTermParser;
import cloud.yunyat.model.master.handler.parser.impl.controlParser.cIcNa1.MeNc1IntrogenParser;
import cloud.yunyat.model.master.handler.parser.impl.controlParser.cIcNa1.SpNa1IntrogenParser;
import cloud.yunyat.model.master.handler.parser.impl.monitoringParser.MeNc1SpontParser;
import cloud.yunyat.model.master.handler.parser.impl.monitoringParser.SpNa1SpontParser;

module cloud.yunyat.model {
    requires ini4j;
    requires static lombok;
    requires io.netty.handler;
    requires io.netty.codec;
    requires io.netty.transport;
    requires io.netty.buffer;
    requires io.netty.common;
//    requires netty.all;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;

    uses Parser;
    provides Parser
            with IcNa1AckConParser,
                    IcNa1ActTermParser,
                    MeNc1IntrogenParser,
                    SpNa1IntrogenParser,
                    MeNc1SpontParser,
                    SpNa1SpontParser;

//    exports cloud.yunyat.model;
    exports cloud.yunyat.model.master;
    exports cloud.yunyat.model.service;
}