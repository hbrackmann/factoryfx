//generated code don't edit manually
import {AttributeMetadata} from "./AttributeMetadata";
import {Data} from "./Data";

export interface AttributeValueAccessor<T> {
    getValue(): T;
    setValue(value: T);
}