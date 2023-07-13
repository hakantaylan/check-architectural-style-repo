package com.example.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMember;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
//import org.springframework.stereotype.Repository;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.core.domain.JavaModifier.PRIVATE;
import static com.tngtech.archunit.core.domain.JavaModifier.STATIC;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.example.archunit")
class CoverageTests {

    public static final double MIN_COVERAGE_SERV_CONTRL = .9;
    public static final double MIN_COVERAGE_REPOS = 1.0;

//    @ArchTest
//    static final ArchRule controllersAndServicesNeedToHaveAssociatedTestClasses =
//            classes()
//                    .that()
//                    .areAnnotatedWith(RestController.class)
//                    .or()
//                    .areAnnotatedWith(Service.class)
//                    .should(haveTheirEquivalentTestClass())
//                    .andShould(havePercentMethodCoverage(MIN_COVERAGE_SERV_CONTRL));
//
//
//    @ArchTest
//    static final ArchRule repositoriesNeedToHaveAssociatedTestsForAllMethods =
//            classes()
//                    .that()
//                    .areMetaAnnotatedWith(Repository.class)
//                    .should(haveTheirEquivalentTestClass())
//                    .andShould(havePercentMethodCoverage(MIN_COVERAGE_REPOS));

        @ArchTest
    static final ArchRule classes_that_reside_in_specified_package_should_have_test_classes =
            classes()
                    .that()
                    .resideInAPackage("com.example.archunit..")
                    .should(haveTheirEquivalentTestClass())
                    .andShould(havePercentMethodCoverage(MIN_COVERAGE_SERV_CONTRL));

    private static ArchCondition<? super JavaClass> haveTheirEquivalentTestClass() {
        return new ArchCondition<>("have associated test classes") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                final String className = item.getSimpleName();
                if (Arrays.asList(excludedClasses).contains(className) || item.getMethods().isEmpty()) {
                    return;
                }

                final boolean hasTestClass = getEquivalentTestClasses(item).size() > 0;

                if (!hasTestClass) {
                    events.add(
                            new SimpleConditionEvent(
                                    item, false, "%s does not have a test class".formatted(className)));
                }
            }
        };
    }

    private static ArchCondition<? super JavaClass> havePercentMethodCoverage(double coverage) {
        return new ArchCondition<>("have most their methods covered") {
            @Override
            public void check(JavaClass clasz, ConditionEvents events) {
                final String className = clasz.getSimpleName();
                if (Arrays.asList(excludedClasses).contains(className)) {
                    return;
                }

                final List<JavaClass> equivalentTestClasses = getEquivalentTestClasses(clasz);

                if (!equivalentTestClasses.isEmpty()) {
                    final List<String> classMethods =
                            getPublicNonStaticMethodsOfClass(clasz).stream().map(JavaMember::getName).toList();
                    final Set<String> testMethods =
                            equivalentTestClasses.stream()
                                    .map(CoverageTests::getPublicNonStaticMethodsOfClass)
                                    .flatMap(List::stream)
                                    .map(JavaMember::getName)
                                    .collect(Collectors.toSet());

                    final List<String> missingMethods =
                            classMethods.stream()
                                    .filter(method -> !testMethods.contains(method))
                                    .collect(Collectors.toList());

                    final int claszMethodsSize = classMethods.size();
                    final double percentMissing = (double) missingMethods.size() / claszMethodsSize;
                    if (1 - percentMissing < coverage) {
                        events.add(
                                new SimpleConditionEvent(
                                        clasz,
                                        false,
                                        "%s has just %.2f%% (%d/%d) of its methods covered [missing: %s]"
                                                .formatted(
                                                        className,
                                                        (1 - percentMissing) * 100,
                                                        (claszMethodsSize - missingMethods.size()),
                                                        claszMethodsSize,
                                                        String.join(", ", missingMethods))));
                    }
                }
            }
        };
    }

    private static List<JavaMethod> getPublicNonStaticMethodsOfClass(JavaClass testClass) {
        return testClass.getMethods().stream()
                .filter(m -> !m.getModifiers().contains(PRIVATE) && !m.getModifiers().contains(STATIC))
                .collect(Collectors.toList());
    }

    private static List<JavaClass> getEquivalentTestClasses(
            JavaClass item) {
        final String className = item.getSimpleName();
        final String testClassName = className + "Test";
        final String integrationTestClassName = className + "IntegrationTest";
        return item.getPackage().getClasses().stream()
                .filter(
                        c ->
                                c.getSimpleName().equals(testClassName)
                                        || c.getSimpleName().equals(integrationTestClassName))
                .collect(Collectors.toList());
    }

    private static final String[] excludedClasses = {
            "ExcludedService",
    };
}