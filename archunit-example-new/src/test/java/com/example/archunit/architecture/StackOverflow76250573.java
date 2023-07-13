package com.example.archunit.architecture;

import com.example.archunit.ddd.Deneme1;
import com.example.archunit.ddd.Deneme2;
import com.example.archunit.ddd.Foo;
import com.example.archunit.ddd.Hello;
import com.tngtech.archunit.core.domain.JavaCall;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.slf4j.Logger;

import static com.example.archunit.architecture.ArchitectureConstants.DEFAULT_PACKAGE;
import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameMatching;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.lang.conditions.ArchConditions.callMethodWhere;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.is;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = DEFAULT_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
public class StackOverflow76250573 {

    @ArchIgnore
    @ArchTest
    public static ArchRule noInfoLoggingWithoutContext =
            noClasses().should(callMethodWhere(target(is(describe("logger.info without context",
                    target ->
                            target.getOwner().isAssignableTo(Logger.class)
                                    && target.getName().equals("info")
                                    && target.getRawParameterTypes().size() < 2
            )))));

    @ArchTest
    public static void some_architecture_rule(JavaClasses classes) {
        ArchRule noInfoLoggingWithoutContext =
                noClasses().should(callMethodWhere(target(is(describe("logger.info without context",
                        target ->
                                target.getOwner().isAssignableTo(Logger.class)
                                        && target.getName().equals("info")
                                        && target.getRawParameterTypes().size() < 2
                )))));

        ArchRule noInfoLoggingWithoutContext2 =
                noClasses().should(callMethodWhere(owner(is(describe("logger.info without context",
                        target ->
                                target.getOwner().isAssignableTo(Logger.class)
                                        && target.getName().equals("info")
                                        && target.getRawParameterTypes().size() < 2
                )))));
        noInfoLoggingWithoutContext.check(classes);
    }

    @ArchTest
    public static final ArchRule no_class_except_Deneme1_and_deneme2_should_call_Foo = noClasses()
            .that(not(name(Deneme1.class.getName()))
                    .and(not(name(Deneme2.class.getName()))))
            .should().callMethodWhere(target(nameMatching("method1"))
                    .and(target(owner(assignableTo(Foo.class)))))
            .orShould().callMethodWhere(target(nameMatching("method2"))
                    .and(target(owner(assignableTo(Foo.class)))));

    @ArchTest
    public static final ArchRule no_class_except_Deneme1_and_deneme2_should_call_Foo2 = noClasses()
            .that().doNotHaveFullyQualifiedName(Deneme1.class.getName())
            .and().doNotHaveFullyQualifiedName(Deneme2.class.getName())
            // alternative 1:
            .should().callMethod(Foo.class, "method1")
            .orShould().callMethod(Foo.class, "method2");
    // alternative 2:
    // .should(callMethod(Foo.class, "method1").or(callMethod(Foo.class, "method2")));

    @ArchTest
    static ArchRule no_code_units_should_declare_exceptions = noCodeUnits()
            .should(new ArchCondition<JavaCodeUnit>("declare exceptions") {
                @Override
                public void check(JavaCodeUnit codeUnit, ConditionEvents events) {
                    int nThrowsDeclarations = codeUnit.getThrowsClause().size();
                    String message = String.format("%s has %d throws declarations in %s",
                            codeUnit.getDescription(), nThrowsDeclarations, codeUnit.getSourceCodeLocation()
                    );
                    events.add(new SimpleConditionEvent(codeUnit, nThrowsDeclarations > 0, message));
                }
            });

    @ArchTest
    static ArchRule if_methodA_is_called_then_methodB_shoould_also_be_called = noCodeUnits()
            .should(new ArchCondition<JavaCodeUnit>("methodB must be called if methodA is called") {
                @Override
                public void check(JavaCodeUnit codeUnit, ConditionEvents events) {
                    noMethods().should(new ArchCondition<>("bla bla") {
                        @Override
                        public void check(JavaMethod javaMethod, ConditionEvents conditionEvents) {

                        }
                    });

                    boolean b = codeUnit.getCallsFromSelf().stream()
                            .map(JavaCall::getTarget)
                            .anyMatch(target ->
                                    target.getOwner().isEquivalentTo(Hello.class)
                                            && target.getName().equals("B") && target.getParameterTypes().isEmpty());
                }
            });

    @ArchTest
    static void methoB_must_be_called_if_methodA_is_called(JavaClasses classes) {
        ArchRule rule = methods().should(new ArchCondition<JavaCodeUnit>("run methodB if they called methodA once") {
            @Override
            public void check(JavaCodeUnit codeUnit, ConditionEvents conditionEvents) {
                boolean isACalled = codeUnit.getCallsFromSelf().stream()
                        .map(JavaCall::getTarget)
                        .anyMatch(target ->
                                target.getOwner().isEquivalentTo(Hello.class)
                                        && target.getName().equals("A") && target.getParameterTypes().isEmpty());
                if (isACalled) {
                    boolean isBCalled = codeUnit.getCallsFromSelf().stream()
                            .map(JavaCall::getTarget)
                            .anyMatch(target ->
                                    target.getOwner().isEquivalentTo(Hello.class)
                                            && target.getName().equals("B") && target.getParameterTypes().isEmpty());
                    if (!isBCalled)
                        conditionEvents.add(new SimpleConditionEvent(codeUnit, false, "method B is not called"));
                }
            }
        });
        rule.check(classes);
    }

//    @ArchTest
//    static void deneme(JavaClasses classes) {
//        ArchCondition<JavaCodeUnit> methodAisCalled = new ArchCondition<>("bla bla") {
//            @Override
//            public void check(JavaCodeUnit codeUnit, ConditionEvents conditionEvents) {
//                boolean isACalled = codeUnit.getCallsFromSelf().stream()
//                        .map(JavaCall::getTarget)
//                        .anyMatch(target ->
//                                target.getOwner().isEquivalentTo(Hello.class)
//                                        && target.getName().equals("A") && target.getParameterTypes().isEmpty());
//                if(isACalled)
//                    conditionEvents.add(new SimpleConditionEvent(codeUnit, true, "method A is called"));
//                else
//                    conditionEvents.add(new SimpleConditionEvent(codeUnit, true, "method A is not called"));
//            }
//        };
//
//        ArchCondition<JavaCodeUnit> methodBisCalled = new ArchCondition<>("bla bla") {
//            @Override
//            public void check(JavaCodeUnit codeUnit, ConditionEvents conditionEvents) {
//                methodAisCalled.check(codeUnit, conditionEvents);
//                long count = conditionEvents.getViolating().stream().filter(i -> !i.isViolation() && i.getDescriptionLines().contains("method A is called")).findFirst().stream().count();
//                if(count > 0) {
//                    boolean isBCalled = codeUnit.getCallsFromSelf().stream()
//                            .map(JavaCall::getTarget)
//                            .anyMatch(target ->
//                                    target.getOwner().isEquivalentTo(Hello.class)
//                                            && target.getName().equals("B") && target.getParameterTypes().isEmpty());
//                    if(!isBCalled)
//                        conditionEvents.add(new SimpleConditionEvent(codeUnit, true, "method B is called"));
//                }
//                else
//                    conditionEvents.add(new SimpleConditionEvent(codeUnit, true, "no need to call method B"));
//            }
//        };
//
//        ArchRule rule = methods().should(methodBisCalled);
//        rule.check(classes);
//    }
}