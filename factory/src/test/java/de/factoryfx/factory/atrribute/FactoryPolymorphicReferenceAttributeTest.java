package de.factoryfx.factory.atrribute;

import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.poly.ErrorPrinter;
import de.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryPolymorphicReferenceAttributeTest {


    @Test
    public void test_json_inside_data(){
        ExamplePolymorphicReferenceAttributeFactory factory = new ExamplePolymorphicReferenceAttributeFactory();
        factory.attribute.set(new ErrorPrinterFactory());
        ExamplePolymorphicReferenceAttributeFactory copy = ObjectMapperBuilder.build().copy(factory);

        Assert.assertNotNull(copy.attribute.get());
    }

    @Test
    public void test_json_defaultValue(){
        ExamplePolymorphicReferenceAttributeFactoryDefault factory = new ExamplePolymorphicReferenceAttributeFactoryDefault();
        ExamplePolymorphicReferenceAttributeFactoryDefault copy = ObjectMapperBuilder.build().copy(factory);

        Assert.assertNotNull(copy.attribute.get());
    }

    public static class ExamplePolymorphicReferenceAttributeFactoryDefault{
        public final FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<>(Printer.class).defaultValue(new ErrorPrinterFactory());
    }

    public static class ExamplePolymorphicReferenceAttributeFactory{
        public final FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<>(Printer.class);
    }


    @Test
    public void test_select(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().addBackReferences();
        ErrorPrinterFactory errorPrinterFactory = new ErrorPrinterFactory();
        polymorphicFactoryExample.reference.set(errorPrinterFactory);
        Assert.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_new_value(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().addBackReferences();

        List<FactoryBase<? extends Printer, ?,?>> factoryBases = polymorphicFactoryExample.reference.internal_createNewPossibleValues();
        Assert.assertEquals(ErrorPrinterFactory.class,new ArrayList<>(factoryBases).get(0).getClass());
        Assert.assertEquals(OutPrinterFactory.class,new ArrayList<>(polymorphicFactoryExample.reference.internal_createNewPossibleValues()).get(1).getClass());
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_setupUnsafe_validation(){
        new FactoryPolymorphicReferenceAttribute<Printer>().setupUnsafe(Printer.class,String.class);
    }

    @Test
    public void test_setupUnsafe_validation_happy_case(){
        new FactoryPolymorphicReferenceAttribute<Printer>().setupUnsafe(Printer.class,ErrorPrinterFactory.class);
    }

    @Test
    public void test_generatorInfo_safe(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_generatorInfo_unsafe(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }


    @Test
    public void test_generatorInfo_constructor(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<>(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    public static class ErrorPrinterFactory2 extends PolymorphicFactoryBase<ErrorPrinter,Void,ExampleFactoryA> {
        @Override
        public ErrorPrinter createImpl() {
            return new ErrorPrinter();
        }
    }

    @Test
    public void test_set(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().addBackReferences();
        ErrorPrinterFactory2 errorPrinterFactory = new ErrorPrinterFactory2();
        polymorphicFactoryExample.reference.set(errorPrinterFactory);
        Assert.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_null(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assert.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(new ErrorPrinterFactory());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assert.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_nullable(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class).nullable();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assert.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(new ErrorPrinterFactory());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assert.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class).nullable();
        Assert.assertFalse(attribute.internal_required());
    }
}