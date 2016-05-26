package de.factoryfx.model.merge;

import java.util.Optional;

import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.LiveObject;

public class MergeHelperTestBase {
    public <T extends FactoryBase<? extends LiveObject,T>> MergeDiff merge(T current, T originalValue, T newValue){
        MergeResult mergeResult = new MergeResult();
        current.merge(Optional.ofNullable(originalValue), Optional.ofNullable(newValue), mergeResult);
        MergeDiff mergeDiff = mergeResult.getMergeDiff();
        if (mergeDiff.hasNoConflicts()){
            mergeResult.executeMerge();
        }
        return mergeDiff;
    }
}
