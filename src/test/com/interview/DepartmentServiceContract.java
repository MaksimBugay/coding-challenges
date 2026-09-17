package com.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

abstract class DepartmentServiceContract {

  @FunctionalInterface
  protected interface Operation {
    Observation invoke(Input input);
  }

  protected abstract Operation operationFor(String scenarioId);

  @Test
  void test001AllowsTheDirectManager() {
    verify(
        "TEST-001",
        auth(2L, 2000L, standardHierarchy()),
        observed(true, state(2L, 1L, 2000L, "Engineering")));
  }

  @Test
  void test002AllowsManagersHigherInTheAncestry() {
    verify(
        "TEST-002",
        auth(3L, 1000L, standardHierarchy()),
        observed(true, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test003DeniesAnUnrelatedManager() {
    verify(
        "TEST-003",
        auth(3L, 4000L, standardHierarchy()),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test004RejectsAMoveWhenTheTargetIsUnauthorized() {
    verify(
        "TEST-004",
        move(3L, 5L, 4000L, twoRootHierarchy()),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test005RejectsAMoveWhenTheNewParentIsUnauthorized() {
    verify(
        "TEST-005",
        move(3L, 5L, 2000L, twoRootHierarchy()),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test006RejectsMovingARootUnderAnotherDepartment() {
    verify(
        "TEST-006",
        move(1L, 5L, 1000L, twoRootHierarchy()),
        observed(false, state(1L, null, 1000L, "Root A")));
  }

  @Test
  void test007RejectsMovingAChildToTheRoot() {
    verify(
        "TEST-007",
        move(3L, null, 1000L, standardHierarchy()),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test008RejectsSelfParenting() {
    verify(
        "TEST-008",
        move(3L, 3L, 3000L, standardHierarchy()),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test009RejectsMovingBelowADescendant() {
    verify(
        "TEST-009",
        move(2L, 3L, 1000L, standardHierarchy()),
        observed(false, state(2L, 1L, 2000L, "Engineering")));
  }

  @Test
  void test010SupportsAValidMoveAcrossSeparateRoots() {
    verify(
        "TEST-010",
        move(3L, 5L, 1000L, twoRootHierarchy()),
        observedAfterSave(state(3L, 5L, 3000L, "Platform")));
  }

  @Test
  void test011ChangesOnlyTheParentAndSavesTheSameTargetOnce() {
    verify(
        "TEST-011",
        move(3L, 6L, 1000L, sameRootHierarchy()),
        observedAfterSave(state(3L, 6L, 3000L, "Platform")));
  }

  @Test
  void test012RejectsANullDepartmentId() {
    verify(
        "TEST-012",
        move(null, 5L, 1000L, twoRootHierarchy()),
        observed(false, null));
  }

  @Test
  void test013RejectsNullAuthorizationInputs() {
    verify(
        "TEST-013",
        auth(null, 1000L, standardHierarchy()),
        observed(false, null));
    verify(
        "TEST-013-MANAGER",
        auth(3L, null, standardHierarchy()),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test014RejectsAMissingTarget() {
    verify(
        "TEST-014",
        move(999L, 5L, 1000L, twoRootHierarchy()),
        observed(false, null));
  }

  @Test
  void test015RejectsAMissingNewParent() {
    verify(
        "TEST-015",
        move(3L, 999L, 1000L, standardHierarchy()),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test016RejectsAMissingAncestor() {
    Map<Long, DepartmentSpec> hierarchy = Map.of(
        3L, department(3L, 999L, 3000L, "Platform"));

    verify(
        "TEST-016",
        auth(3L, 1000L, hierarchy),
        observed(false, state(3L, 999L, 3000L, "Platform")));
  }

  @Test
  void test017TerminatesAndRejectsCyclicAncestry() {
    Map<Long, DepartmentSpec> hierarchy = Map.of(
        2L, department(2L, 3L, 2000L, "Engineering"),
        3L, department(3L, 2L, 3000L, "Platform"));

    assertTimeoutPreemptively(
        Duration.ofSeconds(1),
        () -> verify(
            "TEST-017",
            auth(3L, 1000L, hierarchy),
            observed(false, state(3L, 2L, 3000L, "Platform"))));
  }

  @Test
  void test018RejectsANullManagerForAMove() {
    verify(
        "TEST-018",
        move(3L, 5L, null, twoRootHierarchy()),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test019RejectsANewParentWithAMissingAncestor() {
    Map<Long, DepartmentSpec> hierarchy = Map.of(
        1L, department(1L, null, 1000L, "Root A"),
        2L, department(2L, 1L, 2000L, "Engineering"),
        3L, department(3L, 2L, 3000L, "Platform"),
        5L, department(5L, 999L, 1000L, "Detached"));

    verify(
        "TEST-019",
        move(3L, 5L, 1000L, hierarchy),
        observed(false, state(3L, 2L, 3000L, "Platform")));
  }

  @Test
  void test020RejectsANewParentWithCyclicAncestry() {
    Map<Long, DepartmentSpec> hierarchy = Map.of(
        1L, department(1L, null, 1000L, "Root A"),
        2L, department(2L, 1L, 2000L, "Engineering"),
        3L, department(3L, 2L, 3000L, "Platform"),
        5L, department(5L, 6L, 1000L, "Cycle A"),
        6L, department(6L, 5L, 6000L, "Cycle B"));

    assertTimeoutPreemptively(
        Duration.ofSeconds(1),
        () -> verify(
            "TEST-020",
            move(3L, 5L, 1000L, hierarchy),
            observed(false, state(3L, 2L, 3000L, "Platform"))));
  }

  private void verify(String id, Input input, Observation expected) {
    Observation actual = operationFor(id).invoke(input);
    assertEquals(expected, actual, id);
  }

  private static Input auth(
      Long departmentId, Long managerId, Map<Long, DepartmentSpec> hierarchy) {
    return new Input(Action.AUTHORIZE, departmentId, null, managerId, hierarchy);
  }

  private static Input move(
      Long departmentId,
      Long newParentId,
      Long managerId,
      Map<Long, DepartmentSpec> hierarchy) {
    return new Input(Action.MOVE, departmentId, newParentId, managerId, hierarchy);
  }

  private static Map<Long, DepartmentSpec> standardHierarchy() {
    return Map.of(
        1L, department(1L, null, 1000L, "Root A"),
        2L, department(2L, 1L, 2000L, "Engineering"),
        3L, department(3L, 2L, 3000L, "Platform"));
  }

  private static Map<Long, DepartmentSpec> twoRootHierarchy() {
    return Map.of(
        1L, department(1L, null, 1000L, "Root A"),
        2L, department(2L, 1L, 2000L, "Engineering"),
        3L, department(3L, 2L, 3000L, "Platform"),
        4L, department(4L, null, 1000L, "Root B"),
        5L, department(5L, 4L, 4000L, "Sales"));
  }

  private static Map<Long, DepartmentSpec> sameRootHierarchy() {
    return Map.of(
        1L, department(1L, null, 1000L, "Root A"),
        2L, department(2L, 1L, 2000L, "Engineering"),
        3L, department(3L, 2L, 3000L, "Platform"),
        6L, department(6L, 1L, 6000L, "Operations"));
  }

  private static DepartmentSpec department(
      Long id, Long parentId, Long managerId, String title) {
    return new DepartmentSpec(id, parentId, managerId, title);
  }

  private static DepartmentState state(
      Long id, Long parentId, Long managerId, String title) {
    return new DepartmentState(id, parentId, managerId, title);
  }

  private static Observation observed(boolean result, DepartmentState target) {
    return new Observation(result, target, 0, false, true);
  }

  private static Observation observedAfterSave(DepartmentState target) {
    return new Observation(true, target, 1, true, true);
  }

  protected enum Action {
    AUTHORIZE,
    MOVE
  }

  protected record DepartmentSpec(Long id, Long parentId, Long managerId, String title) {}

  protected record DepartmentState(Long id, Long parentId, Long managerId, String title) {}

  protected record Input(
      Action action,
      Long departmentId,
      Long newParentId,
      Long managerId,
      Map<Long, DepartmentSpec> hierarchy) {}

  protected record Observation(
      boolean result,
      DepartmentState target,
      int saveCount,
      boolean savedSameTarget,
      boolean nonTargetDepartmentsUnchanged) {}
}
