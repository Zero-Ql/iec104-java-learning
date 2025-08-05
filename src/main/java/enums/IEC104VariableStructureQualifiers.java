package enums;

import lombok.Getter;

public enum IEC104VariableStructureQualifiers {
    /**
     * 总召可变结构限定词
     */
    C_IC_NA_1_QUALIFIER(IEC104TypeIdentifier.C_IC_NA_1, 0x14),

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
    private final IEC104TypeIdentifier typeIdentifier;
    @Getter
    private final byte value;

    IEC104VariableStructureQualifiers(IEC104TypeIdentifier typeIdentifier, int value){
        this.typeIdentifier = typeIdentifier;
        this.value = (byte)value;
    }

    public static IEC104VariableStructureQualifiers getQualifiers(IEC104TypeIdentifier typeIdentifier, byte value){
        /*
           根据 typeIdentifier(类型标识) 和 value 返回匹配的可变结构限定词
         */
        for (IEC104VariableStructureQualifiers qualifier : IEC104VariableStructureQualifiers.values()){
            if (qualifier.getTypeIdentifier() == typeIdentifier && qualifier.getValue() == value){
                return qualifier;
            }
        }

//        if ()
        return null;
    }
}
