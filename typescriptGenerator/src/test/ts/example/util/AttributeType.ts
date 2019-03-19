//generated code don't edit manually
export enum AttributeType  {

BooleanAttribute="BooleanAttribute",
LocalDateTimeAttribute="LocalDateTimeAttribute",
FactoryReferenceListAttribute="FactoryReferenceListAttribute",
LocalTimeAttribute="LocalTimeAttribute",
LocalDateAttribute="LocalDateAttribute",
URIListAttribute="URIListAttribute",
ByteAttribute="ByteAttribute",
FactoryReferenceAttribute="FactoryReferenceAttribute",
DataReferenceAttribute="DataReferenceAttribute",
StringAttribute="StringAttribute",
CharAttribute="CharAttribute",
DataReferenceListAttribute="DataReferenceListAttribute",
EncryptedStringAttribute="EncryptedStringAttribute",
EnumListAttribute="EnumListAttribute",
EnumAttribute="EnumAttribute",
LongListAttribute="LongListAttribute",
I18nAttribute="I18nAttribute",
FileContentAttribute="FileContentAttribute",
CharListAttribute="CharListAttribute",
LongAttribute="LongAttribute",
URIAttribute="URIAttribute",
FloatAttribute="FloatAttribute",
PasswordAttribute="PasswordAttribute",
DoubleListAttribute="DoubleListAttribute",
IntegerListAttribute="IntegerListAttribute",
ShortListAttribute="ShortListAttribute",
ShortAttribute="ShortAttribute",
BigDecimalAttribute="BigDecimalAttribute",
ByteArrayAttribute="ByteArrayAttribute",
IntegerAttribute="IntegerAttribute",
FloatListAttribute="FloatListAttribute",
InstantAttribute="InstantAttribute",
DoubleAttribute="DoubleAttribute",
BigIntegerAttribute="BigIntegerAttribute",
LocaleAttribute="LocaleAttribute",
StringListAttribute="StringListAttribute",
DurationAttribute="DurationAttribute",
ByteListAttribute="ByteListAttribute"

}
export namespace AttributeType {
    export function fromJson(json: string): AttributeType{
        return AttributeType[json];
    }
    export function toJson(value: AttributeType): string{
        if (value) return value.toString();
    }
}
