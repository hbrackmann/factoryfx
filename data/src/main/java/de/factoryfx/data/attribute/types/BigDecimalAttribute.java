package de.factoryfx.data.attribute.types;

import java.math.BigDecimal;

import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class BigDecimalAttribute extends ImmutableValueAttribute<BigDecimal,BigDecimalAttribute> {

    private String decimalFormatPattern;

    public BigDecimalAttribute() {
        super(BigDecimal.class);
    }

    public String internal_getDecimalFormatPattern() {
        if (this.decimalFormatPattern==null){
            return "#,#";
        }
        return decimalFormatPattern;
    }

    /**
     * @see java.text.DecimalFormat
     * @param decimalFormatPattern pattern
     */
    public BigDecimalAttribute decimalFormatPattern(String decimalFormatPattern) {
        this.decimalFormatPattern=decimalFormatPattern;
        return this;
    }

}