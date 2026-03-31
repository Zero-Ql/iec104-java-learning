package cloud.yunyat.model.pojo;

import lombok.Value;

import java.util.Map;

@Value
public class ParsedResult {
    int point;
    Object value;
    int quality;
    Map<String, Boolean> qualityBits;
}
