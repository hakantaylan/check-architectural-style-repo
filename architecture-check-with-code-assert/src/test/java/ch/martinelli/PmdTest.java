package ch.martinelli;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.config.In;
import guru.nidi.codeassert.dependency.Dependencies;
import guru.nidi.codeassert.dependency.DependencyRule;
import guru.nidi.codeassert.pmd.CpdAnalyzer;
import guru.nidi.codeassert.pmd.CpdMatchCollector;
import guru.nidi.codeassert.pmd.PmdAnalyzer;
import guru.nidi.codeassert.pmd.PmdViolationCollector;
import net.sourceforge.pmd.RulePriority;
import org.junit.jupiter.api.Test;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasNoCodeDuplications;
import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasNoPmdViolations;
import static guru.nidi.codeassert.pmd.PmdRulesets.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class PmdTest {

    private final AnalyzerConfig config = AnalyzerConfig.maven().main();

    @Test
    public void pmd() {
        PmdViolationCollector collector = new PmdViolationCollector().minPriority(RulePriority.MEDIUM)
                .because("It's not severe and occurs very often",
                        In.everywhere().ignore("MethodArgumentCouldBeFinal"),
                        In.locs("JavaClassBuilder#from", "FindBugsMatchers").ignore("AvoidInstantiatingObjectsInLoops"))
                .because("it'a an enum",
                        In.classes("SignatureParser").ignore("SwitchStmtsShouldHaveDefault"))
                .just(In.classes("*Test").ignore("TooManyStaticImports"));

        PmdAnalyzer analyzer = new PmdAnalyzer(config, collector).withRulesets(
                basic(), braces(), design(), empty(), optimizations(),
                codesize().excessiveMethodLength(40).tooManyMethods(30));

        assertThat(analyzer.analyze(), hasNoPmdViolations());
    }

    @Test
    public void cpd() {
        CpdMatchCollector collector = new CpdMatchCollector()
                .because("equals",
                        In.everywhere().ignore("public boolean equals(Object o) {"))
                .just(
                        In.classes(DependencyRule.class, Dependencies.class).ignoreAll(),
                        In.classes("SignatureParser").ignoreAll());

        CpdAnalyzer analyzer = new CpdAnalyzer(config, 20, collector);

        assertThat(analyzer.analyze(), hasNoCodeDuplications());
    }
}
