import {AttributeAccessor} from "../../AttributeAccessor";
import {Widget} from "../../base/Widget";

export abstract class AttributeEditorWidget extends Widget {

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super()
    }

    createLabel(locale: string): HTMLLabelElement{
        let label: HTMLLabelElement = document.createElement("label");
        label.htmlFor=this.inputId;
        label.textContent=this.attributeAccessor.getAttributeMetadata().getLabelText("en");
        label.className = "col-xl-2 col-form-label";
        label.style.textOverflow="clip";
        label.style.overflow="hidden";
        return label;
    }

}