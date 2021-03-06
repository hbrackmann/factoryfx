package io.github.factoryfx.factory.attribute.types;

import java.util.Locale;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import io.github.factoryfx.factory.util.LanguageText;

public class I18nAttribute extends ImmutableValueAttribute<LanguageText,I18nAttribute> {
    public I18nAttribute() {
        super();
    }

    public I18nAttribute en(String text) {
        getOrCreate().en(text);
        return this;
    }

    public I18nAttribute de(String text) {
        getOrCreate().de(text);
        return this;
    }

    public I18nAttribute es(String text) {
        getOrCreate().es(text);
        return this;
    }

    public I18nAttribute fr(String text) {
        getOrCreate().fr(text);
        return this;
    }

    public I18nAttribute it(String text) {
        getOrCreate().it(text);
        return this;
    }

    public I18nAttribute pt(String text) {
        getOrCreate().pt(text);
        return this;
    }

    private LanguageText getOrCreate(){
        if (get()==null){
            set(new LanguageText());
        }
        return get();
    }


    public String getPreferred(Locale locale) {
        return get().internal_getPreferred(locale);
    }


}
