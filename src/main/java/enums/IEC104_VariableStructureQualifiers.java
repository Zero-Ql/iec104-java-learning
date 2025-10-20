package enums;

import lombok.Getter;

public enum IEC104_VariableStructureQualifiers {
    /**
     * 总召可变结构限定词
     */
    C_IC_NA_1_QUALIFIER(IEC104_TypeIdentifier.C_IC_NA_1, 0x14),

    /**
     * 遥测、遥调可变结构限定词
     */
    M_ME_NC_1_QUALIFIER(null, 0x00),

    /**
     * 选择(查询)可变结构限定词
     */
    M_ME_NC_2_QUALIFIER(null, 0x80),

    ;

    @Getter
    private final IEC104_TypeIdentifier typeIdentifier;
    @Getter
    private final byte qualityDescriptors;

    IEC104_VariableStructureQualifiers(IEC104_TypeIdentifier typeIdentifier, int qualityDescriptors) {
        this.typeIdentifier = typeIdentifier;
        this.qualityDescriptors = (byte) qualityDescriptors;
    }

    public static IEC104_VariableStructureQualifiers getQualifiers(IEC104_TypeIdentifier typeIdentifier, byte qualityDescriptors) {
        /**
         * 根据 typeIdentifier(类型标识) 和 qualityDescriptors(质量描述符) 返回匹配的可变结构限定词
         */
        for (IEC104_VariableStructureQualifiers qualifier : IEC104_VariableStructureQualifiers.values()) {
            if (qualifier.getTypeIdentifier() == typeIdentifier && qualifier.getQualityDescriptors() == qualityDescriptors) {
                return qualifier;
            }
        }

        // 如果 typeIdentifier 匹配 M_ME_NA_1(归一化值) or M_ME_NB_1(标度化值) or M_ME_NC_1(短浮点值) 中的一个，且 qualityDescriptors(质量描述符) 匹配
        if ((IEC104_TypeIdentifier.M_ME_NA_1.equals(typeIdentifier) ||
                IEC104_TypeIdentifier.M_ME_NB_1.equals(typeIdentifier) ||
                IEC104_TypeIdentifier.M_ME_NC_1.equals(typeIdentifier)) &&
                M_ME_NC_1_QUALIFIER.getQualityDescriptors() == qualityDescriptors) {
            return M_ME_NC_1_QUALIFIER;
        }
        return null;
    }
}
