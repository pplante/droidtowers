/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.SparkyTestHelper;
import com.happydroids.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.happydroids.sparky.Expect.expect;

@RunWith(HappyDroidTestRunner.class)
public class GameUpdateDownloaderTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private GameUpdateChecker updateChecker;
  private GameUpdateDownloader downloader;
  private File gameFile;
  private File tempGameStorage;

  @Before
  public void setUp() throws IOException {
    TestHelper.queueApiResponseFromFixture("/api/v1/gameupdate/", "fixture_game_updater_multiple_updates.json");

    makeFakeJarDownloadResponse("/public/uploads/attachments/patch.jar", "0.10.46", "c82b6e1");
    makeFakeJarDownloadResponse("/public/uploads/attachments/patch10.50.jar", "0.10.50", "19093ea");

    temp.create();
    tempGameStorage = temp.newFolder("tempGameStorage");
    gameFile = new File(tempGameStorage, "DroidTowers.jar");
    downloader = new GameUpdateDownloader(tempGameStorage, gameFile);
  }

  @After
  public void tearDown() {
    gameFile.delete();
    tempGameStorage.delete();
    temp.delete();
  }

  @Test
  public void checkForUpdates_shouldNotDownloadFiles() throws IOException {
    expect(tempGameStorage.list().length).toEqual(0);

    downloader.checkForUpdates();

    expect(downloader.updateAvailable()).toBeTrue();
    expect(tempGameStorage.list().length).toEqual(0);
  }

  @Test
  public void fetchUpdates_shouldImmediatelyCallPostDownloadRunnable_whenLocalGameJarIsLatest() throws IOException {
    SparkyTestHelper.makeFakeGameJar(gameFile, "0.10.50", "19093ea");

    AssertableRunnable postDownloadRunnable = new AssertableRunnable();

    downloader.setPostDownloadRunnable(postDownloadRunnable);
    downloader.checkForUpdates();
    downloader.fetchUpdates();

    expect(postDownloadRunnable.hasBeenCalled()).toBeTrue();
  }

  @Test
  public void fetchUpdates_shouldCallPostDownloadRunnable_whenUpdatesAreFinished() throws IOException {
    SparkyTestHelper.makeFakeGameJar(gameFile, "0.10.43", "OLDEST_SHA");

    AssertableRunnable postDownloadRunnable = new AssertableRunnable();

    downloader.setPostDownloadRunnable(postDownloadRunnable);
    downloader.checkForUpdates();
    downloader.fetchUpdates();

    expect(postDownloadRunnable.hasBeenCalled()).toBeTrue();
  }

  @Test
  public void fetchUpdates_shouldDownloadAllUpdatesAndJoinThem() throws Exception {

    SparkyTestHelper.makeFakeGameJar(gameFile, "0.10.43", "OLDEST_SHA");

    downloader.checkForUpdates();
    downloader.fetchUpdates();

    expect(TestHelper.getQueuedRequests()).toBeEmpty();

    JarFile patchedJar = new JarFile(gameFile, false);
    Manifest manifest = patchedJar.getManifest();
    Attributes mainAttributes = manifest.getMainAttributes();

    expect(mainAttributes.getValue("Game-Version")).toEqual("0.10.50");
    expect(mainAttributes.getValue("Game-Version-SHA")).toEqual("19093ea");
  }

  private void makeFakeJarDownloadResponse(String uri, String gameVersion, String versionSha) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SparkyTestHelper.makeFakeGameJar(outputStream, gameVersion, versionSha);
    TestHelper.queueApiResponse(uri, outputStream.toByteArray());
  }
}
