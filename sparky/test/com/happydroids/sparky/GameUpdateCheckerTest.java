/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.TestHelper;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.GameUpdate;
import com.happydroids.server.HappyDroidServiceCollection;
import com.happydroids.server.TestHappyDroidService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.happydroids.sparky.Expect.expect;

@RunWith(HappyDroidTestRunner.class)
public class GameUpdateCheckerTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private GameUpdateChecker updateChecker;
  private File jarFile;
  private ApiCollectionRunnable<HappyDroidServiceCollection<GameUpdate>> runnable;

  @Before
  public void setUp() throws IOException {
    temp.create();
    if (!temp.getRoot().exists()) {
      throw new RuntimeException("Game storage root does not exist?");
    }

    jarFile = temp.newFile("DroidTowers.jar");
    updateChecker = new GameUpdateChecker(temp.getRoot(), jarFile);
  }

  @After
  public void tearDown() {
    temp.delete();
  }

  @Test
  public void parseLocalGameJar_shouldReturnGitSHA_whenLocalJarExists() throws IOException {
    TestHelper.makeFakeGameJar(jarFile, "0.10.1", "OLDEST_SHA");
    updateChecker.parseLocalGameJar();

    expect(updateChecker.getLocalGameJarVersionSHA()).toEqual("OLDEST_SHA");
  }

  @Test
  public void selectUpdates_shouldReturnNewestFullJar_whenNoLocalJarExists() throws IOException {
    TestHelper.queueApiResponse("/api/v1/gameupdate/", "fixture_game_updater_multiple_updates.json");

    updateChecker.parseLocalGameJar();
    runnable = new ApiCollectionRunnable<HappyDroidServiceCollection<GameUpdate>>();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();

    expect(updateChecker.getUpdates().isEmpty()).toBeFalse();
    expect(updateChecker.getPendingUpdates()).toContainExactly(updateChecker.getUpdates().getObjects().get(0));
    expect(updateChecker.shouldUsePatches()).toBeFalse();
    expect(updateChecker.hasCurrentVersion()).toBeFalse();
  }

  @Test
  public void selectUpdates_shouldReturnNewestFullJar_whenNoLocalJarWithValidGitSHAExists() throws IOException {
    TestHelper.queueApiResponse("/api/v1/gameupdate/", "fixture_game_updater_multiple_updates.json");
    TestHelper.makeFakeGameJar(jarFile, "0.10.1", "BULLSHIT SHA");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();

    expect(updateChecker.getPendingUpdates()).toContainInOrder(updateChecker.getUpdates().getObjects().get(0));
    expect(updateChecker.shouldUsePatches()).toBeFalse();
    expect(updateChecker.hasCurrentVersion()).toBeFalse();
  }

  @Test
  public void selectUpdates_shouldReturnListOfPatches_whenLocalJarWithValidGitSHAExists() throws IOException {
    TestHelper.queueApiResponse("/api/v1/gameupdate/", "fixture_game_updater_multiple_updates.json");
    TestHelper.makeFakeGameJar(jarFile, "0.10.1", "OLDEST_SHA");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();

    List<GameUpdate> gameUpdates = updateChecker.getUpdates().getObjects();
    List<GameUpdate> selectedUpdates = updateChecker.getPendingUpdates();
    expect(selectedUpdates).toContainInOrder(gameUpdates.get(1), gameUpdates.get(0));
    expect(updateChecker.shouldUsePatches()).toBeTrue();
    expect(updateChecker.hasCurrentVersion()).toBeFalse();
  }

  @Test
  public void selectUpdates_shouldReturnFullReleaseJar_whenPatchForLatestReleaseDoesNotExist() throws IOException {
    TestHelper.queueApiResponse("/api/v1/gameupdate/", "fixture_game_update_checker_latest_release_has_no_patch.json");
    TestHelper.makeFakeGameJar(jarFile, "0.10.1", "OLDEST_SHA");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();

    List<GameUpdate> gameUpdates = updateChecker.getUpdates().getObjects();
    List<GameUpdate> selectedUpdates = updateChecker.getPendingUpdates();
    expect(selectedUpdates).toContainInOrder(gameUpdates.get(0));
    expect(updateChecker.shouldUsePatches()).toBeFalse();
    expect(updateChecker.hasCurrentVersion()).toBeFalse();
  }

  @Test
  public void hasCurrentVersion_shouldReturnTrue_whenGameJarGitSHAIsLatest() throws IOException {
    TestHelper.queueApiResponse("/api/v1/gameupdate/", "fixture_game_updater_multiple_updates.json");
    TestHelper.makeFakeGameJar(jarFile, "0.10.50", "19093ea");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();

    expect(updateChecker.hasCurrentVersion()).toBeTrue();
  }

  @Test
  public void hasCurrentVersion_shouldReturnTrue_whenGameJarExistsButUpdateServerIsUnreachable() throws IOException {
    TestHappyDroidService.disableNetworkConnection();
    TestHelper.makeFakeGameJar(jarFile, "0.10.50", "19093ea");

    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();

    expect(updateChecker.hasCurrentVersion()).toBeTrue();
  }
}

