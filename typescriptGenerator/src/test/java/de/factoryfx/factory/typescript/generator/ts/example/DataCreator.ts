//generated code don't edit manually
import ExampleData2 from "./ExampleData2";
import ExampleData from "./ExampleData";
import Data from "./Data";

export default class DataCreator  {



    public createData(json: any, idToDataMap: any): Data{
        if (!json) return null;
        let clazz=json['@class'];
        if (typeof json === 'string'){
            return idToDataMap[json];
        }
        if (clazz==='de.factoryfx.factory.typescript.generator.data.ExampleData'){
            let result: ExampleData= new ExampleData();
            result.mapFromJson(json,idToDataMap,this);
            return result;
        }
        if (clazz==='de.factoryfx.factory.typescript.generator.data.ExampleData2'){
            let result: ExampleData2= new ExampleData2();
            result.mapFromJson(json,idToDataMap,this);
            return result;
        }
        return null;
    }

    public createDataList(json: any, idToDataMap: any): Data[]{
        let result: Data[]=[];
        for (let entry of json) {
            result.push(this.createData(entry,idToDataMap));
        }
        return result;
    }


}