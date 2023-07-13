package com.example.archunit.util;

import com.example.archunit.architecture.CustomApiImportOption;
import com.example.archunit.persistence.IPersistenceSession;
import com.example.archunit.persistence.PersistenceSession;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.archunit.util.ArchUnitUtil.*;
import static com.tngtech.archunit.base.DescribedPredicate.doNot;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.core.domain.properties.HasParameterTypes.Predicates.rawParameterTypes;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.have;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.codeUnits;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        logger.info("Uygulama" + Arrays.toString(args) + " parametreleri ile çalışıyor...");
        Map<String, String> configMap = parseArguments(args);
        if (configMap.get("h") != null || configMap.get("help") != null) {
            printHelp();
            return;
        }
        String pathToScan = configMap.get("scanPath");
        if (pathToScan == null) {
            logger.error("Taranacak jar'ları içeren path scanPath parametresi ile belirtilmelidir!");
            System.exit(1);
        }
        List<String> ignoredFileList = new LinkedList<>();
        String ignoredFileName = configMap.get("ignoreFile");
        if (ignoredFileName != null && !ignoredFileName.isEmpty())
            ignoredFileList = parseAndGetFilesToIgnore(ignoredFileName);
        Path path = Path.of(pathToScan);
        List<JarFile> jarFiles = null;
        try (Stream<Path> stream = Files.list(path)) {
            jarFiles = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::toString)
                    .filter(i -> i.endsWith(".jar"))
                    .map(Main::convertToJarFile)
                    .collect(Collectors.toList());

        }

        JavaClasses javaClasses = new ClassFileImporter()
                .withImportOption(new ImportOnlyRelatedClasses())
                .importJars(jarFiles.toArray(new JarFile[0]));

        ArchRule ruleToCheck = codeUnits().that().areDeclaredInClassesThat().resideInAPackage("com.example.archunit")
                .and(doNot(have(rawParameterTypes(PersistenceSession.class, IPersistenceSession.class))))
                .and(doNot(have(owner(thatInIgnoreList(ignoredFileList)))))
                .and(haveOpenedSession)
                .should(closeSession())
                .orShould(delegateSessionClose());
//        ruleToCheck.check(javaClasses);
        EvaluationResult evaluationResult = ruleToCheck.evaluate(javaClasses);
        logger.info("------------------------------------------------------------------------------------------------");
        logger.info(ruleToCheck.getDescription() + " kurallarına göre taranan sınıflar içerisinde");
        logger.info(evaluationResult.getFailureReport().getDetails().size() + " adet hata bulundu");
        evaluationResult.getFailureReport().getDetails().forEach(logger::info);
        logger.info("------------------------------------------------------------------------------------------------");
    }

    private static List<String> parseAndGetFilesToIgnore(String ignoredFileName) throws IOException {
        try {
            return Files.readAllLines(Paths.get(ignoredFileName));
        } catch (IOException e) {
            logger.error("Ignorefile okunurken bir hata oluştu!");
            throw e;
        }
    }

    private static JarFile convertToJarFile(String fileName) {
        try {
            return new JarFile(fileName);
        } catch (IOException e) {
            return sneakyThrow(e);
        }
    }

    private static Map<String, String> parseArguments(String... args) {
        Map<String, String> configMap = new HashMap<>();
        for (String arg : args) {
            String[] splitted = arg.split("=");
            if (splitted.length > 1)
                configMap.put(splitted[0].replace("-", ""), splitted[1]);
            else
                configMap.put(splitted[0].replace("-", ""), "");
        }
        return configMap;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Exception, R> R sneakyThrow(Exception e) throws E {
        throw (E) e;
    }

    private static void printHelp() {
        logger.info("------------------------------------------------------------------------------------------------");
        logger.info("Bu uygulama verilen path'deki jar'ları tarayarak uygun sınıflar içerisindeki tüm metodlarda" +
                " PersistenceSession.getPersistenceSession() metodunu arar.");
        logger.info("Bu sayede session oluşturmuş metodların session'ı kapatıp kapatmadığını analiz eder.");
        logger.info("-scanPath parametresi ile ilgili jar'ların bulunduğu dizin belirtilir.");
        logger.info("-ignoreFile opsiyonel parametresi ile taramadan muaf tutulaacak sınıfların listesini içeren ignore file'ın full path'i belirtilir.");
        logger.info("Fazla sayıda jar'ı taramak istediğinizde OutOfMemoryException hatası alırsanız" +
                " -Xmx parametresi ile uygulamaya ayrılan memory miktarını arttırabilirsiniz.");
        logger.info("Örnek komut java -Xmx4G -jar persistence-session-checker.jar -scanPath=path-to-scan");
        logger.info("------------------------------------------------------------------------------------------------");
    }
}
