package ch.martinelli;

import ch.martinelli.api.ProductController;
import edu.umd.cs.findbugs.Priorities;
import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.config.In;
import guru.nidi.codeassert.dependency.DependencyRules;
import guru.nidi.codeassert.findbugs.BugCollector;
import guru.nidi.codeassert.findbugs.FindBugsAnalyzer;
import guru.nidi.codeassert.findbugs.FindBugsResult;
import guru.nidi.codeassert.pmd.PmdRuleset;
import org.junit.jupiter.api.Test;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasNoBugs;
import static org.hamcrest.MatcherAssert.assertThat;

public class FindBugsTest {

    @Test
    public void findBugs() {
        AnalyzerConfig config = AnalyzerConfig.maven().main();

        BugCollector collector = new BugCollector().maxRank(17).minPriority(Priorities.NORMAL_PRIORITY)
                .just(In.everywhere().ignore("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"))
                .because("It's checked and OK like this",
                        In.classes(DependencyRules.class, PmdRuleset.class).ignore("DP_DO_INSIDE_DO_PRIVILEGED"),
                        In.classes("*Test", "Rulesets")
                                .and(In.classes("ClassFileParser").withMethods("doParse"))
                                .ignore("URF_UNREAD_FIELD"))
                .just(In.classes(ProductController.class).ignore("ENTITY_LEAK"));

        FindBugsResult result = new FindBugsAnalyzer(config, collector).analyze();
        assertThat(result, hasNoBugs());
    }
}
