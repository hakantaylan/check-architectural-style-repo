package com.example.archunit.exceptionrelated;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = {"com.example.archunit.exceptionrelated", "com.example.archunit.outerpackage"})
public class CheckIfCorrectClassIsImported {

    DescribedPredicate<JavaAccess<?>> isForeignMessageClassPredicate =
            new DescribedPredicate<>("target is a foreign message class") {
                @Override
                public boolean test(JavaAccess<?> access) {
                    JavaClass targetClass = access.getTarget().getOwner();
                    if ("Message".equals(targetClass.getSimpleName())) {
                        JavaClass callerClass = access.getOwner().getOwner();
                        return !targetClass.getPackageName().equals(callerClass.getPackageName());
                    }
                    return false;
                }
            };

    @ArchTest
    ArchRule rule =
            noClasses().should().accessTargetWhere(isForeignMessageClassPredicate);
}
