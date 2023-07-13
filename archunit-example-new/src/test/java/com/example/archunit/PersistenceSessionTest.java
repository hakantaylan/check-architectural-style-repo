package com.example.archunit;

import com.example.archunit.persistence.IPersistenceSession;
import com.example.archunit.persistence.PersistenceSession;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.LinkedList;
import java.util.List;

import static com.example.archunit.util.ArchUnitUtil.*;
import static com.example.archunit.util.ArchUnitUtil.delegateSessionClose;
import static com.tngtech.archunit.base.DescribedPredicate.doNot;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.core.domain.properties.HasParameterTypes.Predicates.rawParameterTypes;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.have;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.codeUnits;

@AnalyzeClasses(packages = "com.example.archunit")
public class PersistenceSessionTest {

    private static List<String> ignoredFileList = new LinkedList<>();

    @ArchTest
    public ArchRule ruleToCheck = codeUnits().that().areDeclaredInClassesThat().resideInAPackage("com.example.archunit..")
            .and(doNot(have(rawParameterTypes(PersistenceSession.class, IPersistenceSession.class))))
            .and(doNot(have(owner(thatInIgnoreList(ignoredFileList)))))
            .and(haveOpenedSession)
            .should(closeSession())
            .orShould(delegateSessionClose());
}
