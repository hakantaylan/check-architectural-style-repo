package com.example.archunit;

import com.example.archunit.persistence.IPersistenceSession;
import com.example.archunit.persistence.PersistenceSession;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static com.example.archunit.util.ArchUnitUtil.*;
import static com.tngtech.archunit.base.DescribedPredicate.doNot;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.core.domain.properties.HasParameterTypes.Predicates.rawParameterTypes;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.have;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.codeUnits;


public class PersistenceSessionTest2 {

    private static List<String> ignoredFileList = new LinkedList<>();

    @Test
    public void classes_that_opens_session_must_close_them() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages("com.example.archunit");

        ArchRule ruleToCheck = codeUnits().that().areDeclaredInClassesThat().resideInAPackage("com.example.archunit..")
                .and(doNot(have(rawParameterTypes(PersistenceSession.class, IPersistenceSession.class))))
                .and(doNot(have(owner(thatInIgnoreList(ignoredFileList)))))
                .and(haveOpenedSession)
                .should(closeSession())
                .orShould(delegateSessionClose());

        ruleToCheck.check(importedClasses);
    }
}
