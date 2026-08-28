package cloud.yunyat.model.pojo;

import java.util.Map;

public record ParsedResult(int point, Object value, int quality, Map<String, Boolean> qualityBits) {
}
