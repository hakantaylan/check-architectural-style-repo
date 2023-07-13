package com.example.archunit.util;

import com.example.archunit.persistence.IPersistenceSession;
import com.example.archunit.persistence.PersistenceSession;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.List;
import java.util.Optional;

public class ArchUnitUtil {

    public static DescribedPredicate<JavaCodeUnit> haveOpenedSession = new DescribedPredicate<>("has opened session") {
        @Override
        public boolean test(JavaCodeUnit javaCodeUnit) {
            return javaCodeUnit.getMethodCallsFromSelf().stream()
                    .anyMatch(ArchUnitUtil::hasCreatedPersistenceSession);
        }
    };

    public static ArchCondition<JavaCodeUnit> closeSession() {
        return new ArchCondition<>("call close session") {
            @Override
            public void check(JavaCodeUnit codeUnit, ConditionEvents conditionEvents) {
                boolean hasClosedPersistenceSession = codeUnit.getMethodCallsFromSelf().stream()
                        .anyMatch(ArchUnitUtil::isCloseInvoked);
                if (!hasClosedPersistenceSession) {
                    conditionEvents.add(SimpleConditionEvent.violated(codeUnit, codeUnit.getFullName() + " metodu içerisinde session açıldı fakat kapatılmadı!"));
                }
            }
        };
    }

    public static ArchCondition<JavaCodeUnit> delegateSessionClose() {
        return new ArchCondition<>("delegate session close to another method") {
            @Override
            public void check(JavaCodeUnit codeUnit, ConditionEvents conditionEvents) {
                boolean closeDelegatedToOtherMethod = codeUnit.getMethodCallsFromSelf().stream()
                        .anyMatch(ArchUnitUtil::isClosedInAnotherMethod);
                if (!closeDelegatedToOtherMethod) {
                    conditionEvents.add(SimpleConditionEvent.violated(codeUnit, codeUnit.getFullName() + " metodu içerisinde session açıldı fakat kapatılmadı!"));
                }
            }
        };
    }

    private static boolean hasCreatedPersistenceSession(JavaMethodCall methodCall) {
        boolean isStatic = methodCall.getTarget().resolveMember().stream().anyMatch(m -> m.getModifiers().contains(JavaModifier.STATIC));
        return isStatic && methodCall.getTarget().getOwner().isEquivalentTo(PersistenceSession.class) &&
                methodCall.getName().equals("getPersistenceSession") && methodCall.getTarget().getParameterTypes().isEmpty();
    }

    private static boolean hasCreatedPersistenceSessionv2(JavaMethodCall methodCall) {
        Optional<JavaMethod> targetMethod = methodCall.getTarget().resolveMember();
        if (targetMethod.isPresent()) {
            boolean isStatic = targetMethod.get().getModifiers().contains(JavaModifier.STATIC);
            return isStatic && methodCall.getTarget().getOwner().isEquivalentTo(PersistenceSession.class) &&
                    methodCall.getName().equals("getPersistenceSession") && methodCall.getTarget().getParameterTypes().isEmpty();
        } else
            return false;
    }

    private static boolean isCloseInvoked(JavaMethodCall methodCall) {
        return methodCall.getTarget().getOwner().isAssignableTo(IPersistenceSession.class)
                && methodCall.getName().equals("close") && methodCall.getTarget().getParameterTypes().isEmpty();

    }

    private static boolean isClosedInAnotherMethod(JavaMethodCall methodCall) {
        boolean hasPsParameter = methodCall.getTarget().getRawParameterTypes().stream().anyMatch(i -> i.isAssignableTo(IPersistenceSession.class));
        if (hasPsParameter) {
            JavaMethod javaMethod = methodCall.getTarget().resolveMember().orElse(null);
            if (javaMethod != null) {
                return javaMethod.getMethodCallsFromSelf().stream().anyMatch(ArchUnitUtil::isCloseInvoked);
            }
        }
        return false;
    }

    public static DescribedPredicate<JavaClass> thatNotInIgnoreList(List<String> ignoredClasses) {
        return new DescribedPredicate<>("not in ignore list") {
            @Override
            public boolean test(JavaClass javaClass) {
                if (ignoredClasses != null) {
                    return !ignoredClasses.contains(javaClass.getFullName());
                }
                return true;
            }
        };
    }

    public static DescribedPredicate<JavaClass> thatInIgnoreList(List<String> ignoredClasses) {
        return new DescribedPredicate<>("that in ignore list") {
            @Override
            public boolean test(JavaClass javaClass) {
                if (ignoredClasses != null) {
                    return ignoredClasses.contains(javaClass.getFullName());
                }
                return true;
            }
        };
    }

}
