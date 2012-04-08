/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.TestHelper;
import com.happydroids.server.GameUpdate;
import com.happydroids.server.TestHappyDroidService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.happydroids.sparky.Expect.expect;

@RunWith(HappyDroidTestRunner.class)
public class GameUpdateCheckerTest {
  public TemporaryFolder temp = new TemporaryFolder();
  private GameUpdateChecker updateChecker;

  @Before
  public void setUp() throws IOException {
    temp.create();
    if (!temp.getRoot().exists()) {
      throw new RuntimeException("Game storage root does not exist?");
    }

    updateChecker = new GameUpdateChecker(temp.getRoot());
  }

  @After
  public void tearDown() {
    temp.delete();
  }

  @Test
  public void parseLocalGameJar_shouldReturnGitSHA_whenLocalJarExists() throws IOException {
    TestHelper.makeFakeGameJar(temp.newFile("DroidTowers.jar"), "0.10.1", "32kne34");
    updateChecker.parseLocalGameJar();

    expect(updateChecker.getLocalGameJarVersionSHA()).toEqual("32kne34");
  }

  @Test
  public void selectUpdates_shouldReturnNewestFullJar_whenNoLocalJarExists() throws IOException {
    TestHelper.queueApiResponse("fixture_game_updater_multiple_updates.json");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();

    expect(updateChecker.getUpdates().isEmpty()).toBeFalse();
    expect(updateChecker.selectUpdates()).toContainExactly(updateChecker.getUpdates().getObjects().get(0));
    expect(updateChecker.shouldUsePatches()).toBeFalse();
    expect(updateChecker.hasCurrentVersion()).toBeFalse();
  }

  @Test
  public void selectUpdates_shouldReturnNewestFullJar_whenNoLocalJarWithValidGitSHAExists() throws IOException {
    TestHelper.queueApiResponse("fixture_game_updater_multiple_updates.json");
    TestHelper.makeFakeGameJar(temp.newFile("DroidTowers.jar"), "0.10.1", "32kne3423kjlsdf");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();

    expect(updateChecker.selectUpdates()).toContainInOrder(updateChecker.getUpdates().getObjects().get(0));
    expect(updateChecker.shouldUsePatches()).toBeFalse();
    expect(updateChecker.hasCurrentVersion()).toBeFalse();
  }

  @Test
  public void selectUpdates_shouldReturnListOfPatches_whenLocalJarWithValidGitSHAExists() throws IOException {
    TestHelper.queueApiResponse("fixture_game_updater_multiple_updates.json");
    TestHelper.makeFakeGameJar(temp.newFile("DroidTowers.jar"), "0.10.1", "32kne34");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();

    List<GameUpdate> gameUpdates = updateChecker.getUpdates().getObjects();
    List<GameUpdate> selectedUpdates = updateChecker.selectUpdates();
    expect(selectedUpdates).toContainInOrder(gameUpdates.get(1), gameUpdates.get(0));
    expect(updateChecker.shouldUsePatches()).toBeTrue();
    expect(updateChecker.hasCurrentVersion()).toBeFalse();
  }

  @Test
  public void selectUpdates_shouldReturnFullReleaseJar_whenPatchForLatestReleaseDoesNotExist() throws IOException {
    TestHelper.queueApiResponse("fixture_game_update_checker_latest_release_has_no_patch.json");
    TestHelper.makeFakeGameJar(temp.newFile("DroidTowers.jar"), "0.10.1", "32kne34");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();

    List<GameUpdate> gameUpdates = updateChecker.getUpdates().getObjects();
    List<GameUpdate> selectedUpdates = updateChecker.selectUpdates();
    expect(selectedUpdates).toContainInOrder(gameUpdates.get(0));
    expect(updateChecker.shouldUsePatches()).toBeFalse();
    expect(updateChecker.hasCurrentVersion()).toBeFalse();
  }

  @Test
  public void hasCurrentVersion_shouldReturnTrue_whenGameJarGitSHAIsLatest() throws IOException {
    TestHelper.queueApiResponse("fixture_game_updater_multiple_updates.json");
    TestHelper.makeFakeGameJar(temp.newFile("DroidTowers.jar"), "0.10.50", "19093ea");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();

    expect(updateChecker.hasCurrentVersion()).toBeTrue();
  }

  @Test
  public void hasCurrentVersion_shouldReturnTrue_whenGameJarExistsButUpdateServerIsUnreachable() throws IOException {
    TestHappyDroidService.disableNetworkConnection();
    TestHelper.makeFakeGameJar(temp.newFile("DroidTowers.jar"), "0.10.50", "19093ea");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();

    expect(updateChecker.hasCurrentVersion()).toBeTrue();
  }
}

