package ch.martinelli;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyAnalyzer;
import guru.nidi.codeassert.dependency.DependencyResult;
import guru.nidi.codeassert.dependency.DependencyRule;
import guru.nidi.codeassert.dependency.DependencyRuler;
import guru.nidi.codeassert.dependency.DependencyRules;
import org.junit.jupiter.api.Test;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.matchesRulesExactly;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArchitectureCodeAssertTest {

    @Test
    public void checkArchitecture() {

        AnalyzerConfig analyzerConfig = AnalyzerConfig.maven().main();

        // Class name must match package name in upper case
        class ChMartinelli extends DependencyRuler {

            DependencyRule api, service, repository, entity;

            @Override
            public void defineRules() {
                base().mayUse(base().allSubOf());
                api.mayUse(service);
                api.mayUse(entity);
                service.mayUse(repository);
                service.mayUse(entity);
                repository.mayUse(entity);
            }
        }

        DependencyRules rules = DependencyRules.denyAll()
                .withRelativeRules(new ChMartinelli())
                .withExternals("java.*", "javax.*", "org.springframework.*");

        DependencyResult result = new DependencyAnalyzer(analyzerConfig).rules(rules).analyze();

        assertThat(result, matchesRulesExactly());
    }

}
