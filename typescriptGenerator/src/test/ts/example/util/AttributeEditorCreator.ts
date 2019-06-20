//generated code don't edit manually
import {AttributeEditor} from "./AttributeEditor";
import {AttributeType} from "./AttributeType";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditorStringAttribute} from "./AttributeEditorStringAttribute";
import {AttributeEditorFallback} from "./AttributeEditorFallback";
import {FactoryEditor} from "./FactoryEditor";
import {AttributeEditorFactoryAttribute} from "./AttributeEditorFactoryAttribute";
import {AttributeEditorFactoryListAttribute} from "./AttributeEditorFactoryListAttribute";
import {AttributeEditorIntegerAttribute} from "./AttributeEditorIntegerAttribute";
import {AttributeEditorEnumAttribute} from "./AttributeEditorEnumAttribute";

export class AttributeEditorCreator {
    private attributeEditors: AttributeEditor[];

    constructor(attributeEditors: AttributeEditor[]) {
        if (!attributeEditors){
            this.attributeEditors=[];
            this.attributeEditors[AttributeType.StringAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorStringAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.IntegerAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorIntegerAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.EnumAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorEnumAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.EnumAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorEnumAttribute(attributeAccessor,inputId);


            this.attributeEditors[AttributeType.FactoryAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorFactoryAttribute(attributeAccessor,inputId,factoryEditor);
            this.attributeEditors[AttributeType.FactoryListAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorFactoryListAttribute(attributeAccessor,inputId,factoryEditor);
            this.attributeEditors[AttributeType.FactoryPolymorphicAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorFactoryAttribute(attributeAccessor,inputId,factoryEditor);
            this.attributeEditors[AttributeType.FactoryPolymorphicListAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorFactoryListAttribute(attributeAccessor,inputId,factoryEditor);

        } else {
            this.attributeEditors=attributeEditors;
        }
    }


    create(attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor): AttributeEditor{
        if (!this.attributeEditors[attributeAccessor.getAttributeMetadata().getType()]){
            return new AttributeEditorFallback(attributeAccessor,inputId);
        }
        return this.attributeEditors[attributeAccessor.getAttributeMetadata().getType()](attributeAccessor,inputId,factoryEditor);

    }
}