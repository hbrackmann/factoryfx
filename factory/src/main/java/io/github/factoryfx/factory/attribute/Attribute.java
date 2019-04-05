package io.github.factoryfx.factory.attribute;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import io.github.factoryfx.factory.validation.Validation;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.validation.ValidationResult;

public abstract class Attribute<T,A extends Attribute<T,A>>{

    @JsonIgnore
    private Set<Validation<T>> validations;

    public Attribute() {

    }

    public abstract boolean internal_mergeMatch(T value);

    public boolean internal_ignoreForMerging() {
        return false;
    }


    /**
     *
     * @param originalAttribute originalAttribute
     * @param newAttribute newAttribute
     * @return true if merge conflict
     */
    public boolean internal_hasMergeConflict(Attribute<?,?> originalAttribute, Attribute<?,?> newAttribute) {
        if (newAttribute.internal_mergeMatch(originalAttribute)) {
            return false;
        }
        if (internal_mergeMatch(originalAttribute)) {
            return false;
        }
        if (internal_mergeMatch(newAttribute)) {
            return false;
        }
        return true;
    }

    /**
     * check if merge should be executed e.g. not if values ar equals
     * @param originalAttribute originalAttribute from common version
     * @param newAttribute newAttribute from update
     * @return true if merge should be executed
     * */
    public boolean internal_isMergeable(Attribute<?,?> originalAttribute, Attribute<?,?> newAttribute) {
        if (!internal_mergeMatch(originalAttribute) || internal_mergeMatch(newAttribute)) {
            return false ;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void internal_merge(Attribute<?,?> newValue) {
        set((T) newValue.get());
    }

    /*
        see test {{@Link MergeTest#test_duplicate_ids_bug}} why this is needed
    */
    public abstract void internal_fixDuplicateObjects(Map<String, FactoryBase<?,?>> idToDataMap);

    public void internal_copyTo(A copyAttribute){
        //nothing
    }

    @SuppressWarnings("unchecked")
    public  void  internal_copyToUnsafe(Attribute<?,?> copyAttribute){
        internal_copyTo((A)copyAttribute);
    }

    @SuppressWarnings("unchecked")
    public boolean internal_mergeMatch(Attribute<?,?> attribute) {
        return internal_mergeMatch((T) attribute.get());
    }

    public abstract void internal_semanticCopyTo(A copyAttribute);

    @SuppressWarnings("unchecked")
    public void internal_semanticCopyToUnsafe(Attribute<?,?> copyAttribute){
        internal_semanticCopyTo((A)copyAttribute);
    }

    public List<ValidationError> internal_validate(FactoryBase<?,?> parent, String attributeVariableName) {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (validations==null){
            return validationErrors;
        }
        for (Validation<T> validation : validations) {
            ValidationResult validationResult = validation.validate(get());
            if (validationResult.validationFailed()){
                validationErrors.add(validationResult.createValidationError(this,parent,attributeVariableName));
            }
        }
        return validationErrors;
    }

    public boolean internal_required() {
        return false;
    }

    public void internal_endUsage() {
        //nothing
    }

    public abstract void internal_addListener(AttributeChangeListener<T,A> listener);

    /**
     * remove added Listener or Listener inside WeakAttributeChangeListener
     * @param listener listener
     * */
    public abstract void internal_removeListener(AttributeChangeListener<T,A> listener);

    public abstract T get();

    public abstract void set(T value);

    @JsonIgnore
    public abstract String getDisplayText();

    @SuppressWarnings("unchecked")
    public A validation(Validation<T> validation){
        if (validations==null){
            validations=new HashSet<>();
        }
        this.validations.add(validation);
        return (A)this;
    }
    private static final Locale PORTUGUESE =new Locale("pt", "PT");
    private static final Locale SPANISH=new Locale("es", "ES");
    public String internal_getPreferredLabelText(Locale locale){
        if (locale.equals(this.customLocale)){
            return customLocaleText;
        }

        if (en!=null && locale.equals(Locale.ENGLISH)){
            return en;
        }
        if (de!=null && locale.equals(Locale.GERMAN)){
            return de;
        }
        if (es!=null && locale.equals(SPANISH)){
            return es;
        }
        if (fr!=null && locale.equals(Locale.FRANCE)){
            return fr;
        }
        if (it!=null && locale.equals(Locale.ITALIAN)){
            return it;
        }
        if (pt!=null && locale.equals(PORTUGUESE)){
            return pt;
        }

        if (en!=null){
            return en;
        }
        if (de!=null){
            return de;
        }
        if (es!=null){
            return es;
        }
        if (fr!=null){
            return fr;
        }
        if (it!=null){
            return it;
        }
        if (pt!=null){
            return pt;
        }

        return "";
    }

    public String internal_getAddonText(){
        return addonText;
    }

    public boolean internal_hasWritePermission(Function<String,Boolean> permissionChecker){
        return permission == null || permissionChecker.apply(permission);
    }

    @JsonIgnore
    private String permission;

    @SuppressWarnings("unchecked")
    public A permission(String permission){
        this.permission = permission;
        return (A)this;
    }

    @JsonIgnore
    private String addonText;

    /**
     * add-on text for the attribute, text that is displayed an the right side of the input usually used for units,%,currency symbol etc
     *
     * @param addonText the text
     * @return self attribute
     */
    @SuppressWarnings("unchecked")
    public A addonText(String addonText){
        this.addonText=addonText;
        return (A)this;
    }


    @SuppressWarnings("unchecked")
    public A labelText(String text){
        en=text;
        return (A)this;
    }

    /**
     * Set text with custom locale
     * currently only one additional text/local is supported
     * @param labelText text
     * @param locale locale of the text
     * @return self
     */
    @SuppressWarnings("unchecked")
    public A labelText(String labelText, Locale locale){
        this.customLocale=locale;
        this.customLocaleText=labelText;
        return (A)this;
    }

    //stored in single fields to avoids object bloat
    @JsonIgnore
    String en;
    @JsonIgnore
    String de;
    @JsonIgnore
    String es;
    @JsonIgnore
    String fr;
    @JsonIgnore
    String it;
    @JsonIgnore
    String pt;

    Locale customLocale;
    String customLocaleText;

    @SuppressWarnings("unchecked")
    public A en(String text) {
        en=text;
        return (A)this;
    }
    @SuppressWarnings("unchecked")
    public A de(String text) {
        de=text;
        return (A)this;
    }

    @SuppressWarnings("unchecked")
    public A es(String text) {
        es=text;
        return (A)this;
    }

    @SuppressWarnings("unchecked")
    public A fr(String text) {
        fr=text;
        return (A)this;
    }

    @SuppressWarnings("unchecked")
    public A it(String text) {
        it=text;
        return (A)this;
    }

    @SuppressWarnings("unchecked")
    public A pt(String text) {
        pt=text;
        return (A)this;
    }

    @JsonIgnore
    String tooltipEn;
    @JsonIgnore
    String tooltipDe;

    @SuppressWarnings("unchecked")
    public A tooltipEn(String tooltip){
        tooltipEn=tooltip;
        return (A)this;
    }

    @SuppressWarnings("unchecked")
    public A tooltipDe(String tooltip){
        tooltipDe=tooltip;
        return (A)this;
    }

    public String internal_getPreferredTooltipText(Locale locale){
        if (tooltipEn!=null && locale.equals(Locale.ENGLISH)){
            return tooltipEn;
        }
        if (tooltipDe!=null && locale.equals(Locale.GERMAN)){
            return tooltipDe;
        }

        if (tooltipEn!=null){
            return tooltipEn;
        }
        if (tooltipDe!=null){
            return tooltipDe;
        }
        return "";
    }


    public boolean internal_isUserReadOnly() {
        if (userReadOnly){
            return true;
        }
        if (readyOnlySupplier!=null){
            return readyOnlySupplier.get();
        }
        return false;
    }

    private boolean userReadOnly=false;
    /**
     * marks the attribute as readonly for the user
     * @return self
     */
    @SuppressWarnings("unchecked")
    public A userReadOnly(){
        userReadOnly=true;
        return (A)this;
    }

    private Supplier<Boolean> readyOnlySupplier;

    /**
     * readonly state depend on data in tree
     *
     * @param readyOnlySupplier  readyOnlySupplier
     * @return self
     * */
    @SuppressWarnings("unchecked")
    public A userReadOnly(Supplier<Boolean> readyOnlySupplier){
        this.readyOnlySupplier=readyOnlySupplier;
        return (A)this;
    }

    public AttributeStorageMetadata createAttributeStorageMetadata(String variableName){
        return new AttributeStorageMetadata(variableName,getClass().getName(),false, null);
    }

}