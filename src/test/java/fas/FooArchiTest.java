package fas;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.jupiter.api.Test;

import fas.persistence.Dao;

public class FooArchiTest {
  JavaClasses importedClasses = new ClassFileImporter().importPackages("fas");

  @Test
  public void verificaDependenciaParaCamadaDePersistencia() {
    ArchRule rule = classes().that().resideInAPackage("..persistence..").should().onlyHaveDependentClassesThat()
        .resideInAnyPackage("..persistence", "..service..");

    rule.check(importedClasses);
  }

  @Test
  public void verificaDependenciaDaCamadaDePersistencia() {
    ArchRule rule = noClasses().that().resideInAPackage("..persistence..").should().dependOnClassesThat()
        .resideInAnyPackage("..service..");

    rule.check(importedClasses);
  }

  @Test
  public void verificaNamesClassesCamadaDePersistencia() {
    ArchRule rule = classes().that().haveSimpleNameEndingWith("Dao").should().resideInAnyPackage("..persistence..");

    rule.check(importedClasses);
  }

  @Test
  public void verificarImplementacaoInterfaceDao() {
    ArchRule rule = classes().that().implement(Dao.class).should().haveSimpleNameEndingWith("Dao");

    rule.check(importedClasses);
  }

  @Test
  public void verificarDependenciaCiclicas() {
    ArchRule rule = slices().matching("fas.(*)..").should().beFreeOfCycles();

    rule.check(importedClasses);
  }

  @Test
  public void verificarViolacaoCamadas() {
    ArchRule rule = layeredArchitecture().layer("Service").definedBy("..service..").layer("Persistence")
        .definedBy("..persistence..")

        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service");

    rule.check(importedClasses);
  }

}
