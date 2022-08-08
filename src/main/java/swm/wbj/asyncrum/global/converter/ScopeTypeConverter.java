package swm.wbj.asyncrum.global.converter;

import org.springframework.core.convert.converter.Converter;
import swm.wbj.asyncrum.global.type.ScopeType;

public class ScopeTypeConverter implements Converter<String, ScopeType> {

    @Override
    public ScopeType convert(String source) {
        return ScopeType.of(source);
    }
}
