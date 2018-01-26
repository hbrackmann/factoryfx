package de.factoryfx.data.storage.inmemory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;


public class InMemoryDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final Map<String,DataAndStoredMetadata<R,S>> storage = new TreeMap<>();
    private final Map<String,DataAndScheduledMetadata<R,S>> future = new TreeMap<>();
    private String current;
    private final R initialFactory;
    private final ChangeSummaryCreator<R,S> changeSummaryCreator;

    public InMemoryDataStorage(R initialFactory, ChangeSummaryCreator<R,S> changeSummaryCreator){
        initialFactory.internal().checkUsable();
        this.initialFactory=initialFactory;
        this.changeSummaryCreator = changeSummaryCreator;
    }

    public InMemoryDataStorage(R initialFactory){
        this(initialFactory,(d)->null);
    }

    @Override
    public R getHistoryFactory(String id) {
        DataAndStoredMetadata<R,S> data = storage.get(id);
        return data.root.internal().copyFromRoot();
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return storage.values().stream()/*.filter(factory -> !factory.metadata.id.equals(current))*/.map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public DataAndStoredMetadata<R,S> getCurrentFactory() {
        DataAndStoredMetadata<R,S> result = storage.get(current);
        return new DataAndStoredMetadata<>(result.root.internal().copy(),result.metadata);
    }

    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, MergeDiffInfo<R> mergeDiff) {
        update.root.internal().checkUsable();
        final StoredDataMetadata<S> storedDataMetadata = new StoredDataMetadata<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                user,
                comment,
                update.metadata.baseVersionId,
                update.metadata.dataModelVersion,
                changeSummaryCreator.createChangeSummary(mergeDiff));

        final DataAndStoredMetadata<R,S> updateData = new DataAndStoredMetadata<>(update.root, storedDataMetadata);
        storage.put(updateData.metadata.id, updateData);
        current=updateData.metadata.id;
    }

    @Override
    public DataAndNewMetadata<R> getPrepareNewFactory(){
        NewDataMetadata metadata = new NewDataMetadata();
        metadata.baseVersionId=current;
        return new DataAndNewMetadata<>(getCurrentFactory().root,metadata);
    }

    @Override
    public void loadInitialFactory() {
        current = UUID.randomUUID().toString();
        StoredDataMetadata<S> metadata = new StoredDataMetadata<>(current, "System", "initial", current, 0,null);
        storage.put(current,new DataAndStoredMetadata<>(initialFactory, metadata));
    }

    @Override
    public Collection<ScheduledDataMetadata<S>> getFutureFactoryList() {
        return future.values().stream()/*.filter(factory -> !factory.metadata.id.equals(current))*/.map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public void deleteFutureFactory(String id) {
        future.remove(id);
    }

    public R getFutureFactory(String id) {
        DataAndScheduledMetadata<R,S> data = future.get(id);
        return data.root.internal().copyFromRoot();
    }

    @Override
    public ScheduledDataMetadata<S> addFutureFactory(R futureFactory, NewScheduledDataMetadata futureFactoryMetadata, String user, String comment, MergeDiffInfo<R> mergeDiff) {
        final ScheduledDataMetadata<S> storedFactoryMetadata = new ScheduledDataMetadata<>(
            LocalDateTime.now(),
            UUID.randomUUID().toString(),
            user,
            comment,
            futureFactoryMetadata.newDataMetadata.baseVersionId,
            futureFactoryMetadata.newDataMetadata.dataModelVersion,
            changeSummaryCreator.createFutureChangeSummary(mergeDiff),
            futureFactoryMetadata.scheduled
        );

        final DataAndScheduledMetadata<R,S> updateData = new DataAndScheduledMetadata<>(futureFactory, storedFactoryMetadata);
        future.put(updateData.metadata.id, updateData);
        return storedFactoryMetadata;
    }

}
