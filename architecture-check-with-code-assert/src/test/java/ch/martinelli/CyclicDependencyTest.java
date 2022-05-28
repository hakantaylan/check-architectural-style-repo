package ch.martinelli;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyAnalyzer;
import org.junit.jupiter.api.Test;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasNoCycles;
import static org.hamcrest.MatcherAssert.assertThat;

public class CyclicDependencyTest {

    @Test
    public void verifyThatThereAreNoCyclicDependencies() {
        AnalyzerConfig analyzerConfig = AnalyzerConfig.maven().main();

        assertThat(new DependencyAnalyzer(analyzerConfig).analyze(), hasNoCycles());
    }
}
