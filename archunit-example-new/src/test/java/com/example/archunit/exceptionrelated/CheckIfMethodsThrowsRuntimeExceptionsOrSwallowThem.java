package com.example.archunit.exceptionrelated;

import static com.tngtech.archunit.base.DescribedPredicate.doNot;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.have;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.codeUnits;
import static java.util.stream.Collectors.toSet;

import com.tngtech.archunit.core.domain.AccessTarget;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.Optional;
import java.util.Set;

@AnalyzeClasses(packages = {"com.example.archunit.exceptionrelated"})
public class CheckIfMethodsThrowsRuntimeExceptionsOrSwallowThem {

    @ArchTest
    ArchRule codeUnits_should_declare_all_RuntimeExceptions_they_throw = codeUnits()
            .that(doNot(have(owner(assignableTo(RuntimeException.class)))))
            .should(new ArchCondition<JavaCodeUnit>("declare all RuntimeExceptions they throw") {
                @Override
                public void check(JavaCodeUnit codeUnit, ConditionEvents events) {
                    // TODO: ArchUnit 0.23.1 might not have an API to get exceptions actually thrown.
                    // As a first approximation, the following code collects all RuntimeExceptions that are instantiated
                    // – which has false positives (exceptions that are instantiated, but not thrown),
                    //   as well as false negatives (exceptions that are created via factory methods and thrown).
                    // Accounting for the false negatives in the same way as here is left as an exercise for the interested reader.
                    Set<JavaClass> instantiatedRuntimeExceptions = codeUnit.getConstructorCallsFromSelf().stream()
                            .map(JavaAccess::getTargetOwner)
                            .filter(targetClass -> targetClass.isAssignableTo(RuntimeException.class))
                            .collect(toSet());
                    boolean satisfied = codeUnit.getExceptionTypes().containsAll(instantiatedRuntimeExceptions);
                    String message = String.format("%s does%s declare all RuntimeExceptions it instantiates in %s",
                            codeUnit.getDescription(), satisfied ? "" : " not", codeUnit.getSourceCodeLocation());
                    events.add(new SimpleConditionEvent(codeUnit, satisfied, message));
                }
            });

    @ArchTest
    ArchRule codeUnits_should_declare_all_RuntimeExceptions_of_methods_they_call = codeUnits()
            .should(new ArchCondition<JavaCodeUnit>("declare all RuntimeExceptions of methods they call") {
                @Override
                public void check(JavaCodeUnit codeUnit, ConditionEvents events) {
                    Set<JavaClass> runtimeExceptionsDeclaredByCalledMethods = codeUnit.getMethodCallsFromSelf().stream()
                            .map(JavaAccess::getTarget)
                            .map(AccessTarget.MethodCallTarget::resolveMember)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .flatMap(method -> method.getExceptionTypes().stream())
                            .filter(exceptionType -> exceptionType.isAssignableTo(RuntimeException.class))
                            .collect(toSet());
                    boolean satisfied = codeUnit.getExceptionTypes().containsAll(runtimeExceptionsDeclaredByCalledMethods);
                    String message = String.format("%s does%s declare all RuntimeExceptions of methods they call declare in %s",
                            codeUnit.getDescription(), satisfied ? "" : " not", codeUnit.getSourceCodeLocation());
                    events.add(new SimpleConditionEvent(codeUnit, satisfied, message));
                }
            });
}
