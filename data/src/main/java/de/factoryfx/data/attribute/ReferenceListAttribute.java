package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObservableListJacksonAbleWrapper;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.ReferenceListMergeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import jdk.nashorn.internal.ir.annotations.Ignore;

public class ReferenceListAttribute<T extends Data> extends Attribute<ObservableList<T>> {
    private Data root;
    private Data parent;

    ObservableList<T> list = FXCollections.observableArrayList();
    private Class<T> clazz;

    public ReferenceListAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        this.clazz=clazz;
    }

    @JsonCreator
    protected ReferenceListAttribute() {
        super(null);
    }

    @JsonCreator
    protected ReferenceListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        super(null);
        this.list = list.unwrap();
    }

    public boolean add(T value) {
        get().add(value);
        return false;
    }


    @Override
    public void collectChildren(Set<Data> allModelEntities) {
        list.forEach(entity -> entity.collectModelEntitiesTo(allModelEntities));
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new ReferenceListMergeHelper<>(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity) {
        List<T> currentToEditList = get();

        for (T entity : currentToEditList) {
            entity.fixDuplicateObjects(getCurrentEntity);
        }

        List<T> fixedList = new ArrayList<>();
        for (T entity : currentToEditList) {
            Optional<Data> existingOptional = getCurrentEntity.apply(entity.getId());
            if (existingOptional.isPresent()) {
                fixedList.add((T) existingOptional.get());
            } else {
                fixedList.add(entity);
            }
        }
        currentToEditList.clear();
        currentToEditList.addAll(fixedList);

    }

    @Override
    public ObservableList<T> get() {
        return list;
    }



    @Override
    public void set(ObservableList<T> value) {
        this.list = value;
    }

    public boolean contains(T value) {
        return get().contains(value);
    }

    public void forEach(Consumer<? super T> action) {
        get().forEach(action);
    }

    public T get(int i) {
        return list.get(i);
    }

    @JsonProperty
    ObservableList<T> getList() {
        return list;
    }

    @JsonProperty
    void setList(ObservableList<T> list) {
        this.list = ((ObservableListJacksonAbleWrapper<T>)list).unwrap();
    }

    public void remove(T value) {
        get().remove(value);
    }

    public void set(int i, T value) {
        get().set(i, value);
    }

    public int size() {
        return get().size();
    }

    public Stream<T> stream() {
        return get().stream();
    }

    Map<AttributeChangeListener<ObservableList<T>>, ListChangeListener<T>> listeners= new HashMap<>();
    @Override
    public void addListener(AttributeChangeListener<ObservableList<T>> listener) {
        ListChangeListener<T> listListener = change -> listener.changed(ReferenceListAttribute.this,get());
        listeners.put(listener,listListener);
        list.addListener(listListener);
    }
    @Override
    public void removeListener(AttributeChangeListener<ObservableList<T>> listener) {
        list.removeListener(listeners.get(listener));
        listeners.remove(listener);
    }

    @Override
    public String getDisplayText() {
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ list.size()+")\n");
        for (T item:  list){
            stringBuilder.append(item.getDisplayText());
            stringBuilder.append(",\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.referenceList(this);
    }

    @Override
    @JsonIgnore
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(ObservableList.class,null,null,Data.class, AttributeTypeInfo.AttributeTypeCategory.REFERENCE_LIST, null);
    }


    private Optional<Function<Data,List<T>>> possibleValueProviderFromRoot=Optional.empty();
    private Optional<Supplier<T>> newValueProvider =Optional.empty();

    /**customise the list of selectable items*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A possibleValueProvider(Function<Data,List<T>> provider){
        possibleValueProviderFromRoot=Optional.of(provider);
        return (A)this;
    }

    /**customise how new values are created*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A newValueProvider(Supplier<T> newValueProvider){
        this.newValueProvider =Optional.of(newValueProvider);
        return (A)this;
    }

    public T addNewFactory(){
        T addedFactory=null;
        if (newValueProvider.isPresent()) {
            T newFactory = newValueProvider.get().get();
            get().add(newFactory);
            addedFactory = newFactory;
        }

        if (!newValueProvider.isPresent()){
            try {
                T newFactory = clazz.newInstance();
                get().add(newFactory);
                addedFactory = newFactory;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        for (Data data: get()){
            data.visitAttributesFlat(attribute -> {
                attribute.prepareEditing(root,data);
            });
        }

        return addedFactory;
    }

    @SuppressWarnings("unchecked")
    public List<T> possibleValues(){
        ArrayList<T> result = new ArrayList<>();
        possibleValueProviderFromRoot.ifPresent(factoryBaseListFunction -> {
            List<T> factories = factoryBaseListFunction.apply(root);
            factories.forEach(factory -> result.add(factory));
        });
        if (!possibleValueProviderFromRoot.isPresent()){
            for (Data factory: root.collectChildrenDeep()){
                if (clazz.isAssignableFrom(factory.getClass())){
                    result.add((T) factory);
                }
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void prepareEditing(Data root, Data parent){
        this.parent=parent;
        this.root=root;;
    }

    private boolean userEditable;
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A userEditable(boolean editable){
        userEditable=editable;
        return (A)this;
    }

    @Ignore
    public boolean isUserEditable(){
        return userEditable;
    }


}
